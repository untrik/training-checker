package edu.rutmiit.training_rest.service;

import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_checker_api_contract.exception.InvalidMetricTimestampException;
import edu.rutmiit.training_rest.event.MetricEventPublisher;
import edu.rutmiit.training_rest.storage.InMemoryStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetricService {
    private final InMemoryStorage storage;
    private final WorkoutService workoutService;
    private final MetricEventPublisher eventPublisher;

    public MetricService(InMemoryStorage storage, WorkoutService workoutService,
                         MetricEventPublisher eventPublisher) {
        this.storage = storage;
        this.workoutService = workoutService;
        this.eventPublisher = eventPublisher;
    }

    // TODO: добавить проверку по дате и времени
    public List<CardioMetricResponse> createMetrics(Long workoutId,
                                                    List<CardioMetricRequest> metricRequests) {
        WorkoutResponse workout = workoutService.findById(workoutId);
        List<CardioMetricResponse> responses = new ArrayList<>();
        for (CardioMetricRequest request : metricRequests) {
            if (request.timestamp().toInstant().isBefore(workout.getStartedAt().toInstant())) {
                throw new InvalidMetricTimestampException(workout.getStartedAt(), request.timestamp());
            }

            long id = storage.metricSequence.incrementAndGet();
            CardioMetricResponse response = CardioMetricResponse.builder()
                    .id(id)
                    .workoutId(workout.getId())
                    .timestamp(request.timestamp().withOffsetSameInstant(ZoneOffset.UTC))
                    .heartRate(request.heartRate())
                    .distanceKm(request.distanceKm())
                    .calories(request.calories())
                    .pace(request.pace())
                    .build();
            responses.add(response);
            storage.metrics.put(id, response);
            eventPublisher.publishCreated(response);
        }
        return responses;
    }

    public CardioMetricResponse createMetric(Long workoutId, CardioMetricRequest metricRequest) {
        WorkoutResponse workout = workoutService.findById(workoutId);
        if (metricRequest.timestamp().toInstant().isBefore(workout.getStartedAt().toInstant())) {
            throw new InvalidMetricTimestampException(workout.getStartedAt(), metricRequest.timestamp());
        }
        long id = storage.metricSequence.incrementAndGet();
        CardioMetricResponse response = CardioMetricResponse.builder()
                .id(id)
                .workoutId(workout.getId())
                .timestamp(metricRequest.timestamp().withOffsetSameInstant(ZoneOffset.UTC))
                .heartRate(metricRequest.heartRate())
                .distanceKm(metricRequest.distanceKm())
                .calories(metricRequest.calories())
                .pace(metricRequest.pace())
                .build();
        storage.metrics.put(id, response);
        eventPublisher.publishCreated(response);

        return response;
    }

    public List<CardioMetricResponse> getAllMetricsByWorkoutId(Long workoutId) {
        workoutService.findById(workoutId);

        return storage.metrics.values().stream()
                .filter(metric -> metric.getWorkoutId().equals(workoutId))
                .toList();
    }

    // TODO: полностью переписать логику
//    public WorkoutSummaryResponse createSummary(Long workoutId, List<CardioMetricRequest> metricRequests) {
//        WorkoutResponse workout = workoutService.findById(workoutId);
//        Integer avgHeartRate = 0;
//        Integer maxHeartRate = 0;
//        for (CardioMetricRequest request : metricRequests) {
//            if (request.heartRate() > maxHeartRate) {
//                maxHeartRate = request.heartRate();
//            }
//            avgHeartRate += request.heartRate();
//        }
//        CardioMetricRequest lastMetric = metricRequests.get(metricRequests.size() - 1);
//        avgHeartRate = avgHeartRate / metricRequests.size();
//        Long duration = Duration.between(
//                workout.getStartedAt().toInstant(),
//                lastMetric.timestamp().toInstant()
//        ).getSeconds();
//        Double avgPace = lastMetric.distanceKm() > 0
//                ? (duration / 60.0) / lastMetric.distanceKm()
//                : 0D;
//
//        WorkoutSummaryResponse response = WorkoutSummaryResponse.builder()
//                .workoutId(workout.getId())
//                .duration(duration)
//                .totalDistanceKm(lastMetric.distanceKm())
//                .avgHeartRate(avgHeartRate)
//                .maxHeartRate(maxHeartRate)
//                .totalCalories(lastMetric.calories())
//                .avgPace(avgPace)
//                .build();
//
//        storage.workoutSummaries.put(workout.getId(), response);
//        return response;
//    }

    public WorkoutSummaryResponse createSummary(Long workoutId, List<CardioMetricResponse> metricResponses) {
        WorkoutResponse workout = workoutService.findById(workoutId);
        Integer avgHeartRate = 0;
        Integer maxHeartRate = 0;
        for (CardioMetricResponse response : metricResponses) {
            if (response.getHeartRate() > maxHeartRate) {
                maxHeartRate = response.getHeartRate();
            }
            avgHeartRate += response.getHeartRate();
        }
        CardioMetricResponse lastMetric = metricResponses.get(metricResponses.size() - 1);
        avgHeartRate = avgHeartRate / metricResponses.size();
        Long duration = Duration.between(
                workout.getStartedAt().toInstant(),
                lastMetric.getTimestamp().toInstant()
        ).getSeconds();
        Double avgPace = lastMetric.getDistanceKm() > 0
                ? (duration / 60.0) / lastMetric.getDistanceKm()
                : 0D;

        WorkoutSummaryResponse response = WorkoutSummaryResponse.builder()
                .workoutId(workout.getId())
                .duration(duration)
                .totalDistanceKm(lastMetric.getDistanceKm())
                .avgHeartRate(avgHeartRate)
                .maxHeartRate(maxHeartRate)
                .totalCalories(lastMetric.getCalories())
                .avgPace(avgPace)
                .build();

        storage.workoutSummaries.put(workout.getId(), response);
        return response;
    }

    public WorkoutSummaryResponse getSummaryByWorkoutId(Long workoutId) {
        WorkoutResponse workout = workoutService.findById(workoutId);
        return storage.workoutSummaries.get(workout.getId());
    }
}

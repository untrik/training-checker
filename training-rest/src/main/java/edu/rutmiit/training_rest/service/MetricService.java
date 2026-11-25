package edu.rutmiit.training_rest.service;

import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_checker_api_contract.exception.InvalidMetricTimestampException;
import edu.rutmiit.training_checker_api_contract.exception.ResourceNotFoundException;
import edu.rutmiit.training_rest.domain.CardioMetricEntity;
import edu.rutmiit.training_rest.domain.WorkoutEntity;
import edu.rutmiit.training_rest.domain.WorkoutSummaryEntity;
import edu.rutmiit.training_rest.event.MetricEventPublisher;
import edu.rutmiit.training_rest.mapper.CardioMetricMapper;
import edu.rutmiit.training_rest.mapper.WorkoutSummaryMapper;
import edu.rutmiit.training_rest.repository.CardioMetricRepository;
import edu.rutmiit.training_rest.repository.WorkoutRepository;
import edu.rutmiit.training_rest.repository.WorkoutSummaryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@PreAuthorize("hasAnyRole('USER', 'OPERATOR')")
public class MetricService {
    private final MetricEventPublisher eventPublisher;
    private final WorkoutRepository workoutRepository;
    private final CardioMetricRepository cardioMetricRepository;
    private final CardioMetricMapper cardioMetricMapper;
    private final WorkoutSummaryRepository workoutSummaryRepository;
    private final WorkoutSummaryMapper workoutSummaryMapper;

    public MetricService(MetricEventPublisher eventPublisher, WorkoutRepository workoutRepository,
                         CardioMetricRepository cardioMetricRepository, CardioMetricMapper cardioMetricMapper,
                         WorkoutSummaryRepository workoutSummaryRepository, WorkoutSummaryMapper workoutSummaryMapper) {
        this.eventPublisher = eventPublisher;
        this.workoutRepository = workoutRepository;
        this.cardioMetricRepository = cardioMetricRepository;
        this.cardioMetricMapper = cardioMetricMapper;
        this.workoutSummaryRepository = workoutSummaryRepository;
        this.workoutSummaryMapper = workoutSummaryMapper;
    }

    @Transactional
    public List<CardioMetricResponse> createMetrics(UUID workoutId, List<CardioMetricRequest> metricRequests) {
        WorkoutEntity workout = workoutRepository.findById(workoutId).orElseThrow(()
                -> new ResourceNotFoundException("Workout", workoutId));

        List<CardioMetricResponse> responses = new ArrayList<>();

        for (CardioMetricRequest request : metricRequests) {
            if (request.timestamp().toInstant().isBefore(workout.getStartedAt().toInstant())) {
                throw new InvalidMetricTimestampException(workout.getStartedAt(), request.timestamp());
            }

            UUID id = UUID.randomUUID();

            CardioMetricEntity metric = new CardioMetricEntity(id, workout, request.timestamp().withOffsetSameInstant(ZoneOffset.UTC),
                    request.heartRate(), request.distanceKm(), request.calories(), request.pace());

            cardioMetricRepository.save(metric);

            CardioMetricResponse response = cardioMetricMapper.toResponse(metric);
            responses.add(response);

            eventPublisher.publishCreated(response);
        }
        return responses;
    }

    @Transactional
    public CardioMetricResponse createMetric(UUID workoutId, CardioMetricRequest metricRequest) {
        WorkoutEntity workout = workoutRepository.findById(workoutId).orElseThrow(()
                -> new ResourceNotFoundException("Workout", workoutId));

        if (metricRequest.timestamp().toInstant().isBefore(workout.getStartedAt().toInstant())) {
            throw new InvalidMetricTimestampException(workout.getStartedAt(), metricRequest.timestamp());
        }

        UUID id = UUID.randomUUID();

        CardioMetricEntity metric = new CardioMetricEntity(id, workout, metricRequest.timestamp().withOffsetSameInstant(ZoneOffset.UTC),
                metricRequest.heartRate(), metricRequest.distanceKm(), metricRequest.calories(), metricRequest.pace());

        cardioMetricRepository.save(metric);

        CardioMetricResponse response = cardioMetricMapper.toResponse(metric);

        eventPublisher.publishCreated(response);
        return response;
    }


    @Transactional(readOnly = true)
    public List<CardioMetricResponse> getAllMetricsByWorkoutId(UUID workoutId) {
        workoutRepository.findById(workoutId).orElseThrow(() -> new ResourceNotFoundException("Workout", workoutId));

        return cardioMetricRepository.findAllByWorkoutId(workoutId)
                .stream()
                .map(cardioMetricMapper::toResponse)
                .toList();
    }

    @Transactional
    public WorkoutSummaryResponse createSummary(UUID workoutId, List<CardioMetricResponse> metricResponses) {
        WorkoutSummaryEntity existingSummary = workoutSummaryRepository.findById(workoutId).orElse(null);

        if (existingSummary != null) {
            return workoutSummaryMapper.toResponse(existingSummary);
        }

        WorkoutEntity workout = workoutRepository.findById(workoutId).orElseThrow(()
                -> new ResourceNotFoundException("Workout", workoutId));

        if (metricResponses.isEmpty()) {
            throw new IllegalStateException("Cannot create summary without metrics");
        }

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
        Double avgPace = lastMetric.getDistanceKm() > 0 ? (duration / 60.0) / lastMetric.getDistanceKm() : 0D;

        WorkoutSummaryEntity workoutSummary = new WorkoutSummaryEntity(workout, duration, lastMetric.getDistanceKm(),
                avgHeartRate, maxHeartRate, lastMetric.getCalories(), avgPace);

        workoutSummaryRepository.save(workoutSummary);

        return workoutSummaryMapper.toResponse(workoutSummary);
    }

    @Transactional(readOnly = true)
    public WorkoutSummaryResponse getSummaryByWorkoutId(UUID workoutId) {
        workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", workoutId));

        WorkoutSummaryEntity summary = workoutSummaryRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSummary", workoutId));

        return workoutSummaryMapper.toResponse(summary);
    }
}

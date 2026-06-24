package edu.rutmiit.training_rest.service;

import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_checker_api_contract.exception.ResourceNotFoundException;
import edu.rutmiit.training_rest.event.WorkoutEventPublisher;
import edu.rutmiit.training_rest.storage.InMemoryStorage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class WorkoutService {
    private final InMemoryStorage storage;
    private final UserService userService;
    private final WorkoutEventPublisher eventPublisher;

    public WorkoutService(InMemoryStorage storage, @Lazy UserService userService,
                          WorkoutEventPublisher eventPublisher) {
        this.storage = storage;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    public PagedResponse<WorkoutResponse> getWorkoutsByUser(Long id, WorkoutType type, Double totalDistanceKm,
                                                            Integer totalCalories, String titleSearch, int page, int size) {
        Stream<WorkoutResponse> stream = storage.workouts.values().stream();
//                .filter(w -> w.getFinishedAt() != null);
        stream = stream.filter(w -> w.getUser().getId().equals(id));
        if (type != null) {
            stream = stream.filter(w -> w.getType().equals(type));
        }
        if (totalDistanceKm != null && totalDistanceKm > 0) {
            stream = stream.filter(w -> w.getWorkoutSummary().getTotalDistanceKm() >= totalDistanceKm);
        }
        if (totalCalories != null && totalCalories > 0) {
            stream = stream.filter(w -> w.getWorkoutSummary().getTotalCalories() >= totalCalories);
        }
        if (titleSearch != null && !titleSearch.isBlank()) {
            String q = titleSearch.toLowerCase();
            stream = stream.filter(w -> w.getTitle() != null && w.getTitle().toLowerCase().contains(q));
        }

        List<WorkoutResponse> workouts = stream.toList();
        int totalElements = workouts.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int from = page * size;
        int to = Math.min(from + size, totalElements);
        List<WorkoutResponse> content = (from >= totalElements) ? List.of() : workouts.subList(from, to);
        return new PagedResponse<>(content, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    public WorkoutResponse findById(Long id) {
        WorkoutResponse workout = Optional.ofNullable(storage.workouts.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Workout", id));

        return workout;
    }

    public WorkoutResponse startWorkout(Long userId, WorkoutRequest request) {
        UserResponse user = userService.findById(userId);
        long id = storage.workoutSequence.incrementAndGet();
        WorkoutResponse workout = WorkoutResponse.builder()
                .id(id)
                .user(user)
                .type(request.type())
                .title(request.title())
                .startedAt(OffsetDateTime.now((ZoneOffset.UTC)))
                .finishedAt(null)
                .metrics(null)
                .workoutSummary(null)
                .build();
        storage.workouts.put(id, workout);
        eventPublisher.publishStarted(workout);
        return workout;
    }

    public WorkoutResponse finishWorkout(Long workoutId) {
        WorkoutResponse workout = findById(workoutId);
        if (workout.getFinishedAt() != null) {
            return workout;
        }
        List<CardioMetricResponse> metrics = storage.metrics.values().stream()
                .filter(metric -> metric.getWorkoutId().equals(workout.getId()))
                .toList();
        WorkoutSummaryResponse summary = storage.workoutSummaries.get(workout.getId());
        WorkoutResponse workoutFinished = WorkoutResponse.builder()
                .id(workoutId)
                .user(workout.getUser())
                .type(workout.getType())
                .title(workout.getTitle())
                .startedAt(workout.getStartedAt())
                .finishedAt(OffsetDateTime.now((ZoneOffset.UTC)))
                .metrics(metrics)
                .workoutSummary(summary)
                .build();
        storage.workouts.put(workoutId, workoutFinished);
        eventPublisher.publishFinished(workoutFinished);
        return workoutFinished;
    }

    public void deleteWorkouts(Long workoutId) {
        WorkoutResponse workout = findById(workoutId);
        List<Long> toDeleteMetrics = storage.metrics.values()
                .stream()
                .filter(m -> m.getWorkoutId().equals(workoutId))
                .map(CardioMetricResponse::getId)
                .toList();
        toDeleteMetrics.forEach(storage.metrics::remove);
        storage.workoutSummaries.remove(workoutId);
        storage.workouts.remove(workoutId);
        eventPublisher.publishDeleted(workout.getUser().getId(),
                workout.getId(), workout.getTitle(), workout.getMetrics().size());
    }


    public void deleteWorkoutsByUserId(Long userId) {
        List<Long> toDelete = storage.workouts.values().stream()
                .filter(w -> w.getUser() != null && w.getUser().getId().equals(userId))
                .map(WorkoutResponse::getId)
                .toList();
        toDelete.forEach(this::deleteWorkouts);
    }
}

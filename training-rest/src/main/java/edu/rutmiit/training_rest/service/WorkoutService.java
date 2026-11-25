package edu.rutmiit.training_rest.service;

import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_checker_api_contract.exception.ResourceNotFoundException;
import edu.rutmiit.training_rest.domain.CardioMetricEntity;
import edu.rutmiit.training_rest.domain.UserEntity;
import edu.rutmiit.training_rest.domain.WorkoutEntity;
import edu.rutmiit.training_rest.domain.WorkoutSummaryEntity;
import edu.rutmiit.training_rest.event.WorkoutEventPublisher;
import edu.rutmiit.training_rest.mapper.WorkoutMapper;
import edu.rutmiit.training_rest.repository.CardioMetricRepository;
import edu.rutmiit.training_rest.repository.UserRepository;
import edu.rutmiit.training_rest.repository.WorkoutRepository;
import edu.rutmiit.training_rest.repository.WorkoutSummaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@PreAuthorize("hasAnyRole('USER', 'OPERATOR')")
public class WorkoutService {
    private final WorkoutEventPublisher eventPublisher;
    private final WorkoutRepository workoutRepository;
    private final CardioMetricRepository cardioMetricRepository;
    private final WorkoutSummaryRepository workoutSummaryRepository;
    private final UserRepository userRepository;
    private final WorkoutMapper workoutMapper;

    public WorkoutService(WorkoutEventPublisher eventPublisher, WorkoutRepository workoutRepository,
                          CardioMetricRepository cardioMetricRepository,
                          WorkoutSummaryRepository workoutSummaryRepository, UserRepository userRepository, WorkoutMapper workoutMapper) {

        this.eventPublisher = eventPublisher;
        this.workoutRepository = workoutRepository;
        this.cardioMetricRepository = cardioMetricRepository;
        this.workoutSummaryRepository = workoutSummaryRepository;
        this.userRepository = userRepository;
        this.workoutMapper = workoutMapper;
    }

    @Transactional(readOnly = true)
    public PagedResponse<WorkoutResponse> getWorkoutsByUser(UUID id, WorkoutType type, Double totalDistanceKm,
                                                            Integer totalCalories, String titleSearch, int page, int size) {
        String titlePattern = titleSearch == null || titleSearch.isBlank() ? null : "%" + titleSearch.toLowerCase(Locale.ROOT) + "%";
        Page<WorkoutEntity> result = workoutRepository.findByFilters(id, type, totalDistanceKm,
                totalCalories, titlePattern, PageRequest.of(page, size));

        List<WorkoutResponse> content = result.getContent()
                .stream()
                .map(this::toResponseWithDetails)
                .toList();

        return new PagedResponse<>(content, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    @Transactional(readOnly = true)
    public WorkoutResponse findById(UUID id) {
        WorkoutEntity workout = workoutRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Workout", id));
        return toResponseWithDetails(workout);
    }


    @Transactional
    public WorkoutResponse startWorkout(UUID userId, WorkoutRequest request) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
        UUID id = UUID.randomUUID();
        WorkoutEntity workoutEntity = new WorkoutEntity(id, user, request.title(), OffsetDateTime.now(ZoneOffset.UTC),
                null, request.type());
        workoutRepository.save(workoutEntity);
        WorkoutResponse workout = workoutMapper.toResponse(workoutEntity, List.of(), null);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                eventPublisher.publishStarted(workout);
            }
        });
        return workout;
    }


    @Transactional
    public WorkoutResponse finishWorkout(UUID workoutId) {
        WorkoutEntity workoutEntity = workoutRepository.findById(workoutId).orElseThrow(()
                -> new ResourceNotFoundException("Workout", workoutId));
        if (workoutEntity.getFinishedAt() != null) {
            return toResponseWithDetails(workoutEntity);
        }
        workoutEntity.setFinishedAt(OffsetDateTime.now(ZoneOffset.UTC));
        WorkoutResponse workout = toResponseWithDetails(workoutEntity);
        eventPublisher.publishFinished(workout);
        return workout;
    }

    @Transactional
    public void deleteWorkouts(UUID workoutId) {
        WorkoutEntity workout = workoutRepository.findById(workoutId).orElseThrow(()
                -> new ResourceNotFoundException("Workout", workoutId));

        int metricsCount = cardioMetricRepository.countByWorkoutId(workoutId);
        UUID userId = workout.getUser().getId();
        String title = workout.getTitle();

        workoutRepository.delete(workout);

        eventPublisher.publishDeleted(userId, workoutId, title, metricsCount);

    }


    @Transactional
    public void deleteWorkoutsByUserId(UUID userId) {
        List<WorkoutEntity> workouts = workoutRepository.findAllByUserId(userId);

        workouts.forEach(workout -> deleteWorkouts(workout.getId()));
    }


    private WorkoutResponse toResponseWithDetails(WorkoutEntity workout) {
        List<CardioMetricEntity> metrics = cardioMetricRepository.findAllByWorkoutId(workout.getId());

        WorkoutSummaryEntity summary = workoutSummaryRepository.findById(workout.getId()).orElse(null);

        return workoutMapper.toResponse(workout, metrics, summary);
    }
}

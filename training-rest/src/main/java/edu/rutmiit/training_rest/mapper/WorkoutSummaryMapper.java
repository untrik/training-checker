package edu.rutmiit.training_rest.mapper;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutSummaryResponse;
import edu.rutmiit.training_rest.domain.WorkoutSummaryEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkoutSummaryMapper {

    public WorkoutSummaryResponse toResponse(WorkoutSummaryEntity entity) {
        return WorkoutSummaryResponse.builder()
                .workoutId(entity.getWorkoutId())
                .duration(entity.getDuration())
                .totalDistanceKm(entity.getTotalDistanceKm())
                .avgHeartRate(entity.getAvgHeartRate())
                .maxHeartRate(entity.getMaxHeartRate())
                .totalCalories(entity.getTotalCalories())
                .avgPace(entity.getAvgPace())
                .build();
    }
}
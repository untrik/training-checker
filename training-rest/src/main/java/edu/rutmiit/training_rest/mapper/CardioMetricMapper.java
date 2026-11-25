package edu.rutmiit.training_rest.mapper;

import edu.rutmiit.training_checker_api_contract.dto.CardioMetricResponse;
import edu.rutmiit.training_rest.domain.CardioMetricEntity;
import org.springframework.stereotype.Component;

@Component
public class CardioMetricMapper {

    public CardioMetricResponse toResponse(CardioMetricEntity entity) {
        return CardioMetricResponse.builder()
                .id(entity.getId())
                .workoutId(entity.getWorkout().getId())
                .timestamp(entity.getTimestamp())
                .heartRate(entity.getHeartRate())
                .distanceKm(entity.getDistanceKm())
                .calories(entity.getCalories())
                .pace(entity.getPace())
                .build();
    }
}
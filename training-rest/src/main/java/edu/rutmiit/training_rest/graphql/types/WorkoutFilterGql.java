package edu.rutmiit.training_rest.graphql.types;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutType;

public record WorkoutFilterGql(
        WorkoutType type,
        Double totalDistanceKm,
        Integer totalCalories,
        String titleSearch
) {
}

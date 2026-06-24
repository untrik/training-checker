package edu.rutmiit.training_rest.graphql.types;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutType;

public record CreateWorkoutInputGql(
        WorkoutType type,
        String title
) {
}

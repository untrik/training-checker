package edu.rutmiit.training_rest.graphql.types;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;

import java.util.List;

public record WorkoutConnectionGql(
        List<WorkoutResponse> content,
        PageInfoGql pageInfo,
        int totalElements
) {
}

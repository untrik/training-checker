package edu.rutmiit.training_rest.graphql.types;

public record CreateSummaryInputGql(
        Long duration,
        Double totalDistanceKm,
        Integer avgHeartRate,
        Integer maxHeartRate,
        Integer totalCalories,
        Double avgPace
) {
}

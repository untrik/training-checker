package edu.rutmiit.training_rest.graphql.types;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record CreateMetricInputGql(
        OffsetDateTime timestamp,
        Integer heartRate,
        Double distanceKm,
        Integer calories,
        Integer pace
) {
}

package edu.rutmiit.training_events;

import java.time.OffsetDateTime;
import java.util.UUID;

public sealed interface MetricEvent {
    record Created(
            UUID id,
            UUID workoutId,
            OffsetDateTime timestamp,
            Integer heartRate,
            Double distanceKm,
            Integer calories,
            Integer pace
    )
            implements MetricEvent {
    }

}

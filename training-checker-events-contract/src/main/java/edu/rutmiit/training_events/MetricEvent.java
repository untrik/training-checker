package edu.rutmiit.training_events;

import java.time.OffsetDateTime;

public sealed interface MetricEvent {
    record Created(
            Long id,
            Long workoutId,
            OffsetDateTime timestamp,
            Integer heartRate,
            Double distanceKm,
            Integer calories,
            Integer pace
    )
            implements MetricEvent {
    }

}

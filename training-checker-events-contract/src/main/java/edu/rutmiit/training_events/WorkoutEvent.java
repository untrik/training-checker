package edu.rutmiit.training_events;

import java.nio.file.WatchEvent;
import java.time.OffsetDateTime;
import java.util.UUID;


public sealed interface WorkoutEvent {
    record Started(
            String title,
            UUID userId,
            UUID workoutId,
            OffsetDateTime startedAt
    ) implements WorkoutEvent {
    }

    record Finished(
            String title,
            UUID userId,
            UUID workoutId,
            OffsetDateTime finishedAt,
            int MetricsCount,
            Long duration,
            Double totalDistanceKm,
            Integer avgHeartRate,
            Integer maxHeartRate,
            Integer totalCalories,
            Double avgPace

    ) implements WorkoutEvent {
    }

    record Deleted(
            UUID userId,
            UUID workoutId,
            String title,
            int deletedMetricsCount
    ) implements WorkoutEvent {

    }
    record Enriched(
            UUID workoutId,
            UUID userId,
            String intensityLevel,
            Double efficiencyScore,
            String heartRateZone,
            String Recommendation
    ) implements WorkoutEvent {}
}

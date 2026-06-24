package edu.rutmiit.training_events;

import java.nio.file.WatchEvent;
import java.time.OffsetDateTime;


public sealed interface WorkoutEvent {
    record Started(
            String title,
            Long userId,
            Long workoutId,
            OffsetDateTime startedAt
    ) implements WorkoutEvent {
    }

    record Finished(
            String title,
            Long userId,
            Long workoutId,
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
            Long userId,
            Long workoutId,
            String title,
            int deletedMetricsCount
    ) implements WorkoutEvent {

    }
    record Enriched(
            Long workoutId,
            Long userId,
            String intensityLevel,
            Double efficiencyScore,
            String heartRateZone,
            String Recommendation
    ) implements WorkoutEvent {}
}

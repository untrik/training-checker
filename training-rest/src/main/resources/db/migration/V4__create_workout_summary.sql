CREATE TABLE workout_summaries
(
    workout_id        UUID PRIMARY KEY,
    duration          BIGINT           NOT NULL,
    total_distance_km DOUBLE PRECISION NOT NULL,
    avg_heart_rate    INTEGER          NOT NULL,
    max_heart_rate    INTEGER          NOT NULL,
    total_calories    INTEGER          NOT NULL,
    avg_pace          DOUBLE PRECISION NOT NULL,
    version           BIGINT           NOT NULL DEFAULT 0,

    CONSTRAINT fk_workout_summaries_workout FOREIGN KEY (workout_id) REFERENCES workouts (id) ON DELETE CASCADE,

    CONSTRAINT chk_workout_summary_duration CHECK (duration > 0),

    CONSTRAINT chk_workout_summary_distance CHECK (total_distance_km > 0),

    CONSTRAINT chk_workout_summary_avg_heart_rate CHECK (avg_heart_rate > 0),

    CONSTRAINT chk_workout_summary_max_heart_rate CHECK (max_heart_rate > 0),

    CONSTRAINT chk_workout_summary_calories CHECK (total_calories > 0),

    CONSTRAINT chk_workout_summary_avg_pace CHECK (avg_pace > 0)
);
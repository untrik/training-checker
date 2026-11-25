CREATE TABLE cardio_metrics
(
    id          UUID PRIMARY KEY,
    workout_id  UUID             NOT NULL,
    timestamp   TIMESTAMPTZ      NOT NULL,
    heart_rate  INTEGER          NOT NULL,
    distance_km DOUBLE PRECISION NOT NULL,
    calories    INTEGER          NOT NULL,
    pace        INTEGER          NOT NULL,
    version     BIGINT           NOT NULL DEFAULT 0,

    CONSTRAINT fk_cardio_metrics_workout FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE,

    CONSTRAINT chk_cardio_metrics_distance CHECK (distance_km > 0),

    CONSTRAINT chk_cardio_metrics_calories CHECK (calories > 0),

    CONSTRAINT chk_cardio_metrics_pace CHECK (pace > 0)

);

CREATE INDEX idx_cardio_metrics_workout_id ON cardio_metrics (workout_id);
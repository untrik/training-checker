CREATE TABLE workouts
(
    id          UUID PRIMARY KEY,
    user_id     UUID         NOT NULL,
    title       VARCHAR(250) NOT NULL,
    started_at  TIMESTAMPTZ  NOT NULL,
    finished_at TIMESTAMPTZ,
    type        VARCHAR(32)  NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_workouts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_workouts_user_id ON workouts (user_id);
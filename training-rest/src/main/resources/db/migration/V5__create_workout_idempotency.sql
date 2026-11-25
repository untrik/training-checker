CREATE TABLE workout_idempotency
(
    principal_id        VARCHAR(200) NOT NULL,
    operation           VARCHAR(80)  NOT NULL,
    operation_key       UUID         NOT NULL,
    request_fingerprint CHAR(64)     NOT NULL,
    state               VARCHAR(16)  NOT NULL CHECK (state IN ('IN_PROGRESS', 'COMPLETED')),
    workout_id          UUID,
    response_body       JSONB,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT clock_timestamp(),
    expires_at          TIMESTAMPTZ  NOT NULL DEFAULT (clock_timestamp() + INTERVAL '24 hours'),
    PRIMARY KEY (principal_id, operation, operation_key),
    CHECK ((state = 'IN_PROGRESS' AND workout_id IS NULL AND response_body  IS NULL)
        OR (state = 'COMPLETED' AND workout_id IS NOT NULL AND response_body  IS NOT NULL))
);

-- Снимок ответа сохраняется независимо от будущих изменений/удаления тренировки.
-- Истёкший ключ отклоняется с 410, не освобождается автоматически для нового эффекта.
CREATE INDEX idx_workout_idempotency_expiry ON workout_idempotency (expires_at);

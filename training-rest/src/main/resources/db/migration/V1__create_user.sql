CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    birth_date DATE,
    weight_kg  INTEGER,
    version    BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT uk_users_email UNIQUE (email),

    CONSTRAINT chk_users_weight_kg CHECK (weight_kg BETWEEN 1 AND 1000)
);
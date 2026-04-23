CREATE TABLE users (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(500) NOT NULL,
    created_at  TIMESTAMP
);

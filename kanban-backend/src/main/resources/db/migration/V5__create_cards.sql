CREATE TABLE cards (
    id          BIGSERIAL    PRIMARY KEY,
    column_id   BIGINT       NOT NULL REFERENCES columns(id),
    title       VARCHAR(300) NOT NULL,
    description TEXT,
    assignee_id BIGINT       REFERENCES users(id),
    due_date    DATE,
    priority    VARCHAR(20),
    position    INTEGER      NOT NULL,
    created_at  TIMESTAMP
);

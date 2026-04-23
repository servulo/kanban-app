CREATE TABLE notifications (
    id                  BIGSERIAL   PRIMARY KEY,
    user_id             BIGINT      NOT NULL REFERENCES users(id),
    type                VARCHAR(50) NOT NULL,
    message             TEXT        NOT NULL,
    is_read             BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP,
    related_entity_id   BIGINT,
    related_entity_type VARCHAR(50)
);

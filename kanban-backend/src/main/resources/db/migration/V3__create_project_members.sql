CREATE TABLE project_members (
    id         BIGSERIAL   PRIMARY KEY,
    project_id BIGINT      NOT NULL REFERENCES projects(id),
    user_id    BIGINT      NOT NULL REFERENCES users(id),
    role       VARCHAR(20) NOT NULL,
    joined_at  TIMESTAMP
);

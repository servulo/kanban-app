CREATE TABLE columns (
    id         BIGSERIAL    PRIMARY KEY,
    project_id BIGINT       NOT NULL REFERENCES projects(id),
    name       VARCHAR(100) NOT NULL,
    color      VARCHAR(7),
    position   INTEGER      NOT NULL
);

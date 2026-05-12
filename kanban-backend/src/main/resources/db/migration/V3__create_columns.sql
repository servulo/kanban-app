CREATE TABLE columns (
    id         BIGINT       IDENTITY(1,1) PRIMARY KEY,
    project_id BIGINT       NOT NULL REFERENCES projects(id),
    name       VARCHAR(100) NOT NULL,
    color      VARCHAR(7),
    position   INT          NOT NULL
);

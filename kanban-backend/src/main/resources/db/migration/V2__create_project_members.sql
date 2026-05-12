CREATE TABLE project_members (
    id          BIGINT       IDENTITY(1,1) PRIMARY KEY,
    project_id  BIGINT       NOT NULL REFERENCES projects(id),
    keycloak_id VARCHAR(36)  NOT NULL,
    role        VARCHAR(20)  NOT NULL,
    joined_at   DATETIME2
);

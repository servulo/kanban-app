CREATE TABLE projects (
    id          BIGINT        IDENTITY(1,1) PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    description NVARCHAR(MAX),
    owner_id    VARCHAR(36)   NOT NULL,
    created_at  DATETIME2
);

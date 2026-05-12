CREATE TABLE cards (
    id          BIGINT        IDENTITY(1,1) PRIMARY KEY,
    column_id   BIGINT        NOT NULL REFERENCES columns(id),
    title       VARCHAR(300)  NOT NULL,
    description NVARCHAR(MAX),
    assignee_id VARCHAR(36),
    due_date    DATE,
    priority    VARCHAR(20),
    position    INT           NOT NULL,
    created_at  DATETIME2
);

CREATE TABLE attachments (
    id          BIGINT        IDENTITY(1,1) PRIMARY KEY,
    card_id     BIGINT        NOT NULL REFERENCES cards(id),
    uploaded_by VARCHAR(36)   NOT NULL,
    file_name   VARCHAR(200)  NOT NULL,
    blob_url    NVARCHAR(MAX),
    uploaded_at DATETIME2
);

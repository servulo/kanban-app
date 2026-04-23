CREATE TABLE attachments (
    id          BIGSERIAL    PRIMARY KEY,
    card_id     BIGINT       NOT NULL REFERENCES cards(id),
    uploaded_by BIGINT       NOT NULL REFERENCES users(id),
    file_name   VARCHAR(200) NOT NULL,
    blob_url    TEXT,
    uploaded_at TIMESTAMP
);

CREATE TABLE notifications (
    id                  BIGINT        IDENTITY(1,1) PRIMARY KEY,
    keycloak_id         VARCHAR(36)   NOT NULL,
    type                VARCHAR(50)   NOT NULL,
    message             NVARCHAR(MAX) NOT NULL,
    is_read             BIT           NOT NULL DEFAULT 0,
    created_at          DATETIME2,
    related_entity_id   BIGINT,
    related_entity_type VARCHAR(50)
);

package com.kanbanapp.dto;

import java.time.LocalDateTime;

public class NotificationDTO {

    public static class NotificationResponse {
        public Long id;
        public String type;
        public String message;
        public boolean isRead;
        public String createdAt;
        public Long relatedEntityId;
        public String relatedEntityType;

        public NotificationResponse(Long id, String type, String message, boolean isRead,
                                    LocalDateTime createdAt, Long relatedEntityId, String relatedEntityType) {
            this.id = id;
            this.type = type;
            this.message = message;
            this.isRead = isRead;
            this.createdAt = createdAt.toString();
            this.relatedEntityId = relatedEntityId;
            this.relatedEntityType = relatedEntityType;
        }
    }

    public static class MarkAsReadRequest {
        public boolean markAll;
    }
}
package com.kanbanapp.dto;

import java.util.List;

public class CardDTO {

    public static class CreateRequest {
        public Long columnId;
        public String title;
        public String description;
        public String assigneeId;
        public String dueDate;
        public String priority;
        public Integer position;
    }

    public static class MoveRequest {
        public Long columnId;
        public Integer position;
    }

    public static class AttachmentSummary {
        public Long id;
        public String fileName;
        public String blobUrl;
        public String uploadedAt;

        public AttachmentSummary(Long id, String fileName, String blobUrl, String uploadedAt) {
            this.id = id;
            this.fileName = fileName;
            this.blobUrl = blobUrl;
            this.uploadedAt = uploadedAt;
        }
    }

    public static class CardResponse {
        public Long id;
        public String title;
        public String description;
        public Long columnId;
        public String assigneeId;
        public String dueDate;
        public String priority;
        public Integer position;
        public String createdAt;
        public List<AttachmentSummary> attachments;

        public CardResponse(Long id, String title, String description, Long columnId,
                            String assigneeId, String dueDate,
                            String priority, Integer position, String createdAt,
                            List<AttachmentSummary> attachments) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.columnId = columnId;
            this.assigneeId = assigneeId;
            this.dueDate = dueDate;
            this.priority = priority;
            this.position = position;
            this.createdAt = createdAt;
            this.attachments = attachments;
        }
    }
}

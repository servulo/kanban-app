package com.kanbanapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {

    public static class CreateRequest {
        public String name;
        public String description;
    }

    public static class MemberResponse {
        public String keycloakId;
        public String role;

        public MemberResponse(String keycloakId, String role) {
            this.keycloakId = keycloakId;
            this.role = role;
        }
    }

    public static class ProjectResponse {
        public Long id;
        public String name;
        public String description;
        public String ownerId;
        public LocalDateTime createdAt;
        public List<MemberResponse> members;

        public ProjectResponse(Long id, String name, String description, String ownerId,
                               LocalDateTime createdAt, List<MemberResponse> members) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.ownerId = ownerId;
            this.createdAt = createdAt;
            this.members = members;
        }
    }
}

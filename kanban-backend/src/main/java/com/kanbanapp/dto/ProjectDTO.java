package com.kanbanapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {

    public static class CreateRequest {
        public String name;
        public String description;
    }

    public static class MemberResponse {
        public Long userId;
        public String name;
        public String email;
        public String role;

        public MemberResponse(Long userId, String name, String email, String role) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
        }
    }

    public static class ProjectResponse {
        public Long id;
        public String name;
        public String description;
        public Long ownerId;
        public LocalDateTime createdAt;
        public List<MemberResponse> members;

        public ProjectResponse(Long id, String name, String description, Long ownerId, LocalDateTime createdAt, List<MemberResponse> members) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.ownerId = ownerId;
            this.createdAt = createdAt;
            this.members = members;
        }
    }
}
package com.kanbanapp.dto;

public class AuthDTO {

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class AuthResponse {
        public String token;
        public String name;
        public String email;
        public Long userId;

        public AuthResponse(String token, String name, String email, Long userId) {
            this.token = token;
            this.name = name;
            this.email = email;
            this.userId = userId;
        }
    }
}
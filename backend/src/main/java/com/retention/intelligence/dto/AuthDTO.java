package com.retention.intelligence.dto;

import com.retention.intelligence.security.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class AuthDTO {

    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        public LoginRequest() {}

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private UUID userId;
        private String email;
        private Role role;
        private UUID companyId;

        public AuthResponse() {}

        public AuthResponse(String token, UUID userId, String email, Role role, UUID companyId) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.role = role;
            this.companyId = companyId;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }

        public UUID getCompanyId() { return companyId; }
        public void setCompanyId(UUID companyId) { this.companyId = companyId; }

        public static AuthResponseBuilder builder() { return new AuthResponseBuilder(); }

        public static class AuthResponseBuilder {
            private String token;
            private UUID userId;
            private String email;
            private Role role;
            private UUID companyId;

            public AuthResponseBuilder token(String token) { this.token = token; return this; }
            public AuthResponseBuilder userId(UUID userId) { this.userId = userId; return this; }
            public AuthResponseBuilder email(String email) { this.email = email; return this; }
            public AuthResponseBuilder role(Role role) { this.role = role; return this; }
            public AuthResponseBuilder companyId(UUID companyId) { this.companyId = companyId; return this; }

            public AuthResponse build() {
                return new AuthResponse(token, userId, email, role, companyId);
            }
        }
    }
}

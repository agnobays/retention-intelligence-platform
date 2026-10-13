package com.retention.intelligence.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class OnboardingDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyRegistrationRequest {
        @NotBlank(message = "Company name is required")
        private String companyName;

        @NotBlank(message = "Company domain is required (e.g. acme.co.za)")
        private String domain;

        @NotBlank(message = "Industry / Business type is required")
        private String industry; // e.g. LOGISTICS_COLD_CHAIN, FINANCIAL_SERVICES, FITNESS_GYM, RETAIL_ECOMMERCE, SAAS_SUBSCRIPTION

        private String subscriptionTier; // ENTERPRISE, STANDARD, PRO

        // Initial Admin User Credentials
        @Email(message = "Valid email is required")
        @NotBlank(message = "Admin email is required")
        private String adminEmail;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String adminPassword;

        private String adminFirstName;
        private String adminLastName;

        // Custom Branding / Retention Policy settings (Optional)
        private String brandName;
        private String senderName;
        private String senderTitle;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyRegistrationResponse {
        private UUID companyId;
        private String companyName;
        private String domain;
        private String industry;
        private UUID adminUserId;
        private String adminEmail;
        private String authToken;
        private String isolatedDatabase;
        private String message;
        private boolean success;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniquenessCheckResponse {
        private String field;
        private String value;
        private boolean isAvailable;
        private String message;
    }
}

package com.retention.intelligence.controller;

import com.retention.intelligence.dto.OnboardingDTO;
import com.retention.intelligence.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/onboarding", "/onboarding"})
@RequiredArgsConstructor
@Tag(name = "Organization Onboarding", description = "Endpoints for onboarding new businesses with unique isolated databases")
public class OnboardingController {

    private static final Logger log = LoggerFactory.getLogger(OnboardingController.class);
    private final OnboardingService onboardingService;

    @PostMapping("/register")
    @Operation(summary = "Onboard New Business Organization", description = "Registers a new tenant, verifies domain/email uniqueness, provisions isolated database, and initializes custom retention policy")
    public ResponseEntity<OnboardingDTO.CompanyRegistrationResponse> registerCompany(
            @Valid @RequestBody OnboardingDTO.CompanyRegistrationRequest request) {
        log.info("🏢 API REQUEST: POST /api/v1/onboarding/register - Registering company: {} ({})",
                request.getCompanyName(), request.getDomain());
        return ResponseEntity.ok(onboardingService.onboardNewCompany(request));
    }

    @GetMapping("/check-domain")
    @Operation(summary = "Check Domain Uniqueness", description = "Verifies if an organization domain is available before registration")
    public ResponseEntity<OnboardingDTO.UniquenessCheckResponse> checkDomain(
            @RequestParam String domain) {
        return ResponseEntity.ok(onboardingService.checkDomainAvailability(domain));
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check Email Uniqueness", description = "Verifies if an administrator email is available before registration")
    public ResponseEntity<OnboardingDTO.UniquenessCheckResponse> checkEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(onboardingService.checkEmailAvailability(email));
    }
}

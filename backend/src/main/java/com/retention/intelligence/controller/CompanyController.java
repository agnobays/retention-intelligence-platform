package com.retention.intelligence.controller;

import com.retention.intelligence.entity.Company;
import com.retention.intelligence.entity.RetentionPolicy;
import com.retention.intelligence.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/companies", "/companies"})
@RequiredArgsConstructor
@Tag(name = "Company Management", description = "Endpoints for managing organization tenants & retention policies")
public class CompanyController {

    private static final Logger log = LoggerFactory.getLogger(CompanyController.class);
    private final CompanyService companyService;

    @GetMapping
    @Operation(summary = "List All Companies", description = "Returns registered organization tenants")
    public ResponseEntity<List<Company>> getAllCompanies() {
        log.info("🏢 API REQUEST: GET /api/v1/companies - Fetching tenant companies");
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{companyId}/policy")
    @Operation(summary = "Get Company Retention Policy", description = "Returns custom churn & retention rules for a company")
    public ResponseEntity<RetentionPolicy> getCompanyPolicy(@PathVariable UUID companyId) {
        log.info("📋 API REQUEST: GET /api/v1/companies/{}/policy", companyId);
        return companyService.getPolicyForCompany(companyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{companyId}/policy")
    @Operation(summary = "Update Company Retention Policy", description = "Updates custom churn thresholds & branding rules for a company")
    public ResponseEntity<RetentionPolicy> saveCompanyPolicy(
            @PathVariable UUID companyId,
            @RequestBody RetentionPolicy policy) {
        log.info("💾 API REQUEST: POST /api/v1/companies/{}/policy", companyId);
        return ResponseEntity.ok(companyService.saveOrUpdatePolicy(companyId, policy));
    }
}

package com.retention.intelligence.controller;

import com.retention.intelligence.entity.Company;
import com.retention.intelligence.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/companies", "/companies"})
@RequiredArgsConstructor
@Tag(name = "Company Management", description = "Endpoints for managing organization tenants")
public class CompanyController {

    private static final Logger log = LoggerFactory.getLogger(CompanyController.class);
    private final CompanyService companyService;

    @GetMapping
    @Operation(summary = "List All Companies", description = "Returns registered organization tenants")
    public ResponseEntity<List<Company>> getAllCompanies() {
        log.info("🏢 API REQUEST: GET /api/v1/companies - Fetching tenant companies");
        return ResponseEntity.ok(companyService.getAllCompanies());
    }
}

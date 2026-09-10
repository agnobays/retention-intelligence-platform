package com.retention.intelligence.controller;

import com.retention.intelligence.dto.CustomerDTO;
import com.retention.intelligence.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for Customer Import and Telemetry Status")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')")
    @Operation(summary = "Get All Customers", description = "Retrieves all monitored enterprise customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.info("================================================================================");
        log.info("📥 API REQUEST: GET /api/v1/customers - Fetching all corporate accounts");
        log.info("================================================================================");
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')")
    @Operation(summary = "Get Customer List by Company", description = "Retrieves customers by company ID")
    public ResponseEntity<List<CustomerDTO>> getCustomersByCompany(@PathVariable UUID companyId) {
        log.info("📥 API REQUEST: GET /api/v1/customers/company/{}", companyId);
        return ResponseEntity.ok(customerService.getCustomersByCompany(companyId));
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    @Operation(summary = "Import Customer Data", description = "Imports or updates customer profile data")
    public ResponseEntity<CustomerDTO> importCustomer(@Valid @RequestBody CustomerDTO dto) {
        log.info("================================================================================");
        log.info("📥 API REQUEST: POST /api/v1/customers/import - Importing customer: {} ({})", dto.getName(), dto.getExternalCustomerId());
        log.info("================================================================================");
        return ResponseEntity.ok(customerService.importCustomer(dto));
    }
}

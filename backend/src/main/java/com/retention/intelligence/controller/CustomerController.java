package com.retention.intelligence.controller;

import com.retention.intelligence.dto.BatchImportResultDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/customers", "/customers"})
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for Customer Import and Telemetry Status")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')")
    @Operation(summary = "Get All Customers", description = "Retrieves all monitored enterprise customers for the authenticated company")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(@AuthenticationPrincipal com.retention.intelligence.security.UserPrincipal principal) {
        log.info("================================================================================");
        log.info("📥 API REQUEST: GET /api/v1/customers - Fetching all corporate accounts for company {}", principal.getCompanyId());
        log.info("================================================================================");
        
        if (principal.getRole() == com.retention.intelligence.security.Role.SUPER_ADMIN) {
             return ResponseEntity.ok(customerService.getAllCustomers());
        }
        return ResponseEntity.ok(customerService.getCustomersByCompany(principal.getCompanyId()));
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasAnyRole('COMPANY_ADMIN', 'MANAGER', 'ANALYST') and #companyId == authentication.principal.companyId)")
    @Operation(summary = "Get Customer List by Company", description = "Retrieves customers by company ID")
    public ResponseEntity<List<CustomerDTO>> getCustomersByCompany(@PathVariable java.util.UUID companyId) {
        log.info("📥 API REQUEST: GET /api/v1/customers/company/{}", companyId);
        return ResponseEntity.ok(customerService.getCustomersByCompany(companyId));
    }

    @PostMapping({"/import", "/save"})
    @Operation(summary = "Import Customer Data", description = "Imports or updates customer profile data")
    public ResponseEntity<CustomerDTO> importCustomer(@Valid @RequestBody CustomerDTO dto) {
        log.info("================================================================================");
        log.info("📥 API REQUEST: POST /api/v1/customers/import - Importing customer: {} ({})", dto.getName(), dto.getExternalCustomerId());
        log.info("================================================================================");
        return ResponseEntity.ok(customerService.importCustomer(dto));
    }

    @PostMapping({"/import-batch", "/batch-import", "/import-spreadsheet", "/batch"})
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    @Operation(summary = "Import Spreadsheet Corporate Accounts", description = "Analyzes batch corporate CSV rows, evaluates churn risk, and auto-triggers Camunda 7 BPMN workflows")
    public java.util.concurrent.CompletableFuture<ResponseEntity<BatchImportResultDTO>> importSpreadsheetBatch(@RequestBody List<CustomerDTO> batchList) {
        log.info("================================================================================");
        log.info("📥 API REQUEST: POST /api/v1/customers/import-batch - Analyzing {} spreadsheet rows", batchList != null ? batchList.size() : 0);
        log.info("================================================================================");
        return customerService.processBatchSpreadsheetImport(batchList != null ? batchList : List.of())
                .thenApply(ResponseEntity::ok);
    }
}

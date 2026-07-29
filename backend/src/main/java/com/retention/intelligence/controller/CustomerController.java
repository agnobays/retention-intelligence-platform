package com.retention.intelligence.controller;

import com.retention.intelligence.dto.CustomerDTO;
import com.retention.intelligence.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final CustomerService customerService;

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')")
    @Operation(summary = "Get Customer List", description = "Retrieves customers by company ID")
    public ResponseEntity<List<CustomerDTO>> getCustomersByCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(customerService.getCustomersByCompany(companyId));
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    @Operation(summary = "Import Customer Data", description = "Imports or updates customer profile data")
    public ResponseEntity<CustomerDTO> importCustomer(@Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.importCustomer(dto));
    }
}

package com.retention.intelligence.controller;

import com.retention.intelligence.dto.CustomerValueDTO;
import com.retention.intelligence.service.CustomerValueEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/value-engine")
@RequiredArgsConstructor
@Tag(name = "Customer Value Engine", description = "Endpoints for computing Customer LTV, ARR/MRR, and SLA Tiers")
public class CustomerValueEngineController {

    private final CustomerValueEngineService customerValueEngineService;

    @PostMapping("/calculate/{customerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')")
    @Operation(summary = "Calculate Customer Value", description = "Computes LTV, support load, and SLA tier")
    public ResponseEntity<CustomerValueDTO> calculateCustomerValue(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerValueEngineService.calculateCustomerValue(customerId));
    }
}

package com.retention.intelligence.controller;

import com.retention.intelligence.dto.CustomerValueDTO;
import com.retention.intelligence.service.CustomerValueEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/value-engine", "/value-engine"})
@RequiredArgsConstructor
@Tag(name = "Customer Value Engine", description = "Endpoints for computing Customer LTV, ARR/MRR, and SLA Tiers")
public class CustomerValueEngineController {

    private static final Logger log = LoggerFactory.getLogger(CustomerValueEngineController.class);
    private final CustomerValueEngineService customerValueEngineService;

    @PostMapping({"/calculate/{customerId}", "/{customerId}/calculate"})
    @Operation(summary = "Calculate Customer Value", description = "Computes LTV, support load, and SLA tier")
    public ResponseEntity<CustomerValueDTO> calculateCustomerValue(@PathVariable UUID customerId) {
        log.info("================================================================================");
        log.info("💎 API REQUEST: POST /api/v1/value-engine/calculate/{}", customerId);
        log.info("================================================================================");
        return ResponseEntity.ok(customerValueEngineService.calculateCustomerValue(customerId));
    }
}

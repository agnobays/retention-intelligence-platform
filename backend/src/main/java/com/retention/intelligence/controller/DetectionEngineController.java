package com.retention.intelligence.controller;

import com.retention.intelligence.dto.DetectionDTO;
import com.retention.intelligence.service.DetectionEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/detection")
@RequiredArgsConstructor
@Tag(name = "Detection Engine", description = "Endpoints for analyzing churn risk signals and metric drops")
public class DetectionEngineController {

    private final DetectionEngineService detectionEngineService;

    @PostMapping("/evaluate/{customerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')")
    @Operation(summary = "Run Churn Risk Detection", description = "Evaluates risk metrics and flags at-risk customers")
    public ResponseEntity<DetectionDTO> runDetection(@PathVariable UUID customerId) {
        return ResponseEntity.ok(detectionEngineService.runDetectionForCustomer(customerId));
    }
}

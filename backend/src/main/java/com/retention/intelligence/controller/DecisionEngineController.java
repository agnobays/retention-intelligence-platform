package com.retention.intelligence.controller;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.service.DecisionEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/decision-engine")
@RequiredArgsConstructor
@Tag(name = "Decision Engine", description = "Endpoints for recommending automated recovery playbooks")
public class DecisionEngineController {

    private final DecisionEngineService decisionEngineService;

    @PostMapping("/recommend/{customerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')")
    @Operation(summary = "Recommend Recovery Action", description = "Evaluates decision rules to recommend retention action")
    public ResponseEntity<RecoveryDTO> recommendRecoveryAction(@PathVariable UUID customerId) {
        return ResponseEntity.ok(decisionEngineService.recommendRecoveryAction(customerId));
    }
}

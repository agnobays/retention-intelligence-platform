package com.retention.intelligence.controller;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.service.RecoveryEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/recovery-engine")
@RequiredArgsConstructor
@Tag(name = "Recovery Engine", description = "Endpoints for executing and tracking customer recovery actions")
public class RecoveryEngineController {

    private final RecoveryEngineService recoveryEngineService;

    @PostMapping("/execute/{planId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    @Operation(summary = "Execute Recovery Action", description = "Triggers recovery plan execution")
    public ResponseEntity<RecoveryDTO> executeRecoveryAction(@PathVariable UUID planId) {
        return ResponseEntity.ok(recoveryEngineService.executeRecoveryAction(planId));
    }
}

package com.retention.intelligence.controller;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.service.RecoveryEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/recovery-engine", "/recovery-engine"})
@RequiredArgsConstructor
@Tag(name = "Recovery Engine", description = "Endpoints for executing and tracking customer recovery actions")
public class RecoveryEngineController {

    private static final Logger log = LoggerFactory.getLogger(RecoveryEngineController.class);
    private final RecoveryEngineService recoveryEngineService;

    @PostMapping({"/execute/{planId}", "/{planId}/execute"})
    @Operation(summary = "Execute Recovery Action", description = "Triggers recovery plan execution")
    public ResponseEntity<RecoveryDTO> executeRecoveryAction(@PathVariable UUID planId) {
        log.info("================================================================================");
        log.info("🛠️ API REQUEST: POST /api/v1/recovery-engine/execute/{}", planId);
        log.info("================================================================================");
        return ResponseEntity.ok(recoveryEngineService.executeRecoveryAction(planId));
    }
}

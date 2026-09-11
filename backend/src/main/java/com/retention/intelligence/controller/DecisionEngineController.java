package com.retention.intelligence.controller;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.service.DecisionEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/decision-engine", "/decision-engine"})
@RequiredArgsConstructor
@Tag(name = "Decision Engine", description = "Endpoints for recommending automated recovery playbooks")
public class DecisionEngineController {

    private static final Logger log = LoggerFactory.getLogger(DecisionEngineController.class);
    private final DecisionEngineService decisionEngineService;

    @PostMapping({"/recommend/{customerId}", "/{customerId}/recommend"})
    @Operation(summary = "Recommend Recovery Action", description = "Evaluates decision rules to recommend retention action")
    public ResponseEntity<RecoveryDTO> recommendRecoveryAction(@PathVariable UUID customerId) {
        log.info("================================================================================");
        log.info("🧠 API REQUEST: POST /api/v1/decision-engine/recommend/{}", customerId);
        log.info("================================================================================");
        return ResponseEntity.ok(decisionEngineService.recommendRecoveryAction(customerId));
    }
}

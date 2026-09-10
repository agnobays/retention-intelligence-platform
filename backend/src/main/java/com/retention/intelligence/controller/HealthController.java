package com.retention.intelligence.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Tag(name = "Health & Info", description = "Root system health and platform status endpoints")
public class HealthController {

    @GetMapping({"/", "/health", "/status", "/info"})
    @Operation(summary = "Root System Health", description = "Returns active platform status and navigation links")
    public ResponseEntity<Map<String, Object>> getRootStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "ONLINE",
                "system", "Standard Bank CIB Retention Intelligence Platform API",
                "version", "1.0.0-SNAPSHOT",
                "timestamp", LocalDateTime.now().toString(),
                "endpoints", Map.of(
                        "customers", "/api/v1/customers",
                        "workflowTasks", "/api/v1/workflow/tasks",
                        "detectionBatch", "/api/v1/detection/evaluate-batch",
                        "swaggerUI", "/api/v1/swagger-ui.html"
                )
        ));
    }
}

package com.retention.intelligence.controller;

import com.retention.intelligence.service.IntegrationEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/integration")
@RequiredArgsConstructor
@Tag(name = "Integration Engine", description = "Endpoints for external CRM webhooks and telemetry data ingestion")
public class IntegrationEngineController {

    private final IntegrationEngineService integrationEngineService;

    @PostMapping("/webhook/{sourceSystem}")
    @Operation(summary = "Ingest Telemetry Webhook", description = "Receives webhooks from Segment, Hubspot, or Custom Telemetry")
    public ResponseEntity<Map<String, Object>> receiveWebhook(@PathVariable String sourceSystem,
                                                              @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(integrationEngineService.processWebhook(sourceSystem, payload));
    }
}

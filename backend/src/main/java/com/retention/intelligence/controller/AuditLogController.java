package com.retention.intelligence.controller;

import com.retention.intelligence.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/v1/audit", "/audit"})
@RequiredArgsConstructor
@Tag(name = "Customer Journey Audit Logs", description = "Endpoints for retrieving full customer session journey logs, message history, approvers, and timestamps")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/logs")
    @Operation(summary = "Get All Customer Journey Audit Logs", description = "Retrieves complete session audit history with unique Session IDs, customer numbers, message contents, and approvers")
    public ResponseEntity<List<AuditLogService.CustomerJourneyLogItem>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllJourneyLogs());
    }

    @GetMapping("/logs/customer/{externalCustomerId}")
    @Operation(summary = "Get Audit Logs for Customer Number", description = "Retrieves session audit history for a specific customer ID")
    public ResponseEntity<List<AuditLogService.CustomerJourneyLogItem>> getAuditLogsByCustomer(@PathVariable String externalCustomerId) {
        return ResponseEntity.ok(auditLogService.getLogsByCustomer(externalCustomerId));
    }

    @PostMapping("/logs")
    @Operation(summary = "Record Customer Journey Event", description = "Records a new customer journey session audit log entry")
    public ResponseEntity<AuditLogService.CustomerJourneyLogItem> recordAuditLog(@RequestBody Map<String, String> payload) {
        AuditLogService.CustomerJourneyLogItem item = auditLogService.recordLog(
            payload.get("sessionId"),
            payload.get("externalCustomerId"),
            payload.get("customerName"),
            payload.get("eventType"),
            payload.get("approver"),
            payload.get("recipientEmail"),
            payload.get("subject"),
            payload.get("messageContent"),
            payload.get("concessionReward")
        );
        return ResponseEntity.ok(item);
    }
}

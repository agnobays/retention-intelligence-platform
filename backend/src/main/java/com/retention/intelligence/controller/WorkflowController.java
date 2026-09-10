package com.retention.intelligence.controller;

import com.retention.intelligence.dto.WorkflowDTO;
import com.retention.intelligence.service.EmailService;
import com.retention.intelligence.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/workflow", "/workflow"})
@RequiredArgsConstructor
@Tag(name = "Workflow (Camunda 7)", description = "Endpoints for triggering and querying BPMN Customer Recovery Workflows")
public class WorkflowController {

    private final WorkflowService workflowService;
    private final EmailService emailService;

    @PostMapping("/start/{customerId}")
    @Operation(summary = "Start Customer Recovery BPMN Workflow", description = "Launches Camunda CustomerRecoveryProcess workflow instance")
    public ResponseEntity<WorkflowDTO> startRecoveryWorkflow(@PathVariable UUID customerId) {
        return ResponseEntity.ok(workflowService.startRecoveryWorkflow(customerId));
    }

    @GetMapping({"/tasks", "/tasks/pending"})
    @Operation(summary = "Get Pending Manager Approval User Tasks", description = "Retrieves all active Camunda user tasks waiting for manager approval")
    public ResponseEntity<List<WorkflowDTO>> getPendingManagerTasks() {
        return ResponseEntity.ok(workflowService.getPendingManagerTasks());
    }

    @PostMapping({"/tasks/{taskId}/complete", "/tasks/complete/{taskId}"})
    @Operation(summary = "Complete Manager Approval Task", description = "Approves or rejects a pending Camunda user task and triggers immediate retention email")
    public ResponseEntity<WorkflowDTO> completeManagerTask(
            @PathVariable String taskId,
            @RequestParam(required = false, defaultValue = "true") Boolean approved,
            @RequestBody(required = false) Map<String, Object> body) {
        
        boolean isApproved = true;
        if (approved != null) {
            isApproved = approved;
        }
        if (body != null && body.containsKey("approved")) {
            Object appObj = body.get("approved");
            if (appObj instanceof Boolean) {
                isApproved = (Boolean) appObj;
            }
        }

        return ResponseEntity.ok(workflowService.completeManagerTask(taskId, isApproved));
    }

    @PostMapping({"/test-email", "/send-email"})
    @Operation(summary = "Dispatch Immediate Retention Email", description = "Dispatches immediate test retention email to recipient")
    public ResponseEntity<Map<String, String>> sendTestEmail(@RequestBody(required = false) Map<String, String> payload) {
        String recipient = (payload != null && payload.containsKey("recipient")) ? payload.get("recipient") : "zolani1999@gmail.com";
        String customerName = (payload != null && payload.containsKey("customerName")) ? payload.get("customerName") : "Shoprite Holdings Ltd";
        int discount = (payload != null && payload.containsKey("discount")) ? Integer.parseInt(payload.get("discount")) : 15;

        String result = emailService.sendDirectEmail(customerName, "SB-CIB-1001", discount, "Executive Fee Concession & RM Outreach", recipient);
        return ResponseEntity.ok(Map.of("status", result, "recipient", recipient, "customerName", customerName));
    }
}

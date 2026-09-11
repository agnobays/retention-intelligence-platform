package com.retention.intelligence.controller;

import com.retention.intelligence.dto.WorkflowDTO;
import com.retention.intelligence.service.EmailService;
import com.retention.intelligence.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);

    private final WorkflowService workflowService;
    private final EmailService emailService;

    @PostMapping({"/start/{customerId}", "/{customerId}/start"})
    @Operation(summary = "Start Customer Recovery BPMN Workflow", description = "Launches Camunda CustomerRecoveryProcess workflow instance")
    public ResponseEntity<WorkflowDTO> startRecoveryWorkflow(@PathVariable UUID customerId) {
        log.info("================================================================================");
        log.info("🚀 API REQUEST: POST /api/v1/workflow/start/{} - STARTING CAMUNDA BPMN WORKFLOW", customerId);
        log.info("================================================================================");
        return ResponseEntity.ok(workflowService.startRecoveryWorkflow(customerId));
    }

    @PostMapping({"/start-executive-escalation/{customerId}", "/executive-escalation/{customerId}"})
    @Operation(summary = "Start Executive Escalation Camunda Workflow", description = "Launches Camunda ExecutiveEscalationProcess workflow instance")
    public ResponseEntity<WorkflowDTO> startExecutiveEscalationWorkflow(@PathVariable UUID customerId) {
        log.info("================================================================================");
        log.info("⚡ API REQUEST: POST /api/v1/workflow/start-executive-escalation/{} - LAUNCHING TIER 1 ESCALATION", customerId);
        log.info("================================================================================");
        return ResponseEntity.ok(workflowService.startExecutiveEscalationWorkflow(customerId));
    }

    @PostMapping({"/start-churn-survey/{customerId}", "/churn-survey/{customerId}"})
    @Operation(summary = "Start Churn Prevention Survey Camunda Workflow", description = "Launches Camunda ChurnPreventionSurveyProcess workflow instance")
    public ResponseEntity<WorkflowDTO> startChurnPreventionSurveyWorkflow(@PathVariable UUID customerId) {
        log.info("================================================================================");
        log.info("📊 API REQUEST: POST /api/v1/workflow/start-churn-survey/{} - LAUNCHING SURVEY WORKFLOW", customerId);
        log.info("================================================================================");
        return ResponseEntity.ok(workflowService.startChurnPreventionSurveyWorkflow(customerId));
    }

    @PostMapping({"/trigger-all/{customerId}", "/all/{customerId}"})
    @Operation(summary = "Trigger All 3 Camunda Workflows", description = "Launches all 3 Camunda BPMN workflows in sequence")
    public ResponseEntity<Map<String, Object>> triggerAllWorkflows(@PathVariable UUID customerId) {
        log.info("================================================================================");
        log.info("🚀⚡📊 API REQUEST: POST /api/v1/workflow/trigger-all/{} - LAUNCHING ALL 3 CAMUNDA WORKFLOWS", customerId);
        log.info("================================================================================");
        WorkflowDTO wf1 = workflowService.startRecoveryWorkflow(customerId);
        WorkflowDTO wf2 = workflowService.startExecutiveEscalationWorkflow(customerId);
        WorkflowDTO wf3 = workflowService.startChurnPreventionSurveyWorkflow(customerId);

        return ResponseEntity.ok(Map.of(
            "status", "ALL_WORKFLOWS_ACTIVE",
            "customerRecoveryProcess", wf1,
            "executiveEscalationProcess", wf2,
            "churnPreventionSurveyProcess", wf3
        ));
    }

    @GetMapping({"/tasks", "/tasks/pending"})
    @Operation(summary = "Get Pending Manager Approval User Tasks", description = "Retrieves all active Camunda user tasks waiting for manager approval")
    public ResponseEntity<List<WorkflowDTO>> getPendingManagerTasks() {
        log.info("📥 API REQUEST: GET /api/v1/workflow/tasks/pending - Querying active Camunda user tasks");
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

        log.info("================================================================================");
        log.info("✔️ API REQUEST: POST /api/v1/workflow/tasks/{}/complete - Approval Status: {}", taskId, isApproved);
        log.info("================================================================================");

        return ResponseEntity.ok(workflowService.completeManagerTask(taskId, isApproved));
    }

    @PostMapping({"/test-email", "/send-email"})
    @Operation(summary = "Dispatch Immediate Retention Email", description = "Dispatches immediate test retention email to recipient")
    public ResponseEntity<Map<String, String>> sendTestEmail(@RequestBody(required = false) Map<String, String> payload) {
        String recipient = (payload != null && payload.containsKey("recipient")) ? payload.get("recipient") : "zolani1999@gmail.com";
        String customerName = (payload != null && payload.containsKey("customerName")) ? payload.get("customerName") : "Shoprite Holdings Ltd";
        int discount = (payload != null && payload.containsKey("discount")) ? Integer.parseInt(payload.get("discount")) : 15;

        log.info("================================================================================");
        log.info("📧 API REQUEST: POST /api/v1/workflow/test-email - Triggering test email to {}", recipient);
        log.info("================================================================================");

        String result = emailService.sendDirectEmail(customerName, "SB-CIB-1001", discount, "Executive Fee Concession & RM Outreach", recipient);
        return ResponseEntity.ok(Map.of("status", result, "recipient", recipient, "customerName", customerName));
    }
}

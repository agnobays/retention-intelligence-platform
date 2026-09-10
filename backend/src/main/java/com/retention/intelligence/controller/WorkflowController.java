package com.retention.intelligence.controller;

import com.retention.intelligence.dto.WorkflowDTO;
import com.retention.intelligence.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
@Tag(name = "Workflow (Camunda 7)", description = "Endpoints for triggering and querying BPMN Customer Recovery Workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/start/{customerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    @Operation(summary = "Start Customer Recovery BPMN Workflow", description = "Launches Camunda CustomerRecoveryProcess workflow instance")
    public ResponseEntity<WorkflowDTO> startRecoveryWorkflow(@PathVariable UUID customerId) {
        return ResponseEntity.ok(workflowService.startRecoveryWorkflow(customerId));
    }

    @GetMapping("/tasks/pending")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    @Operation(summary = "Get Pending Manager Approval User Tasks", description = "Retrieves all active Camunda user tasks waiting for manager approval")
    public ResponseEntity<List<WorkflowDTO>> getPendingManagerTasks() {
        return ResponseEntity.ok(workflowService.getPendingManagerTasks());
    }

    @PostMapping("/tasks/complete/{taskId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    @Operation(summary = "Complete Manager Approval Task", description = "Approves or rejects a pending Camunda user task")
    public ResponseEntity<WorkflowDTO> completeManagerTask(@PathVariable String taskId, @RequestParam(defaultValue = "true") boolean approved) {
        return ResponseEntity.ok(workflowService.completeManagerTask(taskId, approved));
    }
}

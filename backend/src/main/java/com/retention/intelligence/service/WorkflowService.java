package com.retention.intelligence.service;

import com.retention.intelligence.dto.WorkflowDTO;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.RecoveryPlan;
import com.retention.intelligence.repository.CustomerRepository;
import com.retention.intelligence.repository.RecoveryPlanRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final CustomerRepository customerRepository;
    private final RecoveryPlanRepository recoveryPlanRepository;

    public WorkflowDTO startRecoveryWorkflow(UUID customerId) {
        String processKey = "CustomerRecoveryProcess";
        Map<String, Object> variables = Map.of(
                "customerId", customerId.toString(),
                "requiresApproval", true
        );

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);

        Customer customer = customerRepository.findById(customerId).orElse(null);
        String customerName = customer != null ? customer.getName() : "Enterprise Client";

        List<RecoveryPlan> plans = recoveryPlanRepository.findByCustomerId(customerId);
        if (!plans.isEmpty()) {
            RecoveryPlan latestPlan = plans.get(plans.size() - 1);
            latestPlan.setWorkflowInstanceId(processInstance.getProcessInstanceId());
            recoveryPlanRepository.save(latestPlan);
        }

        return WorkflowDTO.builder()
                .processDefinitionKey(processKey)
                .workflowInstanceId(processInstance.getProcessInstanceId())
                .customerId(customerId)
                .customerName(customerName)
                .variables(variables)
                .status(processInstance.isEnded() ? "COMPLETED" : "ACTIVE")
                .build();
    }

    public List<WorkflowDTO> getPendingManagerTasks() {
        List<Task> tasks = taskService.createTaskQuery().taskDefinitionKey("Task_ManagerApproval").list();
        List<WorkflowDTO> dtos = new ArrayList<>();

        for (Task task : tasks) {
            String customerIdStr = (String) runtimeService.getVariable(task.getExecutionId(), "customerId");
            UUID customerId = customerIdStr != null ? UUID.fromString(customerIdStr) : null;
            
            String customerName = "Corporate Banking Client";
            String action = "DEDICATED_CIB_RELATIONSHIP_MANAGER_OUTREACH_AND_15_PERCENT_FEE_DISCOUNT";
            Integer discount = 15;

            if (customerId != null) {
                Customer customer = customerRepository.findById(customerId).orElse(null);
                if (customer != null) {
                    customerName = customer.getName();
                }
                List<RecoveryPlan> plans = recoveryPlanRepository.findByCustomerId(customerId);
                if (!plans.isEmpty()) {
                    RecoveryPlan plan = plans.get(plans.size() - 1);
                    action = plan.getRecommendedAction();
                    discount = plan.getDiscountPercentage();
                }
            }

            dtos.add(WorkflowDTO.builder()
                    .taskId(task.getId())
                    .taskName(task.getName())
                    .workflowInstanceId(task.getProcessInstanceId())
                    .customerId(customerId)
                    .customerName(customerName)
                    .recommendedAction(action)
                    .discountPercentage(discount)
                    .status("PENDING_APPROVAL")
                    .build());
        }

        return dtos;
    }

    public WorkflowDTO completeManagerTask(String taskId, boolean approved) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new IllegalArgumentException("Task not found with ID: " + taskId);
        }

        String customerIdStr = (String) runtimeService.getVariable(task.getExecutionId(), "customerId");
        UUID customerId = customerIdStr != null ? UUID.fromString(customerIdStr) : null;

        Map<String, Object> taskVariables = Map.of(
                "approved", approved,
                "requiresApproval", false
        );

        taskService.complete(taskId, taskVariables);

        if (customerId != null) {
            List<RecoveryPlan> plans = recoveryPlanRepository.findByCustomerId(customerId);
            if (!plans.isEmpty()) {
                RecoveryPlan plan = plans.get(plans.size() - 1);
                plan.setStatus(approved ? "APPROVED" : "REJECTED");
                plan.setOutcomeNotes(approved ? "Approved by Relationship Manager." : "Rejected by Relationship Manager.");
                recoveryPlanRepository.save(plan);
            }
        }

        return WorkflowDTO.builder()
                .taskId(taskId)
                .workflowInstanceId(task.getProcessInstanceId())
                .customerId(customerId)
                .status(approved ? "APPROVED" : "REJECTED")
                .build();
    }
}

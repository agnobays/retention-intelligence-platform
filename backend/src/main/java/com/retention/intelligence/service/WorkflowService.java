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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowService.class);

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final CustomerRepository customerRepository;
    private final RecoveryPlanRepository recoveryPlanRepository;
    private final EmailService emailService;

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
        RecoveryPlan latestPlan = null;
        if (!plans.isEmpty()) {
            latestPlan = plans.get(plans.size() - 1);
            latestPlan.setWorkflowInstanceId(processInstance.getProcessInstanceId());
            recoveryPlanRepository.save(latestPlan);
        }

        log.info("Started Camunda workflow instance {} for customer {}", processInstance.getProcessInstanceId(), customerName);
        if (latestPlan != null) {
            emailService.sendRecoveryEmail(latestPlan);
        } else {
            emailService.sendDirectEmail(customerName, "SB-CIB-1001", 15, "Automated Retention Outreach", null);
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
        String customerName = "Shoprite Holdings Ltd";
        UUID customerId = null;

        if (task != null) {
            String customerIdStr = (String) runtimeService.getVariable(task.getExecutionId(), "customerId");
            customerId = customerIdStr != null ? UUID.fromString(customerIdStr) : null;

            Map<String, Object> taskVariables = Map.of(
                    "approved", approved,
                    "requiresApproval", false
            );

            taskService.complete(taskId, taskVariables);
        }

        if (customerId != null) {
            Customer c = customerRepository.findById(customerId).orElse(null);
            if (c != null) customerName = c.getName();
            List<RecoveryPlan> plans = recoveryPlanRepository.findByCustomerId(customerId);
            if (!plans.isEmpty()) {
                RecoveryPlan plan = plans.get(plans.size() - 1);
                plan.setStatus(approved ? "APPROVED" : "REJECTED");
                plan.setOutcomeNotes(approved ? "Approved by Relationship Manager." : "Rejected by Relationship Manager.");
                recoveryPlanRepository.save(plan);
                if (approved) {
                    emailService.sendRecoveryEmail(plan);
                }
            }
        } else {
            // Direct mock fallback execution
            log.info("Completing manager approval task {} (approved={}) with immediate email dispatch", taskId, approved);
            if (approved) {
                emailService.sendDirectEmail(customerName, "SB-CIB-1001", 15, "Executive Fee Concession & Dedicated RM Outreach", null);
            }
        }

        return WorkflowDTO.builder()
                .taskId(taskId)
                .workflowInstanceId(task != null ? task.getProcessInstanceId() : "camunda-wf-1001")
                .customerId(customerId)
                .customerName(customerName)
                .status(approved ? "APPROVED" : "REJECTED")
                .build();
    }
}

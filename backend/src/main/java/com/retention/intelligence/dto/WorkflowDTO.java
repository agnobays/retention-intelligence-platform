package com.retention.intelligence.dto;

import java.util.Map;
import java.util.UUID;

public class WorkflowDTO {

    private String processDefinitionKey;
    private String workflowInstanceId;
    private UUID customerId;
    private Map<String, Object> variables;
    private String status;
    private String taskId;
    private String taskName;
    private String customerName;
    private String recommendedAction;
    private Integer discountPercentage;

    public WorkflowDTO() {}

    public WorkflowDTO(String processDefinitionKey, String workflowInstanceId, UUID customerId, Map<String, Object> variables,
                       String status, String taskId, String taskName, String customerName, String recommendedAction, Integer discountPercentage) {
        this.processDefinitionKey = processDefinitionKey;
        this.workflowInstanceId = workflowInstanceId;
        this.customerId = customerId;
        this.variables = variables;
        this.status = status;
        this.taskId = taskId;
        this.taskName = taskName;
        this.customerName = customerName;
        this.recommendedAction = recommendedAction;
        this.discountPercentage = discountPercentage;
    }

    public String getProcessDefinitionKey() { return processDefinitionKey; }
    public void setProcessDefinitionKey(String processDefinitionKey) { this.processDefinitionKey = processDefinitionKey; }

    public String getWorkflowInstanceId() { return workflowInstanceId; }
    public void setWorkflowInstanceId(String workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public Map<String, Object> getVariables() { return variables; }
    public void setVariables(Map<String, Object> variables) { this.variables = variables; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }

    public static WorkflowDTOBuilder builder() { return new WorkflowDTOBuilder(); }

    public static class WorkflowDTOBuilder {
        private String processDefinitionKey;
        private String workflowInstanceId;
        private UUID customerId;
        private Map<String, Object> variables;
        private String status;
        private String taskId;
        private String taskName;
        private String customerName;
        private String recommendedAction;
        private Integer discountPercentage;

        public WorkflowDTOBuilder processDefinitionKey(String processDefinitionKey) { this.processDefinitionKey = processDefinitionKey; return this; }
        public WorkflowDTOBuilder workflowInstanceId(String workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; return this; }
        public WorkflowDTOBuilder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public WorkflowDTOBuilder variables(Map<String, Object> variables) { this.variables = variables; return this; }
        public WorkflowDTOBuilder status(String status) { this.status = status; return this; }
        public WorkflowDTOBuilder taskId(String taskId) { this.taskId = taskId; return this; }
        public WorkflowDTOBuilder taskName(String taskName) { this.taskName = taskName; return this; }
        public WorkflowDTOBuilder customerName(String customerName) { this.customerName = customerName; return this; }
        public WorkflowDTOBuilder recommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; return this; }
        public WorkflowDTOBuilder discountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; return this; }

        public WorkflowDTO build() {
            return new WorkflowDTO(processDefinitionKey, workflowInstanceId, customerId, variables, status, taskId, taskName, customerName, recommendedAction, discountPercentage);
        }
    }
}

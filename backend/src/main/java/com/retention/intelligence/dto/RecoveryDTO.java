package com.retention.intelligence.dto;

import java.util.UUID;

public class RecoveryDTO {

    private UUID planId;
    private UUID customerId;
    private String recommendedAction;
    private Integer discountPercentage;
    private String status;
    private String workflowInstanceId;
    private Boolean requiresApproval;

    public RecoveryDTO() {}

    public RecoveryDTO(UUID planId, UUID customerId, String recommendedAction, Integer discountPercentage,
                       String status, String workflowInstanceId, Boolean requiresApproval) {
        this.planId = planId;
        this.customerId = customerId;
        this.recommendedAction = recommendedAction;
        this.discountPercentage = discountPercentage;
        this.status = status;
        this.workflowInstanceId = workflowInstanceId;
        this.requiresApproval = requiresApproval;
    }

    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getWorkflowInstanceId() { return workflowInstanceId; }
    public void setWorkflowInstanceId(String workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; }

    public Boolean getRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(Boolean requiresApproval) { this.requiresApproval = requiresApproval; }

    public static RecoveryDTOBuilder builder() { return new RecoveryDTOBuilder(); }

    public static class RecoveryDTOBuilder {
        private UUID planId;
        private UUID customerId;
        private String recommendedAction;
        private Integer discountPercentage;
        private String status;
        private String workflowInstanceId;
        private Boolean requiresApproval;

        public RecoveryDTOBuilder planId(UUID planId) { this.planId = planId; return this; }
        public RecoveryDTOBuilder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public RecoveryDTOBuilder recommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; return this; }
        public RecoveryDTOBuilder discountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; return this; }
        public RecoveryDTOBuilder status(String status) { this.status = status; return this; }
        public RecoveryDTOBuilder workflowInstanceId(String workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; return this; }
        public RecoveryDTOBuilder requiresApproval(Boolean requiresApproval) { this.requiresApproval = requiresApproval; return this; }

        public RecoveryDTO build() {
            return new RecoveryDTO(planId, customerId, recommendedAction, discountPercentage, status, workflowInstanceId, requiresApproval);
        }
    }
}

package com.retention.intelligence.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "recovery_plans")
public class RecoveryPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "recommended_action", nullable = false)
    private String recommendedAction;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_manager_id")
    private User assignedManager;

    private String status; // PENDING_APPROVAL, APPROVED, REJECTED, EXECUTING, COMPLETED, FAILED

    @Column(name = "outcome_notes")
    private String outcomeNotes;

    @Column(name = "workflow_instance_id")
    private String workflowInstanceId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;

    public RecoveryPlan() {}

    public RecoveryPlan(UUID id, Customer customer, String recommendedAction, Integer discountPercentage,
                        User assignedManager, String status, String outcomeNotes, String workflowInstanceId,
                        ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.customer = customer;
        this.recommendedAction = recommendedAction;
        this.discountPercentage = discountPercentage;
        this.assignedManager = assignedManager;
        this.status = status;
        this.outcomeNotes = outcomeNotes;
        this.workflowInstanceId = workflowInstanceId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }

    public User getAssignedManager() { return assignedManager; }
    public void setAssignedManager(User assignedManager) { this.assignedManager = assignedManager; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOutcomeNotes() { return outcomeNotes; }
    public void setOutcomeNotes(String outcomeNotes) { this.outcomeNotes = outcomeNotes; }

    public String getWorkflowInstanceId() { return workflowInstanceId; }
    public void setWorkflowInstanceId(String workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static RecoveryPlanBuilder builder() { return new RecoveryPlanBuilder(); }

    public static class RecoveryPlanBuilder {
        private UUID id;
        private Customer customer;
        private String recommendedAction;
        private Integer discountPercentage;
        private User assignedManager;
        private String status;
        private String outcomeNotes;
        private String workflowInstanceId;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public RecoveryPlanBuilder id(UUID id) { this.id = id; return this; }
        public RecoveryPlanBuilder customer(Customer customer) { this.customer = customer; return this; }
        public RecoveryPlanBuilder recommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; return this; }
        public RecoveryPlanBuilder discountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; return this; }
        public RecoveryPlanBuilder assignedManager(User assignedManager) { this.assignedManager = assignedManager; return this; }
        public RecoveryPlanBuilder status(String status) { this.status = status; return this; }
        public RecoveryPlanBuilder outcomeNotes(String outcomeNotes) { this.outcomeNotes = outcomeNotes; return this; }
        public RecoveryPlanBuilder workflowInstanceId(String workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; return this; }
        public RecoveryPlanBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public RecoveryPlanBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public RecoveryPlan build() {
            return new RecoveryPlan(id, customer, recommendedAction, discountPercentage, assignedManager, status, outcomeNotes, workflowInstanceId, createdAt, updatedAt);
        }
    }
}

package com.retention.intelligence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "external_customer_id", nullable = false)
    private String externalCustomerId;

    @Column(nullable = false)
    private String name;

    private String email;

    // Sanisa Segment & Relationship
    @Column(name = "customer_segment")
    private String customerSegment; // PRIVATE_CLIENT, COMMERCIAL_SME, EVERYDAY_BANKING

    private String tenure; // e.g. "8 years", "4 years", "2 years"

    @Column(name = "products_held")
    private String productsHeld; // e.g. "Current Account, Investments, Credit Card, Home Loan"

    private BigDecimal mrr;
    private BigDecimal arr;

    @Column(name = "health_score")
    private Integer healthScore; // 0-100

    @Column(name = "frustration_score")
    private Integer frustrationScore; // 0-100 (e.g., 88, 76, 62)

    @Column(name = "satisfaction_score")
    private Integer satisfactionScore; // 0-100 (e.g., 42)

    @Column(name = "churn_probability")
    private BigDecimal churnProbability;

    private String status; // ACTIVE, AT_RISK, RECOVERING, CHURNED, SAVED

    // Sanisa Journey & Incident Tracking
    @Column(name = "issue_category")
    private String issueCategory; // SERVICE_DELAY, PAYMENT_DISRUPTION, CARD_DISPUTE

    @Column(name = "issue_severity")
    private String issueSeverity; // CRITICAL, HIGH, MEDIUM

    @Column(name = "previous_complaints_count")
    private Integer previousComplaintsCount;

    @Column(name = "resolution_time_hours")
    private Integer resolutionTimeHours;

    @Column(name = "interactions_count")
    private Integer interactionsCount;

    @Column(name = "escalations_count")
    private Integer escalationsCount;

    // Sanisa Recovery & Loyalty Reward
    @Column(name = "recommended_intervention")
    private String recommendedIntervention;

    @Column(name = "reward_category")
    private String rewardCategory; // LIFESTYLE_EXPERIENCE, MERCHANT_FEE_WAIVER, RETAIL_VOUCHER_POINTS

    @Column(name = "reward_value")
    private String rewardValue; // e.g., "R1,500 Lifestyle Voucher", "Merchant Fee Waiver", "5,000 Loyalty Points"

    @Column(name = "reward_redeemed")
    private Boolean rewardRedeemed;

    @Column(name = "post_recovery_score")
    private Integer postRecoveryScore;

    @Column(name = "retention_outcome")
    private String retentionOutcome; // RETAINED, PENDING, LOST

    @Column(name = "contract_renewal_date")
    private LocalDate contractRenewalDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;

    public Customer() {}

    public Customer(UUID id, Company company, String externalCustomerId, String name, String email,
                    String customerSegment, String tenure, String productsHeld, BigDecimal mrr, BigDecimal arr,
                    Integer healthScore, Integer frustrationScore, Integer satisfactionScore, BigDecimal churnProbability,
                    String status, String issueCategory, String issueSeverity, Integer previousComplaintsCount,
                    Integer resolutionTimeHours, Integer interactionsCount, Integer escalationsCount,
                    String recommendedIntervention, String rewardCategory, String rewardValue, Boolean rewardRedeemed,
                    Integer postRecoveryScore, String retentionOutcome, LocalDate contractRenewalDate,
                    ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.company = company;
        this.externalCustomerId = externalCustomerId;
        this.name = name;
        this.email = email;
        this.customerSegment = customerSegment;
        this.tenure = tenure;
        this.productsHeld = productsHeld;
        this.mrr = mrr;
        this.arr = arr;
        this.healthScore = healthScore;
        this.frustrationScore = frustrationScore;
        this.satisfactionScore = satisfactionScore;
        this.churnProbability = churnProbability;
        this.status = status;
        this.issueCategory = issueCategory;
        this.issueSeverity = issueSeverity;
        this.previousComplaintsCount = previousComplaintsCount;
        this.resolutionTimeHours = resolutionTimeHours;
        this.interactionsCount = interactionsCount;
        this.escalationsCount = escalationsCount;
        this.recommendedIntervention = recommendedIntervention;
        this.rewardCategory = rewardCategory;
        this.rewardValue = rewardValue;
        this.rewardRedeemed = rewardRedeemed;
        this.postRecoveryScore = postRecoveryScore;
        this.retentionOutcome = retentionOutcome;
        this.contractRenewalDate = contractRenewalDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getExternalCustomerId() { return externalCustomerId; }
    public void setExternalCustomerId(String externalCustomerId) { this.externalCustomerId = externalCustomerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCustomerSegment() { return customerSegment; }
    public void setCustomerSegment(String customerSegment) { this.customerSegment = customerSegment; }

    public String getTenure() { return tenure; }
    public void setTenure(String tenure) { this.tenure = tenure; }

    public String getProductsHeld() { return productsHeld; }
    public void setProductsHeld(String productsHeld) { this.productsHeld = productsHeld; }

    public BigDecimal getMrr() { return mrr; }
    public void setMrr(BigDecimal mrr) { this.mrr = mrr; }

    public BigDecimal getArr() { return arr; }
    public void setArr(BigDecimal arr) { this.arr = arr; }

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }

    public Integer getFrustrationScore() { return frustrationScore; }
    public void setFrustrationScore(Integer frustrationScore) { this.frustrationScore = frustrationScore; }

    public Integer getSatisfactionScore() { return satisfactionScore; }
    public void setSatisfactionScore(Integer satisfactionScore) { this.satisfactionScore = satisfactionScore; }

    public BigDecimal getChurnProbability() { return churnProbability; }
    public void setChurnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIssueCategory() { return issueCategory; }
    public void setIssueCategory(String issueCategory) { this.issueCategory = issueCategory; }

    public String getIssueSeverity() { return issueSeverity; }
    public void setIssueSeverity(String issueSeverity) { this.issueSeverity = issueSeverity; }

    public Integer getPreviousComplaintsCount() { return previousComplaintsCount; }
    public void setPreviousComplaintsCount(Integer previousComplaintsCount) { this.previousComplaintsCount = previousComplaintsCount; }

    public Integer getResolutionTimeHours() { return resolutionTimeHours; }
    public void setResolutionTimeHours(Integer resolutionTimeHours) { this.resolutionTimeHours = resolutionTimeHours; }

    public Integer getInteractionsCount() { return interactionsCount; }
    public void setInteractionsCount(Integer interactionsCount) { this.interactionsCount = interactionsCount; }

    public Integer getEscalationsCount() { return escalationsCount; }
    public void setEscalationsCount(Integer escalationsCount) { this.escalationsCount = escalationsCount; }

    public String getRecommendedIntervention() { return recommendedIntervention; }
    public void setRecommendedIntervention(String recommendedIntervention) { this.recommendedIntervention = recommendedIntervention; }

    public String getRewardCategory() { return rewardCategory; }
    public void setRewardCategory(String rewardCategory) { this.rewardCategory = rewardCategory; }

    public String getRewardValue() { return rewardValue; }
    public void setRewardValue(String rewardValue) { this.rewardValue = rewardValue; }

    public Boolean getRewardRedeemed() { return rewardRedeemed; }
    public void setRewardRedeemed(Boolean rewardRedeemed) { this.rewardRedeemed = rewardRedeemed; }

    public Integer getPostRecoveryScore() { return postRecoveryScore; }
    public void setPostRecoveryScore(Integer postRecoveryScore) { this.postRecoveryScore = postRecoveryScore; }

    public String getRetentionOutcome() { return retentionOutcome; }
    public void setRetentionOutcome(String retentionOutcome) { this.retentionOutcome = retentionOutcome; }

    public LocalDate getContractRenewalDate() { return contractRenewalDate; }
    public void setContractRenewalDate(LocalDate contractRenewalDate) { this.contractRenewalDate = contractRenewalDate; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static CustomerBuilder builder() { return new CustomerBuilder(); }

    public static class CustomerBuilder {
        private UUID id;
        private Company company;
        private String externalCustomerId;
        private String name;
        private String email;
        private String customerSegment;
        private String tenure;
        private String productsHeld;
        private BigDecimal mrr;
        private BigDecimal arr;
        private Integer healthScore;
        private Integer frustrationScore;
        private Integer satisfactionScore;
        private BigDecimal churnProbability;
        private String status;
        private String issueCategory;
        private String issueSeverity;
        private Integer previousComplaintsCount;
        private Integer resolutionTimeHours;
        private Integer interactionsCount;
        private Integer escalationsCount;
        private String recommendedIntervention;
        private String rewardCategory;
        private String rewardValue;
        private Boolean rewardRedeemed;
        private Integer postRecoveryScore;
        private String retentionOutcome;
        private LocalDate contractRenewalDate;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public CustomerBuilder id(UUID id) { this.id = id; return this; }
        public CustomerBuilder company(Company company) { this.company = company; return this; }
        public CustomerBuilder externalCustomerId(String externalCustomerId) { this.externalCustomerId = externalCustomerId; return this; }
        public CustomerBuilder name(String name) { this.name = name; return this; }
        public CustomerBuilder email(String email) { this.email = email; return this; }
        public CustomerBuilder customerSegment(String customerSegment) { this.customerSegment = customerSegment; return this; }
        public CustomerBuilder tenure(String tenure) { this.tenure = tenure; return this; }
        public CustomerBuilder productsHeld(String productsHeld) { this.productsHeld = productsHeld; return this; }
        public CustomerBuilder mrr(BigDecimal mrr) { this.mrr = mrr; return this; }
        public CustomerBuilder arr(BigDecimal arr) { this.arr = arr; return this; }
        public CustomerBuilder healthScore(Integer healthScore) { this.healthScore = healthScore; return this; }
        public CustomerBuilder frustrationScore(Integer frustrationScore) { this.frustrationScore = frustrationScore; return this; }
        public CustomerBuilder satisfactionScore(Integer satisfactionScore) { this.satisfactionScore = satisfactionScore; return this; }
        public CustomerBuilder churnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; return this; }
        public CustomerBuilder status(String status) { this.status = status; return this; }
        public CustomerBuilder issueCategory(String issueCategory) { this.issueCategory = issueCategory; return this; }
        public CustomerBuilder issueSeverity(String issueSeverity) { this.issueSeverity = issueSeverity; return this; }
        public CustomerBuilder previousComplaintsCount(Integer previousComplaintsCount) { this.previousComplaintsCount = previousComplaintsCount; return this; }
        public CustomerBuilder resolutionTimeHours(Integer resolutionTimeHours) { this.resolutionTimeHours = resolutionTimeHours; return this; }
        public CustomerBuilder interactionsCount(Integer interactionsCount) { this.interactionsCount = interactionsCount; return this; }
        public CustomerBuilder escalationsCount(Integer escalationsCount) { this.escalationsCount = escalationsCount; return this; }
        public CustomerBuilder recommendedIntervention(String recommendedIntervention) { this.recommendedIntervention = recommendedIntervention; return this; }
        public CustomerBuilder rewardCategory(String rewardCategory) { this.rewardCategory = rewardCategory; return this; }
        public CustomerBuilder rewardValue(String rewardValue) { this.rewardValue = rewardValue; return this; }
        public CustomerBuilder rewardRedeemed(Boolean rewardRedeemed) { this.rewardRedeemed = rewardRedeemed; return this; }
        public CustomerBuilder postRecoveryScore(Integer postRecoveryScore) { this.postRecoveryScore = postRecoveryScore; return this; }
        public CustomerBuilder retentionOutcome(String retentionOutcome) { this.retentionOutcome = retentionOutcome; return this; }
        public CustomerBuilder contractRenewalDate(LocalDate contractRenewalDate) { this.contractRenewalDate = contractRenewalDate; return this; }
        public CustomerBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public CustomerBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Customer build() {
            return new Customer(id, company, externalCustomerId, name, email, customerSegment, tenure, productsHeld, mrr, arr,
                    healthScore, frustrationScore, satisfactionScore, churnProbability, status, issueCategory, issueSeverity,
                    previousComplaintsCount, resolutionTimeHours, interactionsCount, escalationsCount, recommendedIntervention,
                    rewardCategory, rewardValue, rewardRedeemed, postRecoveryScore, retentionOutcome, contractRenewalDate, createdAt, updatedAt);
        }
    }
}

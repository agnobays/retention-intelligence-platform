package com.retention.intelligence.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CustomerDTO {

    private UUID id;
    private UUID companyId;
    private String externalCustomerId;
    private String name;
    private String email;
    private String customerSegment; // PRIVATE_CLIENT, COMMERCIAL_SME, EVERYDAY_BANKING
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

    public CustomerDTO() {}

    public CustomerDTO(UUID id, UUID companyId, String externalCustomerId, String name, String email,
                       String customerSegment, String tenure, String productsHeld, BigDecimal mrr, BigDecimal arr,
                       Integer healthScore, Integer frustrationScore, Integer satisfactionScore, BigDecimal churnProbability,
                       String status, String issueCategory, String issueSeverity, Integer previousComplaintsCount,
                       Integer resolutionTimeHours, Integer interactionsCount, Integer escalationsCount,
                       String recommendedIntervention, String rewardCategory, String rewardValue, Boolean rewardRedeemed,
                       Integer postRecoveryScore, String retentionOutcome, LocalDate contractRenewalDate) {
        this.id = id;
        this.companyId = companyId;
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
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

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

    public static CustomerDTOBuilder builder() { return new CustomerDTOBuilder(); }

    public static class CustomerDTOBuilder {
        private UUID id;
        private UUID companyId;
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

        public CustomerDTOBuilder id(UUID id) { this.id = id; return this; }
        public CustomerDTOBuilder companyId(UUID companyId) { this.companyId = companyId; return this; }
        public CustomerDTOBuilder externalCustomerId(String externalCustomerId) { this.externalCustomerId = externalCustomerId; return this; }
        public CustomerDTOBuilder name(String name) { this.name = name; return this; }
        public CustomerDTOBuilder email(String email) { this.email = email; return this; }
        public CustomerDTOBuilder customerSegment(String customerSegment) { this.customerSegment = customerSegment; return this; }
        public CustomerDTOBuilder tenure(String tenure) { this.tenure = tenure; return this; }
        public CustomerDTOBuilder productsHeld(String productsHeld) { this.productsHeld = productsHeld; return this; }
        public CustomerDTOBuilder mrr(BigDecimal mrr) { this.mrr = mrr; return this; }
        public CustomerDTOBuilder arr(BigDecimal arr) { this.arr = arr; return this; }
        public CustomerDTOBuilder healthScore(Integer healthScore) { this.healthScore = healthScore; return this; }
        public CustomerDTOBuilder frustrationScore(Integer frustrationScore) { this.frustrationScore = frustrationScore; return this; }
        public CustomerDTOBuilder satisfactionScore(Integer satisfactionScore) { this.satisfactionScore = satisfactionScore; return this; }
        public CustomerDTOBuilder churnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; return this; }
        public CustomerDTOBuilder status(String status) { this.status = status; return this; }
        public CustomerDTOBuilder issueCategory(String issueCategory) { this.issueCategory = issueCategory; return this; }
        public CustomerDTOBuilder issueSeverity(String issueSeverity) { this.issueSeverity = issueSeverity; return this; }
        public CustomerDTOBuilder previousComplaintsCount(Integer previousComplaintsCount) { this.previousComplaintsCount = previousComplaintsCount; return this; }
        public CustomerDTOBuilder resolutionTimeHours(Integer resolutionTimeHours) { this.resolutionTimeHours = resolutionTimeHours; return this; }
        public CustomerDTOBuilder interactionsCount(Integer interactionsCount) { this.interactionsCount = interactionsCount; return this; }
        public CustomerDTOBuilder escalationsCount(Integer escalationsCount) { this.escalationsCount = escalationsCount; return this; }
        public CustomerDTOBuilder recommendedIntervention(String recommendedIntervention) { this.recommendedIntervention = recommendedIntervention; return this; }
        public CustomerDTOBuilder rewardCategory(String rewardCategory) { this.rewardCategory = rewardCategory; return this; }
        public CustomerDTOBuilder rewardValue(String rewardValue) { this.rewardValue = rewardValue; return this; }
        public CustomerDTOBuilder rewardRedeemed(Boolean rewardRedeemed) { this.rewardRedeemed = rewardRedeemed; return this; }
        public CustomerDTOBuilder postRecoveryScore(Integer postRecoveryScore) { this.postRecoveryScore = postRecoveryScore; return this; }
        public CustomerDTOBuilder retentionOutcome(String retentionOutcome) { this.retentionOutcome = retentionOutcome; return this; }
        public CustomerDTOBuilder contractRenewalDate(LocalDate contractRenewalDate) { this.contractRenewalDate = contractRenewalDate; return this; }

        public CustomerDTO build() {
            return new CustomerDTO(id, companyId, externalCustomerId, name, email, customerSegment, tenure, productsHeld, mrr, arr,
                    healthScore, frustrationScore, satisfactionScore, churnProbability, status, issueCategory, issueSeverity,
                    previousComplaintsCount, resolutionTimeHours, interactionsCount, escalationsCount, recommendedIntervention,
                    rewardCategory, rewardValue, rewardRedeemed, postRecoveryScore, retentionOutcome, contractRenewalDate);
        }
    }
}

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
    private BigDecimal mrr;
    private BigDecimal arr;
    private Integer healthScore;
    private BigDecimal churnProbability;
    private String status;
    private LocalDate contractRenewalDate;

    public CustomerDTO() {}

    public CustomerDTO(UUID id, UUID companyId, String externalCustomerId, String name, String email,
                       BigDecimal mrr, BigDecimal arr, Integer healthScore, BigDecimal churnProbability,
                       String status, LocalDate contractRenewalDate) {
        this.id = id;
        this.companyId = companyId;
        this.externalCustomerId = externalCustomerId;
        this.name = name;
        this.email = email;
        this.mrr = mrr;
        this.arr = arr;
        this.healthScore = healthScore;
        this.churnProbability = churnProbability;
        this.status = status;
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

    public BigDecimal getMrr() { return mrr; }
    public void setMrr(BigDecimal mrr) { this.mrr = mrr; }

    public BigDecimal getArr() { return arr; }
    public void setArr(BigDecimal arr) { this.arr = arr; }

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }

    public BigDecimal getChurnProbability() { return churnProbability; }
    public void setChurnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getContractRenewalDate() { return contractRenewalDate; }
    public void setContractRenewalDate(LocalDate contractRenewalDate) { this.contractRenewalDate = contractRenewalDate; }

    public static CustomerDTOBuilder builder() { return new CustomerDTOBuilder(); }

    public static class CustomerDTOBuilder {
        private UUID id;
        private UUID companyId;
        private String externalCustomerId;
        private String name;
        private String email;
        private BigDecimal mrr;
        private BigDecimal arr;
        private Integer healthScore;
        private BigDecimal churnProbability;
        private String status;
        private LocalDate contractRenewalDate;

        public CustomerDTOBuilder id(UUID id) { this.id = id; return this; }
        public CustomerDTOBuilder companyId(UUID companyId) { this.companyId = companyId; return this; }
        public CustomerDTOBuilder externalCustomerId(String externalCustomerId) { this.externalCustomerId = externalCustomerId; return this; }
        public CustomerDTOBuilder name(String name) { this.name = name; return this; }
        public CustomerDTOBuilder email(String email) { this.email = email; return this; }
        public CustomerDTOBuilder mrr(BigDecimal mrr) { this.mrr = mrr; return this; }
        public CustomerDTOBuilder arr(BigDecimal arr) { this.arr = arr; return this; }
        public CustomerDTOBuilder healthScore(Integer healthScore) { this.healthScore = healthScore; return this; }
        public CustomerDTOBuilder churnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; return this; }
        public CustomerDTOBuilder status(String status) { this.status = status; return this; }
        public CustomerDTOBuilder contractRenewalDate(LocalDate contractRenewalDate) { this.contractRenewalDate = contractRenewalDate; return this; }

        public CustomerDTO build() {
            return new CustomerDTO(id, companyId, externalCustomerId, name, email, mrr, arr, healthScore, churnProbability, status, contractRenewalDate);
        }
    }
}

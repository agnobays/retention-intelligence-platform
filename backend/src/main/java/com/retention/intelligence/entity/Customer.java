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
    private BigDecimal mrr;
    private BigDecimal arr;

    @Column(name = "health_score")
    private Integer healthScore;

    @Column(name = "churn_probability")
    private BigDecimal churnProbability;

    private String status; // ACTIVE, AT_RISK, RECOVERING, CHURNED, SAVED

    @Column(name = "contract_renewal_date")
    private LocalDate contractRenewalDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;

    public Customer() {}

    public Customer(UUID id, Company company, String externalCustomerId, String name, String email,
                    BigDecimal mrr, BigDecimal arr, Integer healthScore, BigDecimal churnProbability,
                    String status, LocalDate contractRenewalDate, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.company = company;
        this.externalCustomerId = externalCustomerId;
        this.name = name;
        this.email = email;
        this.mrr = mrr;
        this.arr = arr;
        this.healthScore = healthScore;
        this.churnProbability = churnProbability;
        this.status = status;
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
        private BigDecimal mrr;
        private BigDecimal arr;
        private Integer healthScore;
        private BigDecimal churnProbability;
        private String status;
        private LocalDate contractRenewalDate;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public CustomerBuilder id(UUID id) { this.id = id; return this; }
        public CustomerBuilder company(Company company) { this.company = company; return this; }
        public CustomerBuilder externalCustomerId(String externalCustomerId) { this.externalCustomerId = externalCustomerId; return this; }
        public CustomerBuilder name(String name) { this.name = name; return this; }
        public CustomerBuilder email(String email) { this.email = email; return this; }
        public CustomerBuilder mrr(BigDecimal mrr) { this.mrr = mrr; return this; }
        public CustomerBuilder arr(BigDecimal arr) { this.arr = arr; return this; }
        public CustomerBuilder healthScore(Integer healthScore) { this.healthScore = healthScore; return this; }
        public CustomerBuilder churnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; return this; }
        public CustomerBuilder status(String status) { this.status = status; return this; }
        public CustomerBuilder contractRenewalDate(LocalDate contractRenewalDate) { this.contractRenewalDate = contractRenewalDate; return this; }
        public CustomerBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public CustomerBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Customer build() {
            return new Customer(id, company, externalCustomerId, name, email, mrr, arr, healthScore, churnProbability, status, contractRenewalDate, createdAt, updatedAt);
        }
    }
}

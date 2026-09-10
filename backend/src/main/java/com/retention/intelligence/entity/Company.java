package com.retention.intelligence.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String domain;

    private String industry;

    @Column(name = "subscription_tier")
    private String subscriptionTier;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;

    public Company() {}

    public Company(UUID id, String name, String domain, String industry, String subscriptionTier, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.industry = industry;
        this.subscriptionTier = subscriptionTier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getSubscriptionTier() { return subscriptionTier; }
    public void setSubscriptionTier(String subscriptionTier) { this.subscriptionTier = subscriptionTier; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static CompanyBuilder builder() { return new CompanyBuilder(); }

    public static class CompanyBuilder {
        private UUID id;
        private String name;
        private String domain;
        private String industry;
        private String subscriptionTier;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public CompanyBuilder id(UUID id) { this.id = id; return this; }
        public CompanyBuilder name(String name) { this.name = name; return this; }
        public CompanyBuilder domain(String domain) { this.domain = domain; return this; }
        public CompanyBuilder industry(String industry) { this.industry = industry; return this; }
        public CompanyBuilder subscriptionTier(String subscriptionTier) { this.subscriptionTier = subscriptionTier; return this; }
        public CompanyBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public CompanyBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Company build() {
            return new Company(id, name, domain, industry, subscriptionTier, createdAt, updatedAt);
        }
    }
}

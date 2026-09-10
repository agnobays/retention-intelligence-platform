package com.retention.intelligence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_value_scores")
public class CustomerValueScore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private BigDecimal ltv;

    @Column(name = "usage_frequency_score")
    private Integer usageFrequencyScore;

    @Column(name = "support_ticket_volume")
    private Integer supportTicketVolume;

    @Column(name = "sla_tier")
    private String slaTier;

    @Column(name = "strategic_value_tier")
    private String strategicValueTier;

    @Column(name = "calculated_at", insertable = false, updatable = false)
    private ZonedDateTime calculatedAt;

    public CustomerValueScore() {}

    public CustomerValueScore(UUID id, Customer customer, BigDecimal ltv, Integer usageFrequencyScore,
                              Integer supportTicketVolume, String slaTier, String strategicValueTier, ZonedDateTime calculatedAt) {
        this.id = id;
        this.customer = customer;
        this.ltv = ltv;
        this.usageFrequencyScore = usageFrequencyScore;
        this.supportTicketVolume = supportTicketVolume;
        this.slaTier = slaTier;
        this.strategicValueTier = strategicValueTier;
        this.calculatedAt = calculatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public BigDecimal getLtv() { return ltv; }
    public void setLtv(BigDecimal ltv) { this.ltv = ltv; }

    public Integer getUsageFrequencyScore() { return usageFrequencyScore; }
    public void setUsageFrequencyScore(Integer usageFrequencyScore) { this.usageFrequencyScore = usageFrequencyScore; }

    public Integer getSupportTicketVolume() { return supportTicketVolume; }
    public void setSupportTicketVolume(Integer supportTicketVolume) { this.supportTicketVolume = supportTicketVolume; }

    public String getSlaTier() { return slaTier; }
    public void setSlaTier(String slaTier) { this.slaTier = slaTier; }

    public String getStrategicValueTier() { return strategicValueTier; }
    public void setStrategicValueTier(String strategicValueTier) { this.strategicValueTier = strategicValueTier; }

    public ZonedDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(ZonedDateTime calculatedAt) { this.calculatedAt = calculatedAt; }

    public static CustomerValueScoreBuilder builder() { return new CustomerValueScoreBuilder(); }

    public static class CustomerValueScoreBuilder {
        private UUID id;
        private Customer customer;
        private BigDecimal ltv;
        private Integer usageFrequencyScore;
        private Integer supportTicketVolume;
        private String slaTier;
        private String strategicValueTier;
        private ZonedDateTime calculatedAt;

        public CustomerValueScoreBuilder id(UUID id) { this.id = id; return this; }
        public CustomerValueScoreBuilder customer(Customer customer) { this.customer = customer; return this; }
        public CustomerValueScoreBuilder ltv(BigDecimal ltv) { this.ltv = ltv; return this; }
        public CustomerValueScoreBuilder usageFrequencyScore(Integer usageFrequencyScore) { this.usageFrequencyScore = usageFrequencyScore; return this; }
        public CustomerValueScoreBuilder supportTicketVolume(Integer supportTicketVolume) { this.supportTicketVolume = supportTicketVolume; return this; }
        public CustomerValueScoreBuilder slaTier(String slaTier) { this.slaTier = slaTier; return this; }
        public CustomerValueScoreBuilder strategicValueTier(String strategicValueTier) { this.strategicValueTier = strategicValueTier; return this; }
        public CustomerValueScoreBuilder calculatedAt(ZonedDateTime calculatedAt) { this.calculatedAt = calculatedAt; return this; }

        public CustomerValueScore build() {
            return new CustomerValueScore(id, customer, ltv, usageFrequencyScore, supportTicketVolume, slaTier, strategicValueTier, calculatedAt);
        }
    }
}

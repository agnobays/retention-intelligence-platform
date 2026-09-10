package com.retention.intelligence.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "at_risk_metrics")
public class AtRiskMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "metric_type", nullable = false)
    private String metricType;

    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "metric_value")
    private String metricValue;

    @Column(name = "detected_at", insertable = false, updatable = false)
    private ZonedDateTime detectedAt;

    public AtRiskMetric() {}

    public AtRiskMetric(UUID id, Customer customer, String metricType, String severity, String metricValue, ZonedDateTime detectedAt) {
        this.id = id;
        this.customer = customer;
        this.metricType = metricType;
        this.severity = severity;
        this.metricValue = metricValue;
        this.detectedAt = detectedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getMetricValue() { return metricValue; }
    public void setMetricValue(String metricValue) { this.metricValue = metricValue; }

    public ZonedDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(ZonedDateTime detectedAt) { this.detectedAt = detectedAt; }

    public static AtRiskMetricBuilder builder() { return new AtRiskMetricBuilder(); }

    public static class AtRiskMetricBuilder {
        private UUID id;
        private Customer customer;
        private String metricType;
        private String severity;
        private String metricValue;
        private ZonedDateTime detectedAt;

        public AtRiskMetricBuilder id(UUID id) { this.id = id; return this; }
        public AtRiskMetricBuilder customer(Customer customer) { this.customer = customer; return this; }
        public AtRiskMetricBuilder metricType(String metricType) { this.metricType = metricType; return this; }
        public AtRiskMetricBuilder severity(String severity) { this.severity = severity; return this; }
        public AtRiskMetricBuilder metricValue(String metricValue) { this.metricValue = metricValue; return this; }
        public AtRiskMetricBuilder detectedAt(ZonedDateTime detectedAt) { this.detectedAt = detectedAt; return this; }

        public AtRiskMetric build() {
            return new AtRiskMetric(id, customer, metricType, severity, metricValue, detectedAt);
        }
    }
}

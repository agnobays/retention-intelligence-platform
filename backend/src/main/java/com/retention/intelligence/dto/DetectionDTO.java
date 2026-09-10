package com.retention.intelligence.dto;

import java.util.UUID;

public class DetectionDTO {

    private UUID customerId;
    private String metricType;
    private String severity;
    private String metricValue;
    private String status;
    private Double churnProbability;
    private Integer healthScore;

    public DetectionDTO() {}

    public DetectionDTO(UUID customerId, String metricType, String severity, String metricValue, String status, Double churnProbability, Integer healthScore) {
        this.customerId = customerId;
        this.metricType = metricType;
        this.severity = severity;
        this.metricValue = metricValue;
        this.status = status;
        this.churnProbability = churnProbability;
        this.healthScore = healthScore;
    }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getMetricValue() { return metricValue; }
    public void setMetricValue(String metricValue) { this.metricValue = metricValue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getChurnProbability() { return churnProbability; }
    public void setChurnProbability(Double churnProbability) { this.churnProbability = churnProbability; }

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }

    public static DetectionDTOBuilder builder() { return new DetectionDTOBuilder(); }

    public static class DetectionDTOBuilder {
        private UUID customerId;
        private String metricType;
        private String severity;
        private String metricValue;
        private String status;
        private Double churnProbability;
        private Integer healthScore;

        public DetectionDTOBuilder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public DetectionDTOBuilder metricType(String metricType) { this.metricType = metricType; return this; }
        public DetectionDTOBuilder severity(String severity) { this.severity = severity; return this; }
        public DetectionDTOBuilder metricValue(String metricValue) { this.metricValue = metricValue; return this; }
        public DetectionDTOBuilder status(String status) { this.status = status; return this; }
        public DetectionDTOBuilder churnProbability(Double churnProbability) { this.churnProbability = churnProbability; return this; }
        public DetectionDTOBuilder healthScore(Integer healthScore) { this.healthScore = healthScore; return this; }

        public DetectionDTO build() {
            return new DetectionDTO(customerId, metricType, severity, metricValue, status, churnProbability, healthScore);
        }
    }
}

package com.retention.intelligence.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CustomerValueDTO {

    private UUID customerId;
    private BigDecimal ltv;
    private Integer usageFrequencyScore;
    private Integer supportTicketVolume;
    private String slaTier;
    private String strategicValueTier;

    public CustomerValueDTO() {}

    public CustomerValueDTO(UUID customerId, BigDecimal ltv, Integer usageFrequencyScore, Integer supportTicketVolume, String slaTier, String strategicValueTier) {
        this.customerId = customerId;
        this.ltv = ltv;
        this.usageFrequencyScore = usageFrequencyScore;
        this.supportTicketVolume = supportTicketVolume;
        this.slaTier = slaTier;
        this.strategicValueTier = strategicValueTier;
    }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

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

    public static CustomerValueDTOBuilder builder() { return new CustomerValueDTOBuilder(); }

    public static class CustomerValueDTOBuilder {
        private UUID customerId;
        private BigDecimal ltv;
        private Integer usageFrequencyScore;
        private Integer supportTicketVolume;
        private String slaTier;
        private String strategicValueTier;

        public CustomerValueDTOBuilder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public CustomerValueDTOBuilder ltv(BigDecimal ltv) { this.ltv = ltv; return this; }
        public CustomerValueDTOBuilder usageFrequencyScore(Integer usageFrequencyScore) { this.usageFrequencyScore = usageFrequencyScore; return this; }
        public CustomerValueDTOBuilder supportTicketVolume(Integer supportTicketVolume) { this.supportTicketVolume = supportTicketVolume; return this; }
        public CustomerValueDTOBuilder slaTier(String slaTier) { this.slaTier = slaTier; return this; }
        public CustomerValueDTOBuilder strategicValueTier(String strategicValueTier) { this.strategicValueTier = strategicValueTier; return this; }

        public CustomerValueDTO build() {
            return new CustomerValueDTO(customerId, ltv, usageFrequencyScore, supportTicketVolume, slaTier, strategicValueTier);
        }
    }
}

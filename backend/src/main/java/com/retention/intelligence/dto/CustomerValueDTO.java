package com.retention.intelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerValueDTO {

    private UUID customerId;
    private BigDecimal ltv;
    private Integer usageFrequencyScore;
    private Integer supportTicketVolume;
    private String slaTier;
    private String strategicValueTier;
}

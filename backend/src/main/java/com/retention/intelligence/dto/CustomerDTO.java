package com.retention.intelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}

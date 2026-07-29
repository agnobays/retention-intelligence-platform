package com.retention.intelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryDTO {

    private UUID planId;
    private UUID customerId;
    private String recommendedAction;
    private Integer discountPercentage;
    private String status;
    private String workflowInstanceId;
}

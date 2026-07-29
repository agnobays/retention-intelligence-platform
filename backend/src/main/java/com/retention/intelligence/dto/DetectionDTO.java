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
public class DetectionDTO {

    private UUID customerId;
    private String metricType;
    private String severity;
    private String metricValue;
    private String status;
}

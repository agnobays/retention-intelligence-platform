package com.retention.intelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StressTestResultDTO {
    private String testName;
    private String tenantTested;
    private String isolatedDatabase;
    private int totalAccountsSimulated;
    private int totalEventsProcessed;
    private long totalDurationMs;
    private double throughputEventsPerSecond;
    private int atRiskDetections;
    private int automatedInterventions;
    private int workflowsTriggered;
    private boolean multiTenantIsolationVerified;
    private String isolationAuditMessage;
    private List<String> sampleProcessedAccounts;
    private Map<String, Object> performanceMetrics;
}

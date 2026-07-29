package com.retention.intelligence.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportingService {

    public Map<String, Object> getExecutiveDashboardMetrics() {
        return Map.of(
                "totalCustomers", 1420,
                "atRiskCount", 48,
                "savedArr", 345000.00,
                "recoverySuccessRate", 84.2,
                "activeWorkflows", 12
        );
    }
}

package com.retention.intelligence.service;

import com.retention.intelligence.dto.DetectionDTO;
import com.retention.intelligence.entity.AtRiskMetric;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.exception.ResourceNotFoundException;
import com.retention.intelligence.repository.AtRiskMetricRepository;
import com.retention.intelligence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DetectionEngineService {

    private static final Logger log = LoggerFactory.getLogger(DetectionEngineService.class);

    private final CustomerRepository customerRepository;
    private final AtRiskMetricRepository atRiskMetricRepository;

    public DetectionDTO runDetectionForCustomer(UUID customerId) {
        return runDetectionForCustomerInternal(customerId);
    }

    public DetectionDTO runDetectionForCustomerInternal(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        List<AtRiskMetric> existingMetrics = atRiskMetricRepository.findByCustomerId(customerId);

        String metricType;
        String severity;
        String metricValue;

        if (!existingMetrics.isEmpty()) {
            AtRiskMetric metric = existingMetrics.get(0);
            metricType = metric.getMetricType();
            severity = metric.getSeverity();
            metricValue = metric.getMetricValue();
        } else {
            // Derive generic operational metric from customer attributes if available
            if (customer.getIssueCategory() != null && !customer.getIssueCategory().isBlank()) {
                metricType = customer.getIssueCategory().toUpperCase().replaceAll("[^A-Z0-9_]", "_");
                severity = customer.getIssueSeverity() != null ? customer.getIssueSeverity() : "HIGH";
                metricValue = customer.getIssueCategory();
            } else {
                metricType = "OPERATIONAL_ENGAGEMENT_DROP";
                severity = "HIGH";
                metricValue = "Significant decline in operational transaction volume & customer activity";
            }

            AtRiskMetric newMetric = AtRiskMetric.builder()
                    .customer(customer)
                    .metricType(metricType)
                    .severity(severity)
                    .metricValue(metricValue)
                    .build();
            atRiskMetricRepository.save(newMetric);
        }

        int updatedHealthScore = customer.getHealthScore() != null ? customer.getHealthScore() : 45;
        BigDecimal updatedChurnProb = customer.getChurnProbability() != null && customer.getChurnProbability().doubleValue() > 0
                ? customer.getChurnProbability()
                : new BigDecimal("75.00");

        customer.setHealthScore(updatedHealthScore);
        customer.setChurnProbability(updatedChurnProb);

        if (updatedHealthScore < 60 || updatedChurnProb.doubleValue() > 50.0) {
            customer.setStatus("AT_RISK");
        }

        customerRepository.save(customer);

        log.info("Evaluated risk detection for customer {}: Status={}, HealthScore={}, ChurnProb={}%",
                customer.getName(), customer.getStatus(), updatedHealthScore, updatedChurnProb);

        return DetectionDTO.builder()
                .customerId(customerId)
                .metricType(metricType)
                .severity(severity)
                .metricValue(metricValue)
                .status(customer.getStatus())
                .churnProbability(updatedChurnProb.doubleValue())
                .healthScore(updatedHealthScore)
                .build();
    }
}

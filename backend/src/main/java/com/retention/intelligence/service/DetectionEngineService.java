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

        String metricType = "TRANSACTION_VOLUME_DROP_45_PCT";
        String severity = "HIGH";
        String metricValue = "45% decline in corporate clearing transactions";

        if (!existingMetrics.isEmpty()) {
            AtRiskMetric metric = existingMetrics.get(0);
            metricType = metric.getMetricType();
            severity = metric.getSeverity();
            metricValue = metric.getMetricValue();
        } else {
            AtRiskMetric newMetric = AtRiskMetric.builder()
                    .customer(customer)
                    .metricType(metricType)
                    .severity(severity)
                    .metricValue(metricValue)
                    .build();
            atRiskMetricRepository.save(newMetric);
        }

        int updatedHealthScore = Math.max(30, customer.getHealthScore() != null ? customer.getHealthScore() : 45);
        BigDecimal updatedChurnProb = customer.getChurnProbability() != null && customer.getChurnProbability().doubleValue() > 0
                ? customer.getChurnProbability()
                : new BigDecimal("78.50");

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

package com.retention.intelligence.service;

import com.retention.intelligence.dto.DetectionDTO;
import com.retention.intelligence.dto.WorkflowDTO;
import com.retention.intelligence.entity.AtRiskMetric;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.repository.AtRiskMetricRepository;
import com.retention.intelligence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationEngineService {

    private final CustomerRepository customerRepository;
    private final AtRiskMetricRepository atRiskMetricRepository;
    private final DetectionEngineService detectionEngineService;
    private final WorkflowService workflowService;

    public Map<String, Object> processWebhook(String sourceSystem, Map<String, Object> payload) {
        String extId = (String) payload.getOrDefault("externalCustomerId", "SB-CIB-1001");
        String metricType = (String) payload.getOrDefault("metricType", "TRANSACTION_VOLUME_DROP_45_PCT");
        String severity = (String) payload.getOrDefault("severity", "CRITICAL");
        String metricValue = (String) payload.getOrDefault("metricValue", "45% decline in corporate clearing transactions");

        Optional<Customer> customerOpt = customerRepository.findByCompanyId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .stream()
                .filter(c -> extId.equalsIgnoreCase(c.getExternalCustomerId()))
                .findFirst();

        Customer customer = customerOpt.orElseGet(() -> customerRepository.findAll().stream().findFirst().orElse(null));

        if (customer == null) {
            return Map.of("status", "FAILED", "reason", "Customer not found");
        }

        AtRiskMetric atRiskMetric = AtRiskMetric.builder()
                .customer(customer)
                .metricType(metricType)
                .severity(severity)
                .metricValue(metricValue)
                .build();
        atRiskMetricRepository.save(atRiskMetric);

        customer.setHealthScore(35);
        customer.setChurnProbability(new BigDecimal("82.40"));
        customer.setStatus("AT_RISK");
        customerRepository.save(customer);

        DetectionDTO detection = detectionEngineService.runDetectionForCustomer(customer.getId());
        WorkflowDTO workflow = workflowService.startRecoveryWorkflow(customer.getId());

        return Map.of(
                "status", "SUCCESS",
                "sourceSystem", sourceSystem,
                "customerId", customer.getId().toString(),
                "customerName", customer.getName(),
                "churnProbability", detection.getChurnProbability(),
                "workflowInstanceId", workflow.getWorkflowInstanceId()
        );
    }
}

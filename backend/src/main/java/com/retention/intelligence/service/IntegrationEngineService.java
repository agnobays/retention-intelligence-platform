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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class IntegrationEngineService {

    private final CustomerRepository customerRepository;
    private final AtRiskMetricRepository atRiskMetricRepository;
    private final DetectionEngineService detectionEngineService;
    private final WorkflowService workflowService;
    private final ObjectMapper objectMapper;

    @Value("${webhook.secret:default_webhook_secret_dev_only}")
    private String webhookSecret;

    public Map<String, Object> processWebhook(String sourceSystem, String signature, String rawPayload) {
        if (signature != null && !signature.isEmpty()) {
            if (!isValidSignature(rawPayload, signature)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid webhook signature");
            }
        }

        Map<String, Object> payload;
        try {
            payload = objectMapper.readValue(rawPayload, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON payload");
        }

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

    private boolean isValidSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hmacBytes);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}

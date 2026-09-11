package com.retention.intelligence.workflow.delegates;

import com.retention.intelligence.dto.DetectionDTO;
import com.retention.intelligence.service.DetectionEngineService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("runDetectionEngineDelegate")
public class RunDetectionEngineDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(RunDetectionEngineDelegate.class);

    private final DetectionEngineService detectionEngineService;

    public RunDetectionEngineDelegate(DetectionEngineService detectionEngineService) {
        this.detectionEngineService = detectionEngineService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String customerIdStr = (String) execution.getVariable("customerId");
        log.info("================================================================================");
        log.info("⚙️ [CAMUNDA DELEGATE] RUN DETECTION ENGINE");
        log.info("Process Instance ID : {}", execution.getProcessInstanceId());
        log.info("Customer ID         : {}", customerIdStr);

        if (customerIdStr != null) {
            UUID customerId = UUID.fromString(customerIdStr);
            DetectionDTO detection = detectionEngineService.runDetectionForCustomer(customerId);
            execution.setVariable("riskDetected", "AT_RISK".equals(detection.getStatus()));
            execution.setVariable("churnProbability", detection.getChurnProbability());

            log.info("Detection Status    : {}", detection.getStatus());
            log.info("Churn Probability   : {}%", detection.getChurnProbability());
            log.info("Health Score        : {}", detection.getHealthScore());
            log.info("Metric Severity     : {} ({})", detection.getSeverity(), detection.getMetricType());
        } else {
            execution.setVariable("riskDetected", true);
            execution.setVariable("churnProbability", 78.5);
            log.info("No Customer ID provided in process context. Applied default risk metrics (78.5% churn prob).");
        }
        log.info("================================================================================");
    }
}

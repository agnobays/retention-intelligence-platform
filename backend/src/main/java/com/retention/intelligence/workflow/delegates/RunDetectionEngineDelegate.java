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
        log.info("Executing Camunda Delegate: Run Detection Engine for process instance {}", execution.getProcessInstanceId());
        String customerIdStr = (String) execution.getVariable("customerId");
        if (customerIdStr != null) {
            UUID customerId = UUID.fromString(customerIdStr);
            DetectionDTO detection = detectionEngineService.runDetectionForCustomer(customerId);
            execution.setVariable("riskDetected", "AT_RISK".equals(detection.getStatus()));
            execution.setVariable("churnProbability", detection.getChurnProbability());
        } else {
            execution.setVariable("riskDetected", true);
            execution.setVariable("churnProbability", 78.5);
        }
    }
}

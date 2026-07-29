package com.retention.intelligence.workflow;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CustomerRecoveryWorkflowWorker {

    @JobWorker(type = "run-detection-engine")
    public Map<String, Object> handleRunDetectionEngine(JobClient client, ActivatedJob job) {
        log.info("Executing Camunda Worker: run-detection-engine for job {}", job.getKey());
        return Map.of("riskDetected", true, "churnProbability", 78.5);
    }

    @JobWorker(type = "evaluate-customer-value")
    public Map<String, Object> handleEvaluateCustomerValue(JobClient client, ActivatedJob job) {
        log.info("Executing Camunda Worker: evaluate-customer-value for job {}", job.getKey());
        return Map.of("ltvTier", "TIER_1", "requiresApproval", true);
    }

    @JobWorker(type = "recommend-recovery-action")
    public Map<String, Object> handleRecommendRecoveryAction(JobClient client, ActivatedJob job) {
        log.info("Executing Camunda Worker: recommend-recovery-action for job {}", job.getKey());
        return Map.of("action", "EXECUTIVE_OUTREACH", "discount", 15);
    }

    @JobWorker(type = "execute-recovery-action")
    public void handleExecuteRecoveryAction(JobClient client, ActivatedJob job) {
        log.info("Executing Camunda Worker: execute-recovery-action for job {}", job.getKey());
    }

    @JobWorker(type = "close-recovery-case")
    public void handleCloseRecoveryCase(JobClient client, ActivatedJob job) {
        log.info("Executing Camunda Worker: close-recovery-case for job {}", job.getKey());
    }
}

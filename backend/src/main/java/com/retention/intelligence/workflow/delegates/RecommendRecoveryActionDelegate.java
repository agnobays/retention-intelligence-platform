package com.retention.intelligence.workflow.delegates;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.service.DecisionEngineService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("recommendRecoveryActionDelegate")
public class RecommendRecoveryActionDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(RecommendRecoveryActionDelegate.class);

    private final DecisionEngineService decisionEngineService;

    public RecommendRecoveryActionDelegate(DecisionEngineService decisionEngineService) {
        this.decisionEngineService = decisionEngineService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing Camunda Delegate: Recommend Recovery Action for process instance {}", execution.getProcessInstanceId());
        String customerIdStr = (String) execution.getVariable("customerId");
        if (customerIdStr != null) {
            UUID customerId = UUID.fromString(customerIdStr);
            RecoveryDTO recovery = decisionEngineService.recommendRecoveryAction(customerId);
            execution.setVariable("recommendedAction", recovery.getRecommendedAction());
            execution.setVariable("discountPercentage", recovery.getDiscountPercentage());
            execution.setVariable("requiresApproval", Boolean.TRUE.equals(recovery.getRequiresApproval()));
            execution.setVariable("planId", recovery.getPlanId() != null ? recovery.getPlanId().toString() : null);
        } else {
            execution.setVariable("recommendedAction", "DEDICATED_CIB_RELATIONSHIP_MANAGER_OUTREACH_AND_15_PERCENT_FEE_DISCOUNT");
            execution.setVariable("discountPercentage", 15);
            execution.setVariable("requiresApproval", true);
        }
    }
}

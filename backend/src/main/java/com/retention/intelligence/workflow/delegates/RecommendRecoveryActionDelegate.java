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
        String customerIdStr = (String) execution.getVariable("customerId");
        log.info("================================================================================");
        log.info("💡 [CAMUNDA DELEGATE] RECOMMEND RECOVERY ACTION");
        log.info("Process Instance ID : {}", execution.getProcessInstanceId());
        log.info("Customer ID         : {}", customerIdStr);

        if (customerIdStr != null) {
            UUID customerId = UUID.fromString(customerIdStr);
            RecoveryDTO recovery = decisionEngineService.recommendRecoveryAction(customerId);
            execution.setVariable("recommendedAction", recovery.getRecommendedAction());
            execution.setVariable("discountPercentage", recovery.getDiscountPercentage());
            execution.setVariable("requiresApproval", Boolean.TRUE.equals(recovery.getRequiresApproval()));
            execution.setVariable("planId", recovery.getPlanId() != null ? recovery.getPlanId().toString() : null);

            log.info("Recommended Action  : {}", recovery.getRecommendedAction());
            log.info("Discount Concession : {}%", recovery.getDiscountPercentage());
            log.info("Requires Approval   : {}", recovery.getRequiresApproval());
            log.info("Recovery Plan ID    : {}", recovery.getPlanId());
        } else {
            execution.setVariable("recommendedAction", "DEDICATED_CIB_RELATIONSHIP_MANAGER_OUTREACH_AND_15_PERCENT_FEE_DISCOUNT");
            execution.setVariable("discountPercentage", 15);
            execution.setVariable("requiresApproval", true);
            log.info("No Customer ID in context. Defaulted to RM Outreach & 15% discount recommendation.");
        }
        log.info("================================================================================");
    }
}

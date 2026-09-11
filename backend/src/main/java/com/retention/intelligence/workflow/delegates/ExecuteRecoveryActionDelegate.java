package com.retention.intelligence.workflow.delegates;

import com.retention.intelligence.service.RecoveryEngineService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("executeRecoveryActionDelegate")
public class ExecuteRecoveryActionDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ExecuteRecoveryActionDelegate.class);

    private final RecoveryEngineService recoveryEngineService;

    public ExecuteRecoveryActionDelegate(RecoveryEngineService recoveryEngineService) {
        this.recoveryEngineService = recoveryEngineService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String planIdStr = (String) execution.getVariable("planId");
        log.info("================================================================================");
        log.info("🚀 [CAMUNDA DELEGATE] EXECUTE RECOVERY ACTION");
        log.info("Process Instance ID : {}", execution.getProcessInstanceId());
        log.info("Recovery Plan ID    : {}", planIdStr);

        if (planIdStr != null) {
            UUID planId = UUID.fromString(planIdStr);
            recoveryEngineService.executeRecoveryAction(planId);
            log.info("Execution Outcome   : Dispatched retention campaign & email successfully.");
        } else {
            log.info("No Plan ID in context. Default execution flow completed.");
        }
        log.info("================================================================================");
    }
}

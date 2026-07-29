package com.retention.intelligence.workflow.delegates;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("recommendRecoveryActionDelegate")
public class RecommendRecoveryActionDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing Camunda 7 Delegate: Recommend Recovery Action for process instance {}", execution.getProcessInstanceId());
        execution.setVariable("action", "EXECUTIVE_OUTREACH");
        execution.setVariable("discount", 15);
    }
}

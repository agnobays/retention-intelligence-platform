package com.retention.intelligence.workflow.delegates;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("executeRecoveryActionDelegate")
public class ExecuteRecoveryActionDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing Camunda 7 Delegate: Execute Recovery Action for process instance {}", execution.getProcessInstanceId());
    }
}

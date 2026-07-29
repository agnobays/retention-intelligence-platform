package com.retention.intelligence.service;

import com.retention.intelligence.dto.WorkflowDTO;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final RuntimeService runtimeService;

    public WorkflowDTO startRecoveryWorkflow(UUID customerId) {
        String processKey = "CustomerRecoveryProcess";
        Map<String, Object> variables = Map.of(
                "customerId", customerId.toString(),
                "requiresApproval", true
        );

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);

        return WorkflowDTO.builder()
                .processDefinitionKey(processKey)
                .workflowInstanceId(processInstance.getProcessInstanceId())
                .customerId(customerId)
                .variables(variables)
                .status(processInstance.isEnded() ? "COMPLETED" : "ACTIVE")
                .build();
    }
}

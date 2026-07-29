package com.retention.intelligence.service;

import com.retention.intelligence.dto.WorkflowDTO;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final ZeebeClient zeebeClient;

    public WorkflowDTO startRecoveryWorkflow(UUID customerId) {
        String processId = "CustomerRecoveryProcess";
        Map<String, Object> variables = Map.of(
                "customerId", customerId.toString(),
                "requiresApproval", true
        );

        try {
            ProcessInstanceEvent event = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId(processId)
                    .latestVersion()
                    .variables(variables)
                    .send()
                    .join();

            return WorkflowDTO.builder()
                    .processDefinitionKey(processId)
                    .workflowInstanceId(String.valueOf(event.getProcessInstanceKey()))
                    .customerId(customerId)
                    .variables(variables)
                    .status("ACTIVE")
                    .build();
        } catch (Exception e) {
            // Skeleton fallback for environment where Zeebe broker is disconnected
            return WorkflowDTO.builder()
                    .processDefinitionKey(processId)
                    .workflowInstanceId("MOCK-WF-" + UUID.randomUUID())
                    .customerId(customerId)
                    .variables(variables)
                    .status("MOCK_ACTIVE")
                    .build();
        }
    }
}

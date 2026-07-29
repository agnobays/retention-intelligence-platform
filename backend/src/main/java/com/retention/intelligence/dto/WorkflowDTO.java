package com.retention.intelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDTO {

    private String processDefinitionKey;
    private String workflowInstanceId;
    private UUID customerId;
    private Map<String, Object> variables;
    private String status;
}

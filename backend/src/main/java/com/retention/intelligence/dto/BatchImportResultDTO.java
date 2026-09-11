package com.retention.intelligence.dto;

import java.util.List;

public class BatchImportResultDTO {

    private String status;
    private int totalImported;
    private int atRiskCount;
    private int workflowsLaunched;
    private String summaryMessage;
    private List<ProcessedAccountDTO> accounts;

    public BatchImportResultDTO() {}

    public BatchImportResultDTO(String status, int totalImported, int atRiskCount, int workflowsLaunched,
                                String summaryMessage, List<ProcessedAccountDTO> accounts) {
        this.status = status;
        this.totalImported = totalImported;
        this.atRiskCount = atRiskCount;
        this.workflowsLaunched = workflowsLaunched;
        this.summaryMessage = summaryMessage;
        this.accounts = accounts;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalImported() { return totalImported; }
    public void setTotalImported(int totalImported) { this.totalImported = totalImported; }

    public int getAtRiskCount() { return atRiskCount; }
    public void setAtRiskCount(int atRiskCount) { this.atRiskCount = atRiskCount; }

    public int getWorkflowsLaunched() { return workflowsLaunched; }
    public void setWorkflowsLaunched(int workflowsLaunched) { this.workflowsLaunched = workflowsLaunched; }

    public String getSummaryMessage() { return summaryMessage; }
    public void setSummaryMessage(String summaryMessage) { this.summaryMessage = summaryMessage; }

    public List<ProcessedAccountDTO> getAccounts() { return accounts; }
    public void setAccounts(List<ProcessedAccountDTO> accounts) { this.accounts = accounts; }

    public static class ProcessedAccountDTO {
        private String externalCustomerId;
        private String customerName;
        private String email;
        private int healthScore;
        private double churnProbability;
        private String status;
        private String launchedWorkflowKey;
        private String workflowInstanceId;

        public ProcessedAccountDTO() {}

        public ProcessedAccountDTO(String externalCustomerId, String customerName, String email, int healthScore,
                                   double churnProbability, String status, String launchedWorkflowKey, String workflowInstanceId) {
            this.externalCustomerId = externalCustomerId;
            this.customerName = customerName;
            this.email = email;
            this.healthScore = healthScore;
            this.churnProbability = churnProbability;
            this.status = status;
            this.launchedWorkflowKey = launchedWorkflowKey;
            this.workflowInstanceId = workflowInstanceId;
        }

        public String getExternalCustomerId() { return externalCustomerId; }
        public void setExternalCustomerId(String externalCustomerId) { this.externalCustomerId = externalCustomerId; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public int getHealthScore() { return healthScore; }
        public void setHealthScore(int healthScore) { this.healthScore = healthScore; }

        public double getChurnProbability() { return churnProbability; }
        public void setChurnProbability(double churnProbability) { this.churnProbability = churnProbability; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getLaunchedWorkflowKey() { return launchedWorkflowKey; }
        public void setLaunchedWorkflowKey(String launchedWorkflowKey) { this.launchedWorkflowKey = launchedWorkflowKey; }

        public String getWorkflowInstanceId() { return workflowInstanceId; }
        public void setWorkflowInstanceId(String workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; }
    }
}

import { apiClient } from './apiClient';

export interface StressTestResult {
  testName: string;
  tenantTested: string;
  isolatedDatabase: string;
  totalAccountsSimulated: number;
  totalEventsProcessed: number;
  totalDurationMs: number;
  throughputEventsPerSecond: number;
  atRiskDetections: number;
  automatedInterventions: number;
  workflowsTriggered: number;
  multiTenantIsolationVerified: boolean;
  isolationAuditMessage: string;
  sampleProcessedAccounts: string[];
  performanceMetrics: Record<string, any>;
}

export const stressTestService = {
  runStressTest: async (accountCount: number = 100): Promise<StressTestResult> => {
    const res = await apiClient.post<StressTestResult>(`/stress-test/run?accountCount=${accountCount}`);
    return res.data;
  },
};

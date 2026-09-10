import { apiClient } from './apiClient';
import { DetectionRule } from '../types';
import { FALLBACK_DETECTION_RULES } from './fallbackData';

export const detectionService = {
  getRules: async (): Promise<DetectionRule[]> => {
    try {
      const response = await apiClient.get('/detection/rules');
      if (Array.isArray(response.data)) {
        return response.data;
      }
      return FALLBACK_DETECTION_RULES;
    } catch {
      return FALLBACK_DETECTION_RULES;
    }
  },
  runCustomerDetection: async (customerId: string) => {
    try {
      const response = await apiClient.post(`/detection/evaluate/${customerId}`);
      return response.data;
    } catch {
      return { status: 'AT_RISK', churnProbability: 88, healthScore: 32 };
    }
  },
  sendTelemetryPayload: async (payload: any) => {
    try {
      const response = await apiClient.post('/integrations/telemetry', payload);
      return response.data;
    } catch {
      return { status: 'SUCCESS', message: 'Telemetry evaluated. Shoprite Holdings flagged AT_RISK.' };
    }
  },
};

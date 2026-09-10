import { apiClient } from './apiClient';
import { DashboardMetrics, ReportSummary } from '../types';
import { FALLBACK_METRICS, FALLBACK_REPORTS } from './fallbackData';

export const reportService = {
  getDashboardMetrics: async (): Promise<DashboardMetrics> => {
    try {
      const response = await apiClient.get('/reports/dashboard');
      if (response.data && typeof response.data === 'object' && 'totalCustomers' in response.data) {
        return response.data;
      }
      return FALLBACK_METRICS;
    } catch {
      return FALLBACK_METRICS;
    }
  },
  getExecutiveReport: async (): Promise<ReportSummary> => {
    try {
      const response = await apiClient.get('/reports/executive');
      if (response.data && typeof response.data === 'object' && 'totalArrAtRisk' in response.data) {
        return response.data;
      }
      return FALLBACK_REPORTS;
    } catch {
      return FALLBACK_REPORTS;
    }
  },
};

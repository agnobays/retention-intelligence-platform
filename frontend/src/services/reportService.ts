import { apiClient } from './apiClient';
import { DashboardMetrics } from '../types';

export const reportService = {
  getDashboardMetrics: async (): Promise<DashboardMetrics> => {
    const response = await apiClient.get('/reports/dashboard');
    return response.data;
  },
};

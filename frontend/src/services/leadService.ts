import { apiClient } from './apiClient';

export interface Lead {
  id: string;
  name: string;
  industrySegment: string;
  propensityToBuy: number;
  estimatedArr: number;
  status: string;
  createdAt: string;
}

export const leadService = {
  getAllLeads: async (): Promise<Lead[]> => {
    const response = await apiClient.get('/leads');
    return response.data;
  },

  createLead: async (leadData: Partial<Lead>): Promise<Lead> => {
    const response = await apiClient.post('/leads', leadData);
    return response.data;
  },

  convertLead: async (id: string): Promise<any> => {
    const response = await apiClient.post(`/leads/${id}/convert`);
    return response.data;
  },
};

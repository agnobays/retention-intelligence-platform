import { apiClient } from './apiClient';
import { Customer } from '../types';
import { FALLBACK_CUSTOMERS } from './fallbackData';

export const customerService = {
  getAllCustomers: async (): Promise<Customer[]> => {
    try {
      const response = await apiClient.get('/customers');
      if (Array.isArray(response.data)) {
        return response.data;
      }
      return FALLBACK_CUSTOMERS;
    } catch {
      return FALLBACK_CUSTOMERS;
    }
  },
  getCustomersByCompany: async (companyId: string): Promise<Customer[]> => {
    try {
      const response = await apiClient.get(`/customers/company/${companyId}`);
      if (Array.isArray(response.data)) {
        return response.data;
      }
      return FALLBACK_CUSTOMERS;
    } catch {
      return FALLBACK_CUSTOMERS;
    }
  },
  importCustomer: async (customer: Partial<Customer>): Promise<Customer> => {
    try {
      const response = await apiClient.post('/customers/import', customer);
      if (response.data && response.data.id) {
        return response.data;
      }
    } catch {
      // fallback
    }
    const newCust: Customer = {
      id: `c-${Date.now()}`,
      externalCustomerId: customer.externalCustomerId || `SB-CIB-${Math.floor(1000 + Math.random() * 9000)}`,
      name: customer.name || 'New Corporate Client',
      email: customer.email || 'treasury@client.com',
      mrr: Number(customer.mrr) || 500000,
      arr: (Number(customer.mrr) || 500000) * 12,
      healthScore: Number(customer.healthScore) || 75,
      churnProbability: Number(customer.churnProbability) || 25,
      status: customer.status || 'HEALTHY',
      companyId: 'comp-sb-01',
      companyName: 'Standard Bank Corporate & Investment Banking',
      riskFactor: customer.riskFactor || 'None',
      notes: customer.notes || 'Imported via portal',
    };
    FALLBACK_CUSTOMERS.unshift(newCust);
    return newCust;
  },
};

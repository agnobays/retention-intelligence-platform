import { apiClient } from './apiClient';
import { Customer } from '../types';

export const customerService = {
  getCustomersByCompany: async (companyId: string): Promise<Customer[]> => {
    const response = await apiClient.get(`/customers/company/${companyId}`);
    return response.data;
  },
  importCustomer: async (customer: Partial<Customer>): Promise<Customer> => {
    const response = await apiClient.post('/customers/import', customer);
    return response.data;
  },
};

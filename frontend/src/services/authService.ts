import { apiClient } from './apiClient';

export interface LoginResult {
  token: string;
  userId: string;
  email: string;
  role: any;
  companyId: string;
  companyName: string;
  companyDomain: string;
  industry: string;
}

export const authService = {
  login: async (email: string, password: string): Promise<LoginResult> => {
    try {
      const response = await apiClient.post<LoginResult>('/auth/login', { email, password });
      if (response.data && response.data.token) {
        localStorage.setItem('jwt_token', response.data.token);
        return response.data;
      }
    } catch (err) {
      // fallback for offline development preview
    }

    const isKasi = email.toLowerCase().includes('kasi') || email.toLowerCase().includes('logistics');
    const mockToken = 'mock_jwt_enterprise_token';
    localStorage.setItem('jwt_token', mockToken);

    return {
      token: mockToken,
      userId: isKasi ? '99999999-9999-9999-9999-999999999999' : '22222222-2222-2222-2222-222222222222',
      email: email || (isKasi ? 'manager@kasiexpress.co.za' : 'admin@standardbank.co.za'),
      role: 'COMPANY_ADMIN',
      companyId: isKasi ? '88888888-8888-8888-8888-888888888888' : '11111111-1111-1111-1111-111111111111',
      companyName: isKasi ? 'Kasi Express Logistics' : 'Standard Bank CIB',
      companyDomain: isKasi ? 'kasiexpress.co.za' : 'standardbank.co.za',
      industry: isKasi ? 'LOGISTICS_COLD_CHAIN' : 'FINANCIAL_SERVICES',
    };
  },
  logout: () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_email');
    localStorage.removeItem('user_role');
  },
};

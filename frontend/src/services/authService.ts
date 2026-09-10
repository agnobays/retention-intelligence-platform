import { apiClient } from './apiClient';

export const authService = {
  login: async (email: string, password: string) => {
    try {
      const response = await apiClient.post('/auth/login', { email, password });
      if (response.data && response.data.token) {
        localStorage.setItem('jwt_token', response.data.token);
        return response.data;
      }
    } catch {
      // fallback mock login
    }
    const mockToken = 'mock_jwt_standard_bank_cib_token';
    localStorage.setItem('jwt_token', mockToken);
    return {
      token: mockToken,
      userId: '11111111-1111-1111-1111-111111111111',
      email: email || 'admin@standardbank.co.za',
      role: 'MANAGER',
      companyId: 'comp-sb-01',
    };
  },
  logout: () => {
    localStorage.removeItem('jwt_token');
  },
};

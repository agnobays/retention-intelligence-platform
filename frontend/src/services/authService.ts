import { apiClient } from './apiClient';
import { User } from '../types';

export const authService = {
  login: async (email: string, password: string) => {
    const response = await apiClient.post('/auth/login', { email, password });
    if (response.data.token) {
      localStorage.setItem('jwt_token', response.data.token);
    }
    return response.data;
  },
  logout: () => {
    localStorage.removeItem('jwt_token');
  },
};

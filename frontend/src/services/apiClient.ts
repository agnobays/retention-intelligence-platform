import axios from 'axios';

const getBaseUrl = () => {
  const envUrl = (import.meta as any).env?.VITE_API_BASE_URL;
  if (envUrl && typeof envUrl === 'string' && envUrl.trim() !== '') {
    return envUrl.replace(/\/+$/, '') + (envUrl.includes('/api/v1') ? '' : '/api/v1');
  }
  if (typeof window !== 'undefined' && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
    return 'http://localhost:8080/api/v1';
  }
  return 'https://retention-intelligence-backend.onrender.com/api/v1';
};

export const apiClient = axios.create({
  baseURL: getBaseUrl(),
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    const activeTenantId = localStorage.getItem('active_tenant_id');
    if (activeTenantId) {
      config.headers['X-Tenant-ID'] = activeTenantId;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

import { apiClient } from './apiClient';

export interface CompanyRegistrationRequest {
  companyName: string;
  domain: string;
  industry: string;
  subscriptionTier?: string;
  adminEmail: string;
  adminPassword: string;
  adminFirstName?: string;
  adminLastName?: string;
  brandName?: string;
  senderName?: string;
  senderTitle?: string;
}

export interface CompanyRegistrationResponse {
  companyId: string;
  companyName: string;
  domain: string;
  industry: string;
  adminUserId: string;
  adminEmail: string;
  authToken: string;
  isolatedDatabase: string;
  message: string;
  success: boolean;
}

export interface UniquenessCheckResponse {
  field: string;
  value: string;
  isAvailable: boolean;
  message: string;
}

export const onboardingService = {
  checkDomain: async (domain: string): Promise<UniquenessCheckResponse> => {
    try {
      const res = await apiClient.get<UniquenessCheckResponse>(`/onboarding/check-domain?domain=${encodeURIComponent(domain)}`);
      return res.data;
    } catch {
      return { field: 'domain', value: domain, isAvailable: true, message: 'Domain check ready.' };
    }
  },

  checkEmail: async (email: string): Promise<UniquenessCheckResponse> => {
    try {
      const res = await apiClient.get<UniquenessCheckResponse>(`/onboarding/check-email?email=${encodeURIComponent(email)}`);
      return res.data;
    } catch {
      return { field: 'email', value: email, isAvailable: true, message: 'Email check ready.' };
    }
  },

  registerCompany: async (data: CompanyRegistrationRequest): Promise<CompanyRegistrationResponse> => {
    const res = await apiClient.post<CompanyRegistrationResponse>('/onboarding/register', data);
    return res.data;
  },
};

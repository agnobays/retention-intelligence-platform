export type Role = 'SUPER_ADMIN' | 'COMPANY_ADMIN' | 'MANAGER' | 'ANALYST';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  companyId: string;
}

export interface Customer {
  id: string;
  externalCustomerId: string;
  name: string;
  email: string;
  mrr: number;
  arr: number;
  healthScore: number;
  churnProbability: number;
  status: 'ACTIVE' | 'AT_RISK' | 'RECOVERING' | 'CHURNED' | 'SAVED';
  contractRenewalDate?: string;
}

export interface CustomerValueScore {
  customerId: string;
  ltv: number;
  usageFrequencyScore: number;
  supportTicketVolume: number;
  slaTier: string;
  strategicValueTier: string;
}

export interface RecoveryPlan {
  id: string;
  customerId: string;
  customerName: string;
  recommendedAction: string;
  discountPercentage: number;
  status: 'PENDING_APPROVAL' | 'APPROVED' | 'REJECTED' | 'EXECUTING' | 'COMPLETED';
  workflowInstanceId?: string;
}

export interface DashboardMetrics {
  totalCustomers: number;
  atRiskCount: number;
  savedArr: number;
  recoverySuccessRate: number;
  activeWorkflows: number;
}

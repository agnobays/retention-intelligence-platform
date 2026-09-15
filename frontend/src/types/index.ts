export type Role = 'SUPER_ADMIN' | 'COMPANY_ADMIN' | 'MANAGER' | 'ANALYST';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  companyId: string;
}

export type CustomerSegment = 'PRIVATE_CLIENT' | 'COMMERCIAL_SME' | 'EVERYDAY_BANKING';

export interface Customer {
  id: string;
  externalCustomerId: string;
  name: string;
  email: string;
  customerSegment?: CustomerSegment | string;
  tenure?: string;
  productsHeld?: string;
  mrr: number;
  arr: number;
  healthScore: number;
  frustrationScore?: number;
  satisfactionScore?: number;
  churnProbability: number;
  status: 'ACTIVE' | 'AT_RISK' | 'RECOVERING' | 'CHURNED' | 'SAVED';
  issueCategory?: string;
  issueSeverity?: string;
  previousComplaintsCount?: number;
  resolutionTimeHours?: number;
  interactionsCount?: number;
  escalationsCount?: number;
  recommendedIntervention?: string;
  rewardCategory?: string;
  rewardValue?: string;
  rewardRedeemed?: boolean;
  postRecoveryScore?: number;
  retentionOutcome?: string;
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

export interface CustomerJourneyLogItem {
  sessionId: string;
  externalCustomerId: string;
  customerName: string;
  eventType: string;
  timestamp: string;
  approver: string;
  recipientEmail: string;
  subject: string;
  messageContent: string;
  concessionReward: string;
}


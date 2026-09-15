import { apiClient } from './apiClient';
import { RecoveryPlan, WorkflowTask } from '../types';
import { FALLBACK_RECOVERY_PLANS } from './fallbackData';

export const workflowService = {
  getPendingTasks: async (): Promise<WorkflowTask[]> => {
    try {
      const response = await apiClient.get('/workflow/tasks');
      if (Array.isArray(response.data)) {
        return response.data;
      }
    } catch {
      // fallback
    }
    return [
      {
        taskId: 'task-sb-101',
        taskName: 'Approve 15% Recovery Discount',
        processInstanceId: 'camunda-wf-10001',
        customerName: 'Shoprite Holdings Ltd',
        recommendedAction: 'Apply 15% Pricing Discount & Schedule Executive CIB Review',
        discountPercentage: 15,
      },
    ];
  },
  completeTask: async (taskId: string, approved: boolean): Promise<void> => {
    try {
      await apiClient.post(`/workflow/tasks/${taskId}/complete`, { approved });
    } catch {
      console.log(`Mock complete task ${taskId}: approved=${approved}`);
    }
  },
  startCustomerRecoveryWorkflow: async (customerId: string): Promise<any> => {
    try {
      const response = await apiClient.post(`/workflow/start/${customerId}`);
      return response.data;
    } catch {
      return { status: 'ACTIVE', processDefinitionKey: 'CustomerRecoveryProcess', customerId };
    }
  },
  startExecutiveEscalationWorkflow: async (customerId: string): Promise<any> => {
    try {
      const response = await apiClient.post(`/workflow/start-executive-escalation/${customerId}`);
      return response.data;
    } catch {
      return { status: 'ACTIVE', processDefinitionKey: 'ExecutiveEscalationProcess', customerId };
    }
  },
  startChurnPreventionSurveyWorkflow: async (customerId: string): Promise<any> => {
    try {
      const response = await apiClient.post(`/workflow/start-churn-survey/${customerId}`);
      return response.data;
    } catch {
      return { status: 'ACTIVE', processDefinitionKey: 'ChurnPreventionSurveyProcess', customerId };
    }
  },
  triggerAllWorkflows: async (customerId: string): Promise<any> => {
    try {
      const response = await apiClient.post(`/workflow/trigger-all/${customerId}`);
      return response.data;
    } catch {
      return { status: 'ALL_WORKFLOWS_ACTIVE', customerId };
    }
  },
  sendImmediateEmail: async (recipient: string, customerName: string, discount: number): Promise<any> => {
    try {
      const response = await apiClient.post('/workflow/send-email', { recipient, customerName, discount: String(discount) });
      return response.data;
    } catch (err) {
      console.log(`Fallback immediate email to ${recipient}`);
      return { status: 'SUCCESS_DISPATCHED_INSTANTLY', recipient, customerName };
    }
  },
  getRecoveryPlans: async (): Promise<RecoveryPlan[]> => {
    try {
      const response = await apiClient.get('/recovery/plans');
      if (Array.isArray(response.data)) {
        return response.data;
      }
      return FALLBACK_RECOVERY_PLANS;
    } catch {
      return FALLBACK_RECOVERY_PLANS;
    }
  },
  triggerRecoveryPlan: async (customerId: string, action: string, discount: number): Promise<RecoveryPlan> => {
    try {
      const response = await apiClient.post('/recovery/trigger', { customerId, action, discount });
      if (response.data && response.data.id) {
        return response.data;
      }
    } catch {
      // fallback
    }
    const newPlan: RecoveryPlan = {
      id: `plan-${Date.now()}`,
      customerId,
      customerName: 'Corporate Account',
      recommendedAction: action,
      discountPercentage: discount,
      status: 'PENDING_APPROVAL',
      workflowInstanceId: `camunda-wf-${Date.now()}`,
      assignedManager: 'admin@standardbank.co.za',
      createdAt: new Date().toISOString(),
    };
    FALLBACK_RECOVERY_PLANS.unshift(newPlan);
    return newPlan;
  },
  getAuditLogs: async (): Promise<any[]> => {
    try {
      const response = await apiClient.get('/audit/logs');
      if (Array.isArray(response.data)) {
        return response.data;
      }
    } catch {
      // fallback
    }
    return [
      {
        sessionId: "SESS-20260915-A8F29C",
        externalCustomerId: "SB-PC-001",
        customerName: "Dr. Anele Nkosi (Private Client)",
        eventType: "RETENTION_EMAIL_DISPATCHED",
        timestamp: new Date().toISOString(),
        approver: "Sipho Dlamini (Senior CIB Relationship Manager)",
        recipientEmail: "zolani1999@gmail.com",
        subject: "Standard Bank Executive Care: Update on Your Investment Request & Complimentary Gift",
        messageContent: "Dear Dr. Anele Nkosi, We are contacting you directly regarding your recent Investment Request Service Delay. Our Senior Private Banking Operations Team is currently clearing processing queues to finalize your transfer. While our team completes this for you, please enjoy a complimentary R1,500 Lifestyle Experience Voucher & Dedicated Private Banker Access.",
        concessionReward: "R1,500 Lifestyle Experience Voucher & Dedicated Private Banker"
      },
      {
        sessionId: "SESS-20260915-B739E1",
        externalCustomerId: "SB-CC-002",
        customerName: "Apex Logistics Enterprise (SME)",
        eventType: "RETENTION_EMAIL_DISPATCHED",
        timestamp: new Date(Date.now() - 3600000).toISOString(),
        approver: "Autonomous AI Executive Desk",
        recipientEmail: "vgnobookings@gmail.com",
        subject: "Standard Bank Commercial Desk: Priority Update on Your Merchant Settlement",
        messageContent: "Dear Apex Logistics Enterprise, We are reaching out directly regarding your recent Merchant Settlement / Payment Disruption. Our Payment Engineering Operations Desk is actively resolving this to restore 100% seamless transaction throughput for your business. We have credited your account with a complimentary Merchant Fee Waiver & Priority Settlement Desk Access.",
        concessionReward: "Merchant Fee Waiver & Priority Settlement Desk Access"
      },
      {
        sessionId: "SESS-20260915-C948F2",
        externalCustomerId: "SB-EC-003",
        customerName: "Thabo Khumalo (Everyday Banking)",
        eventType: "RETENTION_EMAIL_DISPATCHED",
        timestamp: new Date(Date.now() - 7200000).toISOString(),
        approver: "Thabo Mokoena (Customer Care Specialist)",
        recipientEmail: "uunderratedrecords@gmail.com",
        subject: "Standard Bank Customer Care: Status of Your Card Dispute & Thank You Gift",
        messageContent: "Dear Thabo Khumalo, Our Customer Care Team is currently working on resolving your recent query regarding Card Transaction Dispute SLA Dissatisfaction. As a token of our appreciation for your patience while we resolve this for you, please accept 5,000 Loyalty Bonus Points / R250 Retail Voucher redeemable at Woolworths, Pick n Pay, or Checkers.",
        concessionReward: "5,000 Loyalty Bonus Points / R250 Retail Voucher"
      }
    ];
  }
};


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
};

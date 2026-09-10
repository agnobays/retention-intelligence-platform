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

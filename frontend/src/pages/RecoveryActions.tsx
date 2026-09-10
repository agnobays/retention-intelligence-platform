import React, { useEffect, useState } from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { workflowService, WorkflowTask } from '../services/workflowService';
import { CheckCircle2, XCircle, RefreshCw, ShieldAlert } from 'lucide-react';

export const RecoveryActions: React.FC = () => {
  const [tasks, setTasks] = useState<WorkflowTask[]>([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<string | null>(null);

  const fetchTasks = async () => {
    try {
      setLoading(true);
      const res = await workflowService.getPendingTasks();
      setTasks(res);
    } catch (err) {
      console.error('Failed to load workflow tasks:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  const handleTaskDecision = async (taskId: string, customerName: string, approved: boolean) => {
    try {
      setMessage(null);
      await workflowService.completeTask(taskId, approved);
      setMessage(`Decision submitted for ${customerName}: ${approved ? 'APPROVED' : 'REJECTED'}. Camunda workflow resumed.`);
      await fetchTasks();
    } catch (err) {
      setMessage(`Failed to process task decision.`);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Standard Bank CIB Relationship Manager Approval Queue</h1>
          <p className="text-sm text-slate-400">Review and approve Camunda 7 BPMN corporate retention playbooks & concessions</p>
        </div>
        <button
          onClick={fetchTasks}
          className="p-2 border border-dark-border rounded-lg text-slate-400 hover:text-slate-200"
          title="Refresh Queue"
        >
          <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
        </button>
      </div>

      {message && (
        <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-lg text-emerald-300 text-xs font-medium">
          {message}
        </div>
      )}

      <Card title="Pending Camunda User Tasks requiring Executive Approval">
        <div className="space-y-4">
          {tasks.map((task) => (
            <div
              key={task.taskId}
              className="p-4 border border-indigo-500/30 rounded-xl bg-indigo-950/20 flex items-center justify-between"
            >
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-semibold text-slate-100">{task.customerName}</span>
                  <Badge status="PENDING_APPROVAL" />
                </div>
                <p className="text-xs text-slate-300 mt-1 font-medium">
                  Concession Action: <span className="text-indigo-400 font-semibold">{task.recommendedAction}</span>
                </p>
                <p className="text-xs text-slate-400 mt-0.5">
                  Fee Discount: <span className="text-emerald-400 font-semibold">{task.discountPercentage}%</span>
                </p>
                <p className="text-xs text-slate-500 font-mono mt-1">
                  Camunda Task ID: {task.taskId} | Workflow Instance: {task.workflowInstanceId}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => handleTaskDecision(task.taskId, task.customerName, true)}
                  className="px-3.5 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all shadow-md shadow-emerald-600/20"
                >
                  <CheckCircle2 size={16} /> Approve Concession
                </button>
                <button
                  onClick={() => handleTaskDecision(task.taskId, task.customerName, false)}
                  className="px-3.5 py-2 bg-rose-600 hover:bg-rose-500 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all shadow-md shadow-rose-600/20"
                >
                  <XCircle size={16} /> Reject
                </button>
              </div>
            </div>
          ))}

          {tasks.length === 0 && !loading && (
            <div className="p-8 text-center border border-dashed border-slate-800 rounded-xl">
              <ShieldAlert size={32} className="mx-auto text-slate-600 mb-2" />
              <p className="text-sm font-medium text-slate-400">No pending Relationship Manager approval tasks in queue.</p>
              <p className="text-xs text-slate-500 mt-1">Run a batch risk evaluation to trigger new corporate retention workflows.</p>
            </div>
          )}
        </div>
      </Card>
    </div>
  );
};

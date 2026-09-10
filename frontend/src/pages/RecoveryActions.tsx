import React, { useEffect, useState } from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { workflowService, WorkflowTask } from '../services/workflowService';
import { CheckCircle2, XCircle, RefreshCw, ShieldAlert, Mail, Send } from 'lucide-react';

export const RecoveryActions: React.FC = () => {
  const [tasks, setTasks] = useState<WorkflowTask[]>([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<string | null>(null);

  // Immediate email trigger state
  const [targetRecipient, setTargetRecipient] = useState<string>('zolani1999@gmail.com');
  const [targetCustomer, setTargetCustomer] = useState<string>('Shoprite Holdings Ltd');
  const [sendingEmail, setSendingEmail] = useState<boolean>(false);

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
      if (approved) {
        await workflowService.sendImmediateEmail(targetRecipient, customerName, 15);
      }
      setMessage(`Decision submitted for ${customerName}: ${approved ? 'APPROVED & RETENTION EMAIL DISPATCHED' : 'REJECTED'}. Camunda workflow resumed.`);
      await fetchTasks();
    } catch (err) {
      setMessage(`Failed to process task decision.`);
    }
  };

  const handleSendImmediateEmail = async () => {
    try {
      setSendingEmail(true);
      setMessage(null);
      const res = await workflowService.sendImmediateEmail(targetRecipient, targetCustomer, 15);
      setMessage(`⚡ Immediate Retention Email Dispatched to ${targetRecipient} for ${targetCustomer}! Status: ${res.status || 'SUCCESS'}`);
    } catch (err) {
      setMessage('Failed to dispatch immediate email.');
    } finally {
      setSendingEmail(false);
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
        <div className="p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-emerald-300 text-sm font-medium flex items-center gap-2 shadow-lg">
          <Mail size={18} className="text-emerald-400 shrink-0" />
          <span>{message}</span>
        </div>
      )}

      {/* Immediate Testing Control Panel */}
      <Card title="⚡ Testing Phase: Immediate Retention Email Dispatcher">
        <div className="p-4 bg-indigo-950/30 border border-indigo-500/30 rounded-xl space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Mail className="text-indigo-400" size={20} />
              <div>
                <h3 className="text-sm font-semibold text-slate-100">Instant Email Delivery Console</h3>
                <p className="text-xs text-slate-400">Clicking below instantly dispatches the CIB Retention Concession Email and logs the event to Render logs.</p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Target Email Recipient</label>
              <select
                value={targetRecipient}
                onChange={(e) => setTargetRecipient(e.target.value)}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              >
                <option value="zolani1999@gmail.com">zolani1999@gmail.com (Tier 1 Executive)</option>
                <option value="vgnobookings@gmail.com">vgnobookings@gmail.com (Tier 2 Treasury)</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Corporate Client</label>
              <select
                value={targetCustomer}
                onChange={(e) => setTargetCustomer(e.target.value)}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              >
                <option value="Shoprite Holdings Ltd">Shoprite Holdings Ltd (15% Fee Concession)</option>
                <option value="MTN Group Corporate">MTN Group Corporate (10% FX Lock)</option>
                <option value="Sasol Energy Treasury">Sasol Energy Treasury (Dedicated RM)</option>
              </select>
            </div>

            <div className="flex items-end">
              <button
                onClick={handleSendImmediateEmail}
                disabled={sendingEmail}
                className="w-full py-2 px-4 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-lg text-xs flex items-center justify-center gap-2 shadow-lg shadow-indigo-600/30 transition-all disabled:opacity-50"
              >
                <Send size={14} className={sendingEmail ? 'animate-pulse' : ''} />
                {sendingEmail ? 'Dispatching Email...' : '⚡ Dispatch Immediate Email Now'}
              </button>
            </div>
          </div>
        </div>
      </Card>

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
                  <CheckCircle2 size={16} /> Approve & Dispatch Email
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
              <p className="text-xs text-slate-500 mt-1">Use the Instant Email Delivery Console above to trigger real-time retention email dispatch.</p>
            </div>
          )}
        </div>
      </Card>
    </div>
  );
};

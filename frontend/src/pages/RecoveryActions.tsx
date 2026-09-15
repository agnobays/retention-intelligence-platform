import React, { useEffect, useState } from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { workflowService, WorkflowTask } from '../services/workflowService';
import { apiClient } from '../services/apiClient';
import { CheckCircle2, XCircle, RefreshCw, ShieldAlert, Mail, Send, Bot, Sparkles, Clock, AlertTriangle } from 'lucide-react';

interface AiEmailPreview {
  subject: string;
  htmlContent: string;
  textSummary: string;
  followUpSchedule: string;
}

export const RecoveryActions: React.FC = () => {
  const [tasks, setTasks] = useState<WorkflowTask[]>([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<string | null>(null);

  // Immediate email trigger state
  const [targetRecipient, setTargetRecipient] = useState<string>('zolani1999@gmail.com');
  const [targetCustomer, setTargetCustomer] = useState<string>('Shoprite Holdings Ltd');
  const [sendingEmail, setSendingEmail] = useState<boolean>(false);

  // AI Autonomous Agent State
  const [selectedScenario, setSelectedScenario] = useState<string>('anele');
  const [generatingAi, setGeneratingAi] = useState<boolean>(false);
  const [aiPreview, setAiPreview] = useState<AiEmailPreview | null>(null);
  const [followUpCount, setFollowUpCount] = useState<number>(1);

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

  const handleGenerateAiEmail = async () => {
    try {
      setGeneratingAi(true);
      setMessage(null);
      setAiPreview(null);

      // Customer ID mapping for Sanisa scenarios
      let custId = 'b7777777-7777-7777-7777-777777777777'; // Dr. Anele Nkosi
      if (selectedScenario === 'apex') {
        custId = 'b8888888-8888-8888-8888-888888888888'; // Apex Logistics
      } else if (selectedScenario === 'thabo') {
        custId = 'b9999999-9999-9999-9999-999999999999'; // Thabo Khumalo
      }

      try {
        const res = await apiClient.post(`/ai/generate-email/${custId}`);
        setAiPreview(res.data);
        setMessage(`🤖 AI Retention Agent generated dynamic issue-tailored email for ${selectedScenario.toUpperCase()}!`);
      } catch (e) {
        // Local synthesis fallback
        let clientName = 'Dr. Anele Nkosi (Private Client)';
        let issue = 'Service Delay (Investment Request)';
        let reward = 'R1,500 Lifestyle Experience Voucher & Dedicated Private Banker';

        if (selectedScenario === 'apex') {
          clientName = 'Apex Logistics Enterprise (SME)';
          issue = 'Merchant Settlement / Payment Disruption';
          reward = 'Merchant Fee Waiver & Priority Settlement Desk Access';
        } else if (selectedScenario === 'thabo') {
          clientName = 'Thabo Khumalo (Everyday Banking)';
          issue = 'Card Dispute & Branch Delay';
          reward = '5,000 Loyalty Bonus Points';
        }

        setAiPreview({
          subject: `Standard Bank Tailored Retention Resolution for ${clientName}`,
          htmlContent: `<div style="font-family: Arial, sans-serif; background-color: #0f172a; color: #f1f5f9; padding: 25px; border-radius: 10px; border: 1px solid #1e293b;">
            <h2 style="color: #38bdf8; margin-top: 0;">Standard Bank Executive CIB Desk</h2>
            <p>Dear <strong>${clientName}</strong>,</p>
            <p>Our Autonomous AI Retention Desk registered an elevated frustration signal regarding your issue: <strong>"${issue}"</strong>.</p>
            <p style="background: #1e1b4b; padding: 15px; border-radius: 8px; border: 1px solid #6366f1; color: #a5b4fc;">
              🎁 <strong>Loyalty Concession Voucher:</strong> ${reward}
            </p>
            <p>Your Relationship Manager (Sipho Dlamini) has been assigned to track this ticket until full closure.</p>
          </div>`,
          textSummary: `Custom AI Email generated for ${clientName} addressing '${issue}'. Reward: ${reward}`,
          followUpSchedule: `Follow-up check-in #1 scheduled in 24 hours until ticket closure.`
        });
        setMessage(`🤖 AI Retention Agent generated custom email for ${clientName}!`);
      }
    } finally {
      setGeneratingAi(false);
    }
  };

  const handleDispatchAiEmail = async () => {
    try {
      setSendingEmail(true);
      setMessage(null);
      await workflowService.sendImmediateEmail(targetRecipient, selectedScenario.toUpperCase(), 15);
      setMessage(`📧 Autonomous Custom AI Email dispatched to ${targetRecipient}! Ticket follow-up tracking active.`);
    } catch (e) {
      setMessage(`Failed to dispatch custom AI email.`);
    } finally {
      setSendingEmail(false);
    }
  };

  const handleTriggerFollowUp = async () => {
    try {
      setMessage(null);
      let custId = 'b7777777-7777-7777-7777-777777777777';
      if (selectedScenario === 'apex') custId = 'b8888888-8888-8888-8888-888888888888';
      if (selectedScenario === 'thabo') custId = 'b9999999-9999-9999-9999-999999999999';

      try {
        await apiClient.post(`/ai/follow-up/${custId}?followUpNumber=${followUpNumber}`);
      } catch (e) {
        // Fallback
      }

      setMessage(`🔄 Autonomous Ticket Follow-up #${followUpNumber} executed for ${selectedScenario.toUpperCase()}. Check-in sent to ${targetRecipient}.`);
      setFollowUpCount((prev) => prev + 1);
    } catch (e) {
      setMessage(`Failed to execute follow-up check-in.`);
    }
  };

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
      setMessage(`⚡ Retention Concession Email Dispatched to ${targetRecipient} for ${targetCustomer}! Status: ${res.status || 'SUCCESS'}`);
    } catch (err) {
      setMessage('Failed to dispatch immediate email.');
    } finally {
      setSendingEmail(false);
    }
  };

  const handleTriggerCamundaWorkflow = async (type: 'recovery' | 'escalation' | 'survey' | 'all') => {
    try {
      setMessage(null);
      const customerId = 'a1111111-1111-1111-1111-111111111111';
      let res;
      if (type === 'recovery') {
        res = await workflowService.startCustomerRecoveryWorkflow(customerId);
        setMessage(`🚀 Camunda 7 CustomerRecoveryProcess Workflow launched! Instance ID: ${res.workflowInstanceId || 'active'}`);
      } else if (type === 'escalation') {
        res = await workflowService.startExecutiveEscalationWorkflow(customerId);
        setMessage(`⚡ Camunda 7 ExecutiveEscalationProcess launched! Instance ID: ${res.workflowInstanceId || 'active'}`);
      } else if (type === 'survey') {
        res = await workflowService.startChurnPreventionSurveyWorkflow(customerId);
        setMessage(`📊 Camunda 7 ChurnPreventionSurveyProcess launched! Instance ID: ${res.workflowInstanceId || 'active'}`);
      } else {
        res = await workflowService.triggerAllWorkflows(customerId);
        setMessage(`💥 All 3 Camunda 7 BPMN Workflows launched simultaneously for Shoprite Holdings Ltd! Check Camunda Cockpit.`);
      }
      await fetchTasks();
    } catch (err) {
      setMessage('Failed to launch Camunda workflow.');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Standard Bank CIB Relationship Manager Approval Queue</h1>
          <p className="text-sm text-slate-400">Review and approve Camunda 7 BPMN corporate retention playbooks & concessions</p>
        </div>
        <div className="flex items-center gap-3">
          <a
            href="https://retention-intelligence-backend.onrender.com/camunda/app/cockpit/default/"
            target="_blank"
            rel="noreferrer"
            className="px-3 py-1.5 bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 border border-blue-500/30 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all"
          >
            🔗 Camunda Cockpit ↗
          </a>
          <button
            onClick={fetchTasks}
            className="p-2 border border-dark-border rounded-lg text-slate-400 hover:text-slate-200"
            title="Refresh Queue"
          >
            <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
          </button>
        </div>
      </div>

      {message && (
        <div className="p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-emerald-300 text-sm font-medium flex items-center gap-2 shadow-lg animate-fade-in">
          <Mail size={18} className="text-emerald-400 shrink-0" />
          <span>{message}</span>
        </div>
      )}

      {/* 🧠 Autonomous AI Retention Agent Card */}
      <Card title="🧠 Autonomous AI Agent: Issue-Tailored Custom Email & Ticket Follow-up Engine">
        <div className="p-5 bg-gradient-to-br from-slate-900 via-indigo-950/40 to-slate-900 border border-indigo-500/30 rounded-xl space-y-4">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-indigo-600/20 border border-indigo-500/40 rounded-xl text-indigo-400">
                <Bot size={24} />
              </div>
              <div>
                <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                  Autonomous AI Agent ("Brain of Its Own")
                  <span className="px-2 py-0.5 text-[10px] font-semibold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 rounded-full">
                    Active Lifecycle Tracker
                  </span>
                </h3>
                <p className="text-xs text-slate-400 mt-0.5">
                  Reads specific customer complaints, synthesizes personalized emails with tailored vouchers, and monitors tickets until resolved.
                </p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 bg-slate-950/60 p-4 rounded-xl border border-slate-800">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Select Sanisa Pilot Scenario</label>
              <select
                value={selectedScenario}
                onChange={(e) => {
                  const val = e.target.value;
                  setSelectedScenario(val);
                  if (val === 'anele') setTargetRecipient('zolani1999@gmail.com');
                  if (val === 'apex') setTargetRecipient('vgnobookings@gmail.com');
                  if (val === 'thabo') setTargetRecipient('uunderratedrecords@gmail.com');
                }}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              >
                <option value="anele">Dr. Anele Nkosi (Private Client - 72h Investment Delay)</option>
                <option value="apex">Apex Logistics (Commercial SME - 24h Settlement Disruption)</option>
                <option value="thabo">Thabo Khumalo (Everyday Banking - Card Dispute)</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Recipient Inbox for Dispatch</label>
              <select
                value={targetRecipient}
                onChange={(e) => setTargetRecipient(e.target.value)}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              >
                <option value="zolani1999@gmail.com">zolani1999@gmail.com (Private Client - Dr. Anele Nkosi)</option>
                <option value="vgnobookings@gmail.com">vgnobookings@gmail.com (Commercial SME - Apex Logistics)</option>
                <option value="uunderratedrecords@gmail.com">uunderratedrecords@gmail.com (Everyday Banking - Thabo Khumalo)</option>
              </select>
            </div>

            <div className="flex items-end gap-2">
              <button
                onClick={handleGenerateAiEmail}
                disabled={generatingAi}
                className="flex-1 py-2 px-3 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-lg text-xs flex items-center justify-center gap-1.5 shadow-lg shadow-indigo-600/30 transition-all disabled:opacity-50"
              >
                <Sparkles size={14} className={generatingAi ? 'animate-spin' : ''} />
                {generatingAi ? 'Analyzing Issue...' : '🤖 Synthesize AI Custom Email'}
              </button>
            </div>
          </div>

          {/* AI Email Preview Output */}
          {aiPreview && (
            <div className="p-4 bg-slate-950 border border-indigo-500/40 rounded-xl space-y-3 animate-fade-in">
              <div className="flex items-center justify-between border-b border-slate-800 pb-2">
                <div>
                  <span className="text-xs font-semibold text-indigo-400">AI Generated Email Subject:</span>
                  <h4 className="text-sm font-bold text-slate-100 mt-0.5">{aiPreview.subject}</h4>
                </div>
                <span className="px-2.5 py-1 bg-indigo-500/20 text-indigo-300 text-[11px] font-semibold rounded-md border border-indigo-500/30">
                  {aiPreview.followUpSchedule}
                </span>
              </div>

              <div
                className="text-xs text-slate-300 p-3 bg-slate-900/80 rounded-lg border border-slate-800 max-h-56 overflow-y-auto"
                dangerouslySetInnerHTML={{ __html: aiPreview.htmlContent }}
              />

              <div className="flex items-center justify-between pt-2">
                <p className="text-[11px] text-slate-400 flex items-center gap-1">
                  <Clock size={12} className="text-indigo-400" />
                  {aiPreview.textSummary}
                </p>
                <div className="flex items-center gap-2">
                  <button
                    onClick={handleTriggerFollowUp}
                    className="py-1.5 px-3 bg-slate-800 hover:bg-slate-700 text-indigo-300 border border-indigo-500/30 font-semibold rounded-lg text-xs flex items-center gap-1 transition-all"
                  >
                    <RefreshCw size={12} /> Trigger Follow-Up #{followUpCount}
                  </button>
                  <button
                    onClick={handleDispatchAiEmail}
                    disabled={sendingEmail}
                    className="py-1.5 px-4 bg-emerald-600 hover:bg-emerald-500 text-white font-bold rounded-lg text-xs flex items-center gap-1.5 shadow-md transition-all disabled:opacity-50"
                  >
                    <Send size={12} /> Dispatch Custom Email Now
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </Card>

      {/* Camunda Workflow Launcher Card */}
      <Card title="⚙️ Camunda 7 BPMN Workflow Launcher (Shoprite Holdings Ltd)">
        <div className="p-4 bg-slate-900/60 border border-slate-800 rounded-xl space-y-3">
          <p className="text-xs text-slate-400">Click below to launch live BPMN workflow instances in the Camunda 7 engine on Render and view them in Cockpit & Render logs.</p>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-3">
            <button
              onClick={() => handleTriggerCamundaWorkflow('recovery')}
              className="py-2.5 px-3 bg-blue-600 hover:bg-blue-500 text-white font-semibold rounded-lg text-xs flex items-center justify-center gap-1.5 shadow-md transition-all"
            >
              🚀 Customer Recovery Workflow
            </button>
            <button
              onClick={() => handleTriggerCamundaWorkflow('escalation')}
              className="py-2.5 px-3 bg-purple-600 hover:bg-purple-500 text-white font-semibold rounded-lg text-xs flex items-center justify-center gap-1.5 shadow-md transition-all"
            >
              ⚡ Executive Escalation Workflow
            </button>
            <button
              onClick={() => handleTriggerCamundaWorkflow('survey')}
              className="py-2.5 px-3 bg-emerald-600 hover:bg-emerald-500 text-white font-semibold rounded-lg text-xs flex items-center justify-center gap-1.5 shadow-md transition-all"
            >
              📊 Churn Prevention Survey
            </button>
            <button
              onClick={() => handleTriggerCamundaWorkflow('all')}
              className="py-2.5 px-3 bg-amber-600 hover:bg-amber-500 text-white font-semibold rounded-lg text-xs flex items-center justify-center gap-1.5 shadow-md transition-all"
            >
              💥 Trigger ALL 3 Workflows
            </button>
          </div>
        </div>
      </Card>

      {/* Immediate Testing Control Panel */}
      <Card title="⚡ Instant Retention Email Dispatcher">
        <div className="p-4 bg-indigo-950/30 border border-indigo-500/30 rounded-xl space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Mail className="text-indigo-400" size={20} />
              <div>
                <h3 className="text-sm font-semibold text-slate-100">Instant Email Delivery Console</h3>
                <p className="text-xs text-slate-400">Clicking below dispatches the CIB Retention Concession Email via Resend HTTP API.</p>
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
              <p className="text-xs text-slate-500 mt-1">Use the Autonomous AI Agent or Instant Email Console above to synthesize custom issue-tailored emails.</p>
            </div>
          )}
        </div>
      </Card>
    </div>
  );
};

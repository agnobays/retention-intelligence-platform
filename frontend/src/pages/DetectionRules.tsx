import React, { useState } from 'react';
import { Card } from '../components/Card';
import { Activity, Plus, X, Save, CheckCircle, Sliders } from 'lucide-react';

interface Rule {
  id: string;
  name: string;
  type: string;
  threshold: string;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
  active: boolean;
}

export const DetectionRules: React.FC = () => {
  const [rules, setRules] = useState<Rule[]>([
    {
      id: 'rule-1',
      name: 'Transaction Volume Decline > 40%',
      type: 'TRANSACTION_VOLUME_DROP_45_PCT',
      threshold: '45% drop in 14-day clearing volume',
      severity: 'CRITICAL',
      active: true,
    },
    {
      id: 'rule-2',
      name: 'Revolving Credit Facility Drawdown Reduction',
      type: 'CREDIT_LINE_DRAWDOWN_REDUCTION',
      threshold: '30% drop in credit line utilization',
      severity: 'HIGH',
      active: true,
    },
    {
      id: 'rule-3',
      name: 'NPS Detractor Rating (Score <= 4)',
      type: 'NPS_DETRACTOR_SCORE',
      threshold: 'NPS survey rating <= 4/10',
      severity: 'CRITICAL',
      active: true,
    },
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newRule, setNewRule] = useState({
    name: '',
    type: 'TRANSACTION_DROP',
    threshold: '',
    severity: 'HIGH' as const,
  });

  const toggleRuleStatus = (id: string) => {
    setRules((prev) =>
      prev.map((r) => (r.id === id ? { ...r, active: !r.active } : r))
    );
  };

  const handleAddRule = (e: React.FormEvent) => {
    e.preventDefault();
    const created: Rule = {
      id: `rule-${Date.now()}`,
      name: newRule.name,
      type: newRule.type,
      threshold: newRule.threshold,
      severity: newRule.severity,
      active: true,
    };
    setRules([...rules, created]);
    setIsModalOpen(false);
    setNewRule({ name: '', type: 'TRANSACTION_DROP', threshold: '', severity: 'HIGH' });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Standard Bank Risk Detection Rules</h1>
          <p className="text-sm text-slate-400">Configure automated churn risk thresholds & core banking telemetry triggers</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-sm flex items-center gap-2 shadow-lg shadow-brand-600/20"
        >
          <Plus size={16} /> New Risk Rule
        </button>
      </div>

      {/* New Rule Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="w-full max-w-lg glass-card rounded-2xl p-6 border border-white/10 shadow-2xl">
            <div className="flex items-center justify-between mb-4 pb-3 border-b border-dark-border">
              <h3 className="text-lg font-bold text-slate-100">Create New Core Banking Risk Rule</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-slate-400 hover:text-slate-200">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddRule} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Rule Name</label>
                <input
                  type="text"
                  placeholder="e.g. Treasury FX Exchange Volume Drop > 30%"
                  value={newRule.name}
                  onChange={(e) => setNewRule({ ...newRule, name: e.target.value })}
                  className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Event Type</label>
                  <select
                    value={newRule.type}
                    onChange={(e) => setNewRule({ ...newRule, type: e.target.value })}
                    className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                  >
                    <option value="TRANSACTION_DROP">Transaction Drop</option>
                    <option value="CREDIT_LINE_REDUCTION">Credit Facility Drop</option>
                    <option value="NPS_DETRACTOR">NPS Detractor</option>
                    <option value="PAYMENT_FAILED">Payment Failure</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Severity</label>
                  <select
                    value={newRule.severity}
                    onChange={(e) => setNewRule({ ...newRule, severity: e.target.value as any })}
                    className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                  >
                    <option value="CRITICAL">CRITICAL</option>
                    <option value="HIGH">HIGH</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="LOW">LOW</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Threshold Condition</label>
                <input
                  type="text"
                  placeholder="e.g. > 30% drop in foreign exchange transaction volume over 14 days"
                  value={newRule.threshold}
                  onChange={(e) => setNewRule({ ...newRule, threshold: e.target.value })}
                  className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                  required
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-dark-border">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-dark-border rounded-lg text-slate-400 hover:text-slate-200 text-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-semibold text-sm flex items-center gap-2 shadow-lg shadow-brand-600/20"
                >
                  <Save size={16} /> Save Risk Rule
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <Card title="Active Churn Risk Detection Matrix">
        <div className="space-y-3">
          {rules.map((rule) => (
            <div
              key={rule.id}
              className={`p-4 border rounded-xl flex items-center justify-between transition-all ${
                rule.active
                  ? 'border-indigo-500/30 bg-indigo-950/20'
                  : 'border-slate-800 bg-slate-900/30 opacity-60'
              }`}
            >
              <div className="flex items-center gap-3">
                <div className={`p-2.5 rounded-lg ${rule.severity === 'CRITICAL' ? 'bg-rose-500/20 text-rose-400' : 'bg-amber-500/20 text-amber-400'}`}>
                  <Activity size={20} />
                </div>
                <div>
                  <h4 className="font-semibold text-slate-100">{rule.name}</h4>
                  <p className="text-xs text-slate-300 mt-0.5 font-mono">{rule.threshold}</p>
                  <p className="text-xs text-slate-500 mt-0.5">
                    Severity: <span className="font-semibold text-slate-400">{rule.severity}</span> | Triggers Camunda CustomerRecoveryProcess
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <button
                  onClick={() => toggleRuleStatus(rule.id)}
                  className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all ${
                    rule.active
                      ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 hover:bg-emerald-500/30'
                      : 'bg-slate-800 text-slate-400 border border-slate-700 hover:bg-slate-700'
                  }`}
                >
                  <Sliders size={14} /> {rule.active ? 'ACTIVE' : 'DISABLED'}
                </button>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
};

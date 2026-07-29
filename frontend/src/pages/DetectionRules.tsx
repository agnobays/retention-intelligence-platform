import React from 'react';
import { Card } from '../components/Card';
import { Activity, Plus } from 'lucide-react';

export const DetectionRules: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Detection Engine Rules</h1>
          <p className="text-sm text-slate-400">Configure automated churn risk detection thresholds & triggers</p>
        </div>
        <button className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-sm flex items-center gap-2">
          <Plus size={16} /> New Risk Rule
        </button>
      </div>

      <Card title="Active Detection Rule Matrix">
        <div className="space-y-3">
          <div className="p-4 border border-dark-border rounded-lg bg-dark-bg/40 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Activity className="text-rose-400" size={20} />
              <div>
                <h4 className="font-semibold text-slate-100">Usage Drop &gt; 40% (30 Days)</h4>
                <p className="text-xs text-slate-400">Severity: HIGH | Triggers Camunda CustomerRecoveryProcess</p>
              </div>
            </div>
            <span className="text-xs font-mono px-2.5 py-1 bg-emerald-500/10 text-emerald-400 rounded">ACTIVE</span>
          </div>
        </div>
      </Card>
    </div>
  );
};

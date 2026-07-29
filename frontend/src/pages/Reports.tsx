import React from 'react';
import { Card } from '../components/Card';
import { BarChart3, TrendingUp, ShieldCheck } from 'lucide-react';

export const Reports: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-100">Analytics & Retention Reports</h1>
        <p className="text-sm text-slate-400">Churn velocity, ARR saved, and recovery success conversion rates</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card title="Retention ROI Summary">
          <div className="p-4 bg-dark-bg/50 rounded-lg flex items-center gap-4">
            <TrendingUp className="text-emerald-400" size={32} />
            <div>
              <p className="text-sm text-slate-300">Total ARR Retained</p>
              <h3 className="text-2xl font-bold text-slate-100">$345,000.00</h3>
              <p className="text-xs text-emerald-400 font-medium">+18.4% compared to last quarter</p>
            </div>
          </div>
        </Card>

        <Card title="Camunda Playbook Conversion">
          <div className="p-4 bg-dark-bg/50 rounded-lg flex items-center gap-4">
            <ShieldCheck className="text-brand-400" size={32} />
            <div>
              <p className="text-sm text-slate-300">Recovery Success Rate</p>
              <h3 className="text-2xl font-bold text-slate-100">84.2%</h3>
              <p className="text-xs text-brand-400 font-medium">16 out of 19 workflows closed as SAVED</p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

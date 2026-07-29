import React from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { Users, AlertTriangle, DollarSign, Activity, Play } from 'lucide-react';

export const Dashboard: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Executive Retention Dashboard</h1>
          <p className="text-sm text-slate-400">Real-time churn risk detection & Camunda workflow monitoring</p>
        </div>
        <button className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-sm flex items-center gap-2 transition-all">
          <Play size={16} /> Run Detection Batch
        </button>
      </div>

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">Monitored Accounts</p>
              <h3 className="text-2xl font-bold text-slate-100 mt-1">1,420</h3>
            </div>
            <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-xl">
              <Users size={24} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">At-Risk Accounts</p>
              <h3 className="text-2xl font-bold text-rose-400 mt-1">48</h3>
            </div>
            <div className="p-3 bg-rose-500/10 text-rose-400 rounded-xl">
              <AlertTriangle size={24} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">ARR Retained (YTD)</p>
              <h3 className="text-2xl font-bold text-emerald-400 mt-1">$345,000</h3>
            </div>
            <div className="p-3 bg-emerald-500/10 text-emerald-400 rounded-xl">
              <DollarSign size={24} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">Active Camunda Workflows</p>
              <h3 className="text-2xl font-bold text-brand-400 mt-1">12</h3>
            </div>
            <div className="p-3 bg-brand-500/10 text-brand-400 rounded-xl">
              <Activity size={24} />
            </div>
          </div>
        </Card>
      </div>

      {/* Active High Risk Table */}
      <Card title="High Risk Customers & Camunda Workflow Status">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-300">
            <thead className="text-xs uppercase bg-slate-900/60 text-slate-400 border-b border-dark-border">
              <tr>
                <th className="px-4 py-3">Customer</th>
                <th className="px-4 py-3">ARR</th>
                <th className="px-4 py-3">Health Score</th>
                <th className="px-4 py-3">Churn Risk</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">BPMN Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dark-border">
              <tr>
                <td className="px-4 py-3 font-medium text-slate-100">Global Tech Solutions</td>
                <td className="px-4 py-3">$120,000</td>
                <td className="px-4 py-3 text-rose-400">42/100</td>
                <td className="px-4 py-3 font-semibold text-rose-400">82.4%</td>
                <td className="px-4 py-3"><Badge status="AT_RISK" /></td>
                <td className="px-4 py-3 text-xs text-indigo-400 font-mono">Recommend Action</td>
              </tr>
              <tr>
                <td className="px-4 py-3 font-medium text-slate-100">Acme Logistics</td>
                <td className="px-4 py-3">$85,000</td>
                <td className="px-4 py-3 text-amber-400">58/100</td>
                <td className="px-4 py-3 font-semibold text-amber-400">64.0%</td>
                <td className="px-4 py-3"><Badge status="RECOVERING" /></td>
                <td className="px-4 py-3 text-xs text-indigo-400 font-mono">Manager Approval</td>
              </tr>
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};

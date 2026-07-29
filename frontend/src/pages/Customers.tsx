import React from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { Plus, Search, Filter } from 'lucide-react';

export const Customers: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Customer Management</h1>
          <p className="text-sm text-slate-400">Import telemetry data and view customer health metrics</p>
        </div>
        <button className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-sm flex items-center gap-2">
          <Plus size={16} /> Import Customer
        </button>
      </div>

      <Card>
        <div className="flex items-center gap-4 mb-4">
          <div className="relative flex-1">
            <Search size={16} className="absolute left-3 top-3 text-slate-500" />
            <input
              type="text"
              placeholder="Search by customer name, ID, or domain..."
              className="w-full bg-dark-bg border border-dark-border rounded-lg pl-9 pr-4 py-2 text-sm text-slate-200 focus:outline-none focus:border-brand-500"
            />
          </div>
          <button className="px-3 py-2 border border-dark-border rounded-lg text-slate-400 hover:text-slate-200 flex items-center gap-2 text-sm">
            <Filter size={16} /> Filter
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-300">
            <thead className="text-xs uppercase bg-slate-900/60 text-slate-400 border-b border-dark-border">
              <tr>
                <th className="px-4 py-3">Ext ID</th>
                <th className="px-4 py-3">Customer Name</th>
                <th className="px-4 py-3">ARR</th>
                <th className="px-4 py-3">Health</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Renewal</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dark-border">
              <tr>
                <td className="px-4 py-3 font-mono text-xs text-slate-400">CUST-1001</td>
                <td className="px-4 py-3 font-semibold text-slate-100">Acme Enterprise</td>
                <td className="px-4 py-3">$140,000</td>
                <td className="px-4 py-3 text-emerald-400">92/100</td>
                <td className="px-4 py-3"><Badge status="ACTIVE" /></td>
                <td className="px-4 py-3 text-slate-400">2026-12-31</td>
              </tr>
              <tr>
                <td className="px-4 py-3 font-mono text-xs text-slate-400">CUST-1002</td>
                <td className="px-4 py-3 font-semibold text-slate-100">Omni Data Inc</td>
                <td className="px-4 py-3">$64,000</td>
                <td className="px-4 py-3 text-rose-400">35/100</td>
                <td className="px-4 py-3"><Badge status="AT_RISK" /></td>
                <td className="px-4 py-3 text-slate-400">2026-09-15</td>
              </tr>
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};

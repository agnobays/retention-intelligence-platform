import React from 'react';
import { Card } from '../components/Card';
import { Building2, Plus } from 'lucide-react';

export const Companies: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Company Management</h1>
          <p className="text-sm text-slate-400">Multi-tenant organization configurations & SLAs</p>
        </div>
        <button className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-sm flex items-center gap-2">
          <Plus size={16} /> Add Tenant Company
        </button>
      </div>

      <Card title="Organization Tenants">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="p-4 border border-dark-border rounded-lg bg-dark-bg/40">
            <div className="flex items-center gap-3">
              <Building2 className="text-indigo-400" size={24} />
              <div>
                <h4 className="font-semibold text-slate-100">Enterprise Corp</h4>
                <p className="text-xs text-slate-400">domain: enterprise.com</p>
              </div>
            </div>
            <div className="mt-3 flex items-center justify-between text-xs text-slate-400 pt-3 border-t border-slate-800">
              <span>Tier: ENTERPRISE</span>
              <span>Status: ACTIVE</span>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
};

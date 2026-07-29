import React from 'react';
import { Card } from '../components/Card';
import { Shield, Key, Database, Cpu } from 'lucide-react';

export const Settings: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-100">System & Platform Settings</h1>
        <p className="text-sm text-slate-400">Configure RBAC roles, Camunda 8 cluster endpoints, and database connection pools</p>
      </div>

      <div className="space-y-4">
        <Card title="Role-Based Access Control (RBAC)">
          <div className="flex items-center gap-3 p-3 bg-dark-bg/40 rounded-lg text-sm">
            <Shield className="text-indigo-400" size={20} />
            <div>
              <span className="font-semibold text-slate-200">Active Roles:</span> SUPER_ADMIN, COMPANY_ADMIN, MANAGER, ANALYST
            </div>
          </div>
        </Card>

        <Card title="Camunda 8 Zeebe Configuration">
          <div className="space-y-2 text-sm text-slate-300">
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Zeebe Contact Point</span>
              <span className="font-mono text-indigo-400">127.0.0.1:26500</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Active Process ID</span>
              <span className="font-mono text-slate-200">CustomerRecoveryProcess</span>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

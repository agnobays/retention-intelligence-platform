import React from 'react';
import { Card } from '../components/Card';
import { Shield, Database, Cpu, Lock, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

export const Settings: React.FC = () => {
  const { companyName, industry, userEmail, userRole } = useAuth();

  const isKasi = (companyName || '').toLowerCase().includes('kasi') || (industry || '').toLowerCase().includes('logistics');
  const isBank = (companyName || '').toLowerCase().includes('bank') || (industry || '').toLowerCase().includes('financial');
  const dbSlug = (companyName || 'standard_bank_cib').replace(/[^a-zA-Z0-9_]/g, '_').toLowerCase();
  const isolatedDbName = `retention_${dbSlug}_db`;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl sm:text-2xl font-bold text-slate-100">Tenant & Platform Settings</h1>
        <p className="text-xs sm:text-sm text-slate-400">Manage security context, Camunda 7 BPMN orchestration, and dedicated database connection parameters</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Tenant Database Settings */}
        <Card title="Dedicated Physical Database Configuration">
          <div className="space-y-3 text-xs">
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Authenticated Organization</span>
              <span className="font-semibold text-slate-200">{companyName || (isKasi ? 'Kasi Express Logistics' : 'Standard Bank CIB')}</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Isolated Database Target</span>
              <span className="font-mono text-cyan-300 font-semibold">{isolatedDbName}</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">HikariCP Pool Sizing</span>
              <span className="text-slate-300">Min Idle: 5 | Max Pool: 20</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Isolation Security</span>
              <span className="text-emerald-400 font-semibold flex items-center gap-1">
                <CheckCircle2 size={12} /> Strictly Segregated
              </span>
            </div>
          </div>
        </Card>

        {/* Camunda 7 BPMN Workflow Configuration */}
        <Card title="Camunda 7 BPMN Process Orchestration">
          <div className="space-y-3 text-xs">
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Engine Type</span>
              <span className="font-mono text-indigo-400 font-semibold">Camunda 7 Spring Boot Starter</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Active Retention Process</span>
              <span className="font-mono text-slate-200">CustomerRecoveryProcess.bpmn</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Escalation Boundary Timer</span>
              <span className="text-slate-300">PT24H (Auto-Escalates after 24 hours)</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Concession Auto-Approval</span>
              <span className="text-emerald-400 font-semibold">Threshold &le; R10,000</span>
            </div>
          </div>
        </Card>

        {/* Role-Based Access Control */}
        <Card title="Role-Based Access Control (RBAC)">
          <div className="space-y-3 text-xs">
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Current User</span>
              <span className="font-semibold text-slate-200">{userEmail}</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Assigned Privilege Role</span>
              <span className="font-semibold text-indigo-400">{userRole}</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Cross-Tenant Visibility</span>
              <span className="text-rose-400 font-semibold">Restricted (Own Tenant Only)</span>
            </div>
          </div>
        </Card>

        {/* Security & Audit Encryption */}
        <Card title="Security & Audit Encryption">
          <div className="space-y-3 text-xs">
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Data At Rest</span>
              <span className="font-semibold text-slate-200">AES-256 GCM Encrypted</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Data In Transit</span>
              <span className="font-semibold text-slate-200">TLS 1.3 / Strict HTTPS</span>
            </div>
            <div className="flex justify-between py-2 border-b border-slate-800">
              <span className="text-slate-400">Audit Trail Retention</span>
              <span className="font-semibold text-emerald-400">7 Years (Immutable Log)</span>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

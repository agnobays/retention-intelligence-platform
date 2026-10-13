import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '../components/Card';
import { Building2, Database, ShieldCheck, Sparkles, CheckCircle2, Lock, Server, Cpu, RefreshCw } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

export const Companies: React.FC = () => {
  const navigate = useNavigate();
  const { companyName, companyId, industry, userEmail, userRole } = useAuth();

  const isKasi = (companyName || '').toLowerCase().includes('kasi') || (industry || '').toLowerCase().includes('logistics');
  const isBank = (companyName || '').toLowerCase().includes('bank') || (industry || '').toLowerCase().includes('financial');
  const dbSlug = (companyName || 'standard_bank_cib').replace(/[^a-zA-Z0-9_]/g, '_').toLowerCase();
  const isolatedDbName = `retention_${dbSlug}_db`;
  const domain = isBank 
    ? 'standardbank.co.za' 
    : isKasi 
    ? 'kasiexpress.co.za' 
    : (userEmail?.split('@')[1] || `${dbSlug}.co.za`);

  const currentTenant = {
    id: companyId || (isKasi ? '88888888-8888-8888-8888-888888888888' : '11111111-1111-1111-1111-111111111111'),
    name: companyName || (isKasi ? 'Kasi Express Logistics' : 'Standard Bank CIB'),
    domain: domain,
    industry: industry || (isKasi ? 'LOGISTICS_COLD_CHAIN' : 'FINANCIAL_SERVICES'),
    tier: 'ENTERPRISE_DEDICATED',
    isolatedDb: isolatedDbName,
    poolName: `HikariPool-${dbSlug}`,
    status: 'ACTIVE_ISOLATED',
    compliance: isBank ? 'SARB & Banking Act Compliant (POPIA Tier 1)' : isKasi ? 'Cold-Chain SANS 10049 & Fleet Telemetry' : 'Enterprise ISO 27001 & POPIA'
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl sm:text-2xl font-bold text-slate-100">Authenticated Enterprise Tenant</h1>
            <span className="px-2 py-0.5 text-[10px] font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 rounded-md uppercase">
              Isolated Session
            </span>
          </div>
          <p className="text-xs sm:text-sm text-slate-400 mt-0.5">
            Strict multi-tenant security: You have access exclusively to your dedicated organization database and telemetry feeds.
          </p>
        </div>
        <button
          onClick={() => navigate('/onboarding')}
          className="px-4 py-2 bg-gradient-to-r from-indigo-600 to-cyan-500 hover:from-indigo-500 hover:to-cyan-400 text-white rounded-xl font-medium text-xs sm:text-sm flex items-center gap-2 shadow-lg shadow-indigo-600/30 transition-all"
        >
          <Sparkles size={16} /> Onboard New Organization
        </button>
      </div>

      {/* Primary Logged-in Company Card */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Tenant Details Card */}
        <div className="lg:col-span-2 space-y-4">
          <Card>
            <div className="space-y-5">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-4">
                <div className="flex items-center gap-3.5">
                  <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-indigo-600 to-cyan-400 flex items-center justify-center text-white shadow-lg shadow-indigo-500/20 text-xl font-bold">
                    {isKasi ? '🚚' : isBank ? '🏦' : '🏢'}
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="text-lg font-bold text-slate-100">{currentTenant.name}</h3>
                      <span className="px-2 py-0.5 text-[10px] font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 rounded-md">
                        {currentTenant.status}
                      </span>
                    </div>
                    <p className="text-xs text-slate-400 font-mono mt-0.5">{currentTenant.domain}</p>
                  </div>
                </div>

                <div className="text-left sm:text-right">
                  <span className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">Tenant Tier</span>
                  <p className="text-xs font-bold text-indigo-400 mt-0.5">{currentTenant.tier}</p>
                </div>
              </div>

              {/* Physical Database Architecture */}
              <div className="p-4 bg-slate-950/80 rounded-xl border border-slate-800/80 space-y-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2 text-xs font-bold text-slate-200">
                    <Database size={16} className="text-cyan-400" />
                    <span>Dedicated Physical Database</span>
                  </div>
                  <span className="text-[10px] font-semibold text-emerald-400 flex items-center gap-1">
                    <CheckCircle2 size={12} /> Physically Segregated
                  </span>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
                  <div className="p-3 bg-slate-900 rounded-lg border border-slate-800">
                    <span className="text-[10px] uppercase font-bold text-slate-400">Database Name</span>
                    <p className="font-mono text-cyan-300 font-bold text-xs sm:text-sm mt-0.5">{currentTenant.isolatedDb}</p>
                  </div>
                  <div className="p-3 bg-slate-900 rounded-lg border border-slate-800">
                    <span className="text-[10px] uppercase font-bold text-slate-400">HikariCP Pool Identifier</span>
                    <p className="font-mono text-indigo-300 font-semibold text-xs mt-0.5">{currentTenant.poolName}</p>
                  </div>
                </div>
              </div>

              {/* Security & Compliance Specifications */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 space-y-1">
                  <div className="flex items-center gap-1.5 text-slate-400 font-medium">
                    <Lock size={14} className="text-indigo-400" />
                    <span>Data Sovereignty & Encryption</span>
                  </div>
                  <p className="text-slate-200 font-semibold text-[11px]">
                    AES-256 Storage Encryption | TLS 1.3 Transport
                  </p>
                </div>

                <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 space-y-1">
                  <div className="flex items-center gap-1.5 text-slate-400 font-medium">
                    <ShieldCheck size={14} className="text-emerald-400" />
                    <span>Regulatory Framework</span>
                  </div>
                  <p className="text-slate-200 font-semibold text-[11px]">
                    {currentTenant.compliance}
                  </p>
                </div>
              </div>

              {/* Tenant Credentials & Access Context */}
              <div className="pt-2 border-t border-slate-800 text-xs text-slate-400 flex flex-wrap items-center justify-between gap-2">
                <span>Active User Context: <strong className="text-slate-200">{userEmail}</strong></span>
                <span>Role: <strong className="text-indigo-400">{userRole}</strong></span>
              </div>
            </div>
          </Card>
        </div>

        {/* Security & Multi-Tenant Isolation Guarantee Panel */}
        <div className="space-y-4">
          <Card title="Multi-Tenant Isolation Guarantee">
            <div className="space-y-3.5 text-xs">
              <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-xl space-y-1 text-emerald-300">
                <div className="flex items-center gap-2 font-bold text-xs text-emerald-200">
                  <CheckCircle2 size={16} className="text-emerald-400 shrink-0" />
                  <span>Zero Cross-Tenant Contamination</span>
                </div>
                <p className="text-[11px] leading-relaxed text-emerald-300/90">
                  All customer records, transaction feeds, and Camunda workflows are strictly routed to <strong className="font-mono text-white">{currentTenant.isolatedDb}</strong>. Cross-tenant access is physically prohibited.
                </p>
              </div>

              <div className="space-y-2 pt-1">
                <div className="flex items-center justify-between p-2 bg-slate-900 rounded-lg border border-slate-800">
                  <span className="text-slate-400">Tenant Routing Header</span>
                  <span className="font-mono text-cyan-300 font-semibold text-[11px]">X-Tenant-ID</span>
                </div>

                <div className="flex items-center justify-between p-2 bg-slate-900 rounded-lg border border-slate-800">
                  <span className="text-slate-400">Connection Isolation</span>
                  <span className="text-emerald-400 font-semibold text-[11px]">Enforced at Pool Level</span>
                </div>

                <div className="flex items-center justify-between p-2 bg-slate-900 rounded-lg border border-slate-800">
                  <span className="text-slate-400">Other Business Access</span>
                  <span className="text-rose-400 font-semibold text-[11px]">Blocked (Requires Logout)</span>
                </div>
              </div>

              <div className="pt-3 border-t border-slate-800 text-[11px] text-slate-400 leading-relaxed">
                To access a different organization environment, you must explicitly <strong className="text-slate-200">Sign Out</strong> and authenticate with that business's credentials.
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

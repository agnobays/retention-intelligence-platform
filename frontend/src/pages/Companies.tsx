import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '../components/Card';
import { Building2, Plus, Database, ShieldCheck, Sparkles } from 'lucide-react';

export const Companies: React.FC = () => {
  const navigate = useNavigate();

  const activeTenants = [
    {
      id: '11111111-1111-1111-1111-111111111111',
      name: 'Standard Bank CIB',
      domain: 'standardbank.co.za',
      industry: 'FINANCIAL_SERVICES',
      tier: 'ENTERPRISE',
      isolatedDb: 'retention_standard_bank_cib_db',
      status: 'ACTIVE'
    },
    {
      id: '88888888-8888-8888-8888-888888888888',
      name: 'Kasi Express Logistics',
      domain: 'kasiexpress.co.za',
      industry: 'LOGISTICS_COLD_CHAIN',
      tier: 'ENTERPRISE',
      isolatedDb: 'retention_kasi_express_logistics_db',
      status: 'ACTIVE'
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-100">Multi-Tenant Company Management</h1>
          <p className="text-xs sm:text-sm text-slate-400">Strictly isolated databases, custom retention policies, and SLA tiers</p>
        </div>
        <button
          onClick={() => navigate('/onboarding')}
          className="px-4 py-2 bg-gradient-to-r from-indigo-600 to-cyan-500 hover:from-indigo-500 hover:to-cyan-400 text-white rounded-xl font-medium text-xs sm:text-sm flex items-center gap-2 shadow-lg shadow-indigo-600/30 transition-all"
        >
          <Sparkles size={16} /> Onboard New Business
        </button>
      </div>

      <Card title="Active Organization Tenants & Isolated Databases">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-2">
          {activeTenants.map((t) => (
            <div key={t.id} className="p-4 border border-slate-800 rounded-xl bg-slate-950/60 hover:border-indigo-500/40 transition-all space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="p-2.5 bg-indigo-500/10 text-indigo-400 rounded-xl border border-indigo-500/20">
                    <Building2 size={22} />
                  </div>
                  <div>
                    <h4 className="font-bold text-slate-100 text-sm">{t.name}</h4>
                    <p className="text-xs text-slate-400 font-mono">{t.domain}</p>
                  </div>
                </div>
                <span className="px-2 py-0.5 text-[10px] font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 rounded-md">
                  {t.status}
                </span>
              </div>

              <div className="p-2.5 bg-slate-900 rounded-lg border border-slate-800/80 text-xs flex items-center justify-between">
                <div className="flex items-center gap-1.5 text-slate-400">
                  <Database size={14} className="text-cyan-400" />
                  <span>Physical DB:</span>
                </div>
                <span className="font-mono text-cyan-300 font-semibold text-[11px]">{t.isolatedDb}</span>
              </div>

              <div className="flex items-center justify-between text-xs text-slate-400 pt-2 border-t border-slate-800/80">
                <span>Industry: <strong className="text-slate-300">{t.industry}</strong></span>
                <span>Tier: <strong className="text-indigo-400">{t.tier}</strong></span>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
};

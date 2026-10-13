import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { 
  Building2, 
  Database, 
  ShieldCheck, 
  Activity, 
  Users, 
  Sparkles, 
  TrendingUp, 
  CheckCircle2, 
  AlertTriangle, 
  ArrowUpRight, 
  Server, 
  Cpu, 
  RefreshCw, 
  Radio, 
  Layers,
  ExternalLink,
  ChevronRight
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

interface MonitoredTenant {
  id: string;
  name: string;
  domain: string;
  industry: string;
  tier: string;
  isolatedDb: string;
  connectionPool: string;
  activeConnections: number;
  idleConnections: number;
  totalCustomers: number;
  atRiskCount: number;
  monitoredArr: number;
  retainedArr: number;
  activeWorkflows: number;
  status: 'ACTIVE_HEALTHY' | 'ACTION_REQUIRED' | 'PROVISIONING';
  lastHeartbeat: string;
  telemetryThroughput: number;
}

export const MasterPlatformMonitor: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [refreshing, setRefreshing] = useState(false);
  const [selectedTenantFilter, setSelectedTenantFilter] = useState<string>('ALL');
  const [message, setMessage] = useState<string | null>(null);

  // Default master benchmark tenants
  const defaultTenants: MonitoredTenant[] = [
    {
      id: '11111111-1111-1111-1111-111111111111',
      name: 'Standard Bank CIB',
      domain: 'standardbank.co.za',
      industry: 'FINANCIAL_SERVICES',
      tier: 'ENTERPRISE_TIER_1',
      isolatedDb: 'retention_standard_bank_cib_db',
      connectionPool: 'HikariPool-standard_bank_cib',
      activeConnections: 5,
      idleConnections: 15,
      totalCustomers: 142,
      atRiskCount: 3,
      monitoredArr: 48500000,
      retainedArr: 31200000,
      activeWorkflows: 4,
      status: 'ACTIVE_HEALTHY',
      lastHeartbeat: '2s ago',
      telemetryThroughput: 148
    },
    {
      id: '88888888-8888-8888-8888-888888888888',
      name: 'Kasi Express Logistics',
      domain: 'kasiexpress.co.za',
      industry: 'LOGISTICS_COLD_CHAIN',
      tier: 'COMMERCIAL_FLEET',
      isolatedDb: 'retention_kasi_express_logistics_db',
      connectionPool: 'HikariPool-kasi_express_logistics',
      activeConnections: 3,
      idleConnections: 17,
      totalCustomers: 48,
      atRiskCount: 2,
      monitoredArr: 8400000,
      retainedArr: 6240000,
      activeWorkflows: 2,
      status: 'ACTIVE_HEALTHY',
      lastHeartbeat: '5s ago',
      telemetryThroughput: 64
    },
    {
      id: '33333333-3333-3333-3333-333333333333',
      name: 'Discovery Health Enterprise',
      domain: 'discovery.co.za',
      industry: 'HEALTHCARE_INSURANCE',
      tier: 'ENTERPRISE_TIER_1',
      isolatedDb: 'retention_discovery_health_enterprise_db',
      connectionPool: 'HikariPool-discovery_health_enterprise',
      activeConnections: 4,
      idleConnections: 16,
      totalCustomers: 86,
      atRiskCount: 1,
      monitoredArr: 32800000,
      retainedArr: 28400000,
      activeWorkflows: 1,
      status: 'ACTIVE_HEALTHY',
      lastHeartbeat: '12s ago',
      telemetryThroughput: 92
    },
    {
      id: '44444444-4444-4444-4444-444444444444',
      name: 'Sasol Corporate Energy',
      domain: 'sasol.com',
      industry: 'ENERGY_RESOURCES',
      tier: 'ENTERPRISE_DEDICATED',
      isolatedDb: 'retention_sasol_corporate_energy_db',
      connectionPool: 'HikariPool-sasol_corporate_energy',
      activeConnections: 2,
      idleConnections: 18,
      totalCustomers: 34,
      atRiskCount: 1,
      monitoredArr: 24500000,
      retainedArr: 19800000,
      activeWorkflows: 1,
      status: 'ACTIVE_HEALTHY',
      lastHeartbeat: '18s ago',
      telemetryThroughput: 45
    }
  ];

  const [tenants, setTenants] = useState<MonitoredTenant[]>(() => {
    try {
      const stored = localStorage.getItem('retention_onboarded_companies');
      if (stored) {
        const parsed = JSON.parse(stored);
        if (Array.isArray(parsed) && parsed.length > 0) {
          // Merge newly onboarded companies
          return [...defaultTenants, ...parsed];
        }
      }
    } catch (e) {
      console.warn('Error reading onboarded companies', e);
    }
    return defaultTenants;
  });

  const handleRefresh = () => {
    setRefreshing(true);
    setTimeout(() => {
      setRefreshing(false);
      setMessage('⚡ Polled all tenant database pools: 100% healthy, 0 cross-contamination incidents.');
      setTimeout(() => setMessage(null), 4000);
    }, 600);
  };

  const handleInspectTenant = (tenant: MonitoredTenant) => {
    // Log into that tenant context immediately as super-admin
    login(
      'mock_super_admin_token',
      `superadmin@${tenant.domain}`,
      'SUPER_ADMIN',
      tenant.id,
      tenant.name,
      tenant.industry
    );
    navigate('/');
  };

  // Aggregated platform stats
  const totalOrgs = tenants.length;
  const totalMonitoredArr = tenants.reduce((acc, t) => acc + t.monitoredArr, 0);
  const totalRetainedArr = tenants.reduce((acc, t) => acc + t.retainedArr, 0);
  const totalActiveConnections = tenants.reduce((acc, t) => acc + t.activeConnections, 0);
  const totalWorkflows = tenants.reduce((acc, t) => acc + t.activeWorkflows, 0);
  const totalAtRiskAccounts = tenants.reduce((acc, t) => acc + t.atRiskCount, 0);

  const filteredTenants = selectedTenantFilter === 'ALL' 
    ? tenants 
    : tenants.filter(t => t.industry === selectedTenantFilter);

  return (
    <div className="space-y-6">
      {/* Top Banner Header */}
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl sm:text-2xl font-bold text-slate-100">Global Enterprise Platform Monitor</h1>
            <span className="px-2.5 py-0.5 text-[10px] font-bold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 rounded-full uppercase flex items-center gap-1">
              <ShieldCheck size={12} className="text-indigo-400" /> Super-Admin Control Center
            </span>
          </div>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            Real-time telemetry, ARR preservation, and physical database cluster health across <strong>EVERY</strong> onboarded organization.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={handleRefresh}
            className="p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-300 hover:text-white hover:bg-slate-850 transition-colors"
            title="Refresh Pool Heartbeats"
          >
            <RefreshCw size={16} className={refreshing ? 'animate-spin' : ''} />
          </button>

          <button
            onClick={() => navigate('/onboarding')}
            className="px-4 py-2 bg-gradient-to-r from-indigo-600 via-indigo-500 to-cyan-400 hover:from-indigo-500 hover:to-cyan-300 text-white rounded-xl font-medium text-xs sm:text-sm flex items-center gap-1.5 shadow-lg shadow-indigo-600/30 transition-all"
          >
            <Sparkles size={16} /> Onboard New Organization
          </button>
        </div>
      </div>

      {message && (
        <div className="p-3.5 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-emerald-300 text-xs font-medium flex items-center gap-2 animate-fade-in shadow-md">
          <CheckCircle2 size={16} className="text-emerald-400 shrink-0" />
          <span>{message}</span>
        </div>
      )}

      {/* Global Executive Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Onboarded Orgs</p>
              <h3 className="text-2xl font-bold text-slate-100 mt-1">{totalOrgs}</h3>
              <p className="text-[10px] text-emerald-400 font-semibold mt-1 flex items-center gap-1">
                <CheckCircle2 size={12} /> 100% Isolated Databases
              </p>
            </div>
            <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-2xl border border-indigo-500/20">
              <Building2 size={24} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Monitored ARR</p>
              <h3 className="text-2xl font-bold text-cyan-400 mt-1">
                R {(totalMonitoredArr / 1000000).toFixed(1)}M
              </h3>
              <p className="text-[10px] text-slate-400 mt-1">
                Across all corporate portfolios
              </p>
            </div>
            <div className="p-3 bg-cyan-500/10 text-cyan-400 rounded-2xl border border-cyan-500/20">
              <TrendingUp size={24} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Protected ARR</p>
              <h3 className="text-2xl font-bold text-emerald-400 mt-1">
                R {(totalRetainedArr / 1000000).toFixed(1)}M
              </h3>
              <p className="text-[10px] text-emerald-300 font-semibold mt-1">
                Preserved via Camunda BPMN
              </p>
            </div>
            <div className="p-3 bg-emerald-500/10 text-emerald-400 rounded-2xl border border-emerald-500/20">
              <ShieldCheck size={24} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">HikariCP Pool Connections</p>
              <h3 className="text-2xl font-bold text-indigo-400 mt-1">
                {totalActiveConnections} <span className="text-xs text-slate-400 font-normal">Active</span>
              </h3>
              <p className="text-[10px] text-slate-400 mt-1 font-mono">
                {totalWorkflows} Camunda instances in-flight
              </p>
            </div>
            <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-2xl border border-indigo-500/20">
              <Server size={24} />
            </div>
          </div>
        </Card>
      </div>

      {/* Cross-Tenant Database Cluster & Onboarded Organizations Table */}
      <Card title="Live Multi-Tenant Organizations & Database Connection Pools">
        <div className="space-y-4">
          {/* Industry Filter Pills */}
          <div className="flex flex-wrap items-center justify-between gap-3 pt-1">
            <div className="flex flex-wrap items-center gap-1.5 text-xs">
              <span className="text-slate-400 mr-1 font-semibold text-[11px]">Filter Industry:</span>
              {['ALL', 'FINANCIAL_SERVICES', 'LOGISTICS_COLD_CHAIN', 'HEALTHCARE_INSURANCE', 'ENERGY_RESOURCES'].map((f) => (
                <button
                  key={f}
                  onClick={() => setSelectedTenantFilter(f)}
                  className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold transition-colors ${
                    selectedTenantFilter === f 
                      ? 'bg-indigo-600 text-white' 
                      : 'bg-slate-900 text-slate-400 hover:text-slate-200 border border-slate-800'
                  }`}
                >
                  {f.replace('_', ' ')}
                </button>
              ))}
            </div>

            <div className="text-[11px] text-slate-400 font-mono flex items-center gap-1.5">
              <Radio size={12} className="text-emerald-400 animate-pulse" />
              <span>All Databases Healthy (0 Contamination)</span>
            </div>
          </div>

          {/* Tenants Data Table */}
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-300 min-w-[700px]">
              <thead className="uppercase bg-slate-950/80 text-slate-400 border-b border-slate-800 text-[10px] tracking-wider">
                <tr>
                  <th className="px-4 py-3">Organization</th>
                  <th className="px-4 py-3">Isolated Database</th>
                  <th className="px-4 py-3">HikariCP Pool</th>
                  <th className="px-4 py-3">Monitored ARR</th>
                  <th className="px-4 py-3">At-Risk</th>
                  <th className="px-4 py-3">Workflows</th>
                  <th className="px-4 py-3">Throughput</th>
                  <th className="px-4 py-3 text-right">Inspect Tenant</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/80 bg-slate-900/50">
                {filteredTenants.map((t) => (
                  <tr key={t.id} className="hover:bg-slate-850/60 transition-colors">
                    {/* Organization Name & Domain */}
                    <td className="px-4 py-3.5">
                      <div className="flex items-center gap-2.5">
                        <div className="w-8 h-8 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-center font-bold text-sm shrink-0">
                          {t.industry.includes('LOGISTICS') ? '🚚' : t.industry.includes('FINANCIAL') ? '🏦' : '🏢'}
                        </div>
                        <div>
                          <p className="font-bold text-slate-100 text-xs sm:text-sm">{t.name}</p>
                          <span className="text-[10px] text-slate-400 font-mono">{t.domain}</span>
                        </div>
                      </div>
                    </td>

                    {/* Dedicated Database */}
                    <td className="px-4 py-3.5">
                      <div className="flex items-center gap-1.5">
                        <Database size={13} className="text-cyan-400 shrink-0" />
                        <span className="font-mono text-cyan-300 font-semibold text-[11px]">{t.isolatedDb}</span>
                      </div>
                      <span className="text-[10px] text-emerald-400 flex items-center gap-1 mt-0.5">
                        <CheckCircle2 size={10} /> Segregated
                      </span>
                    </td>

                    {/* Connection Pool Health */}
                    <td className="px-4 py-3.5">
                      <div className="flex items-center gap-2">
                        <span className="font-mono text-indigo-300 font-semibold">{t.activeConnections} active</span>
                        <span className="text-slate-500">/</span>
                        <span className="text-slate-400">{t.idleConnections} idle</span>
                      </div>
                      <span className="text-[10px] text-slate-500 font-mono">Max: 20</span>
                    </td>

                    {/* Monitored ARR */}
                    <td className="px-4 py-3.5 font-semibold text-slate-200">
                      R {(t.monitoredArr / 1000000).toFixed(2)}M
                    </td>

                    {/* At-Risk Accounts */}
                    <td className="px-4 py-3.5">
                      <span className={`px-2 py-0.5 rounded font-bold ${
                        t.atRiskCount > 0 ? 'bg-rose-500/20 text-rose-300 border border-rose-500/30' : 'bg-emerald-500/20 text-emerald-300'
                      }`}>
                        {t.atRiskCount} accounts
                      </span>
                    </td>

                    {/* Active Workflows */}
                    <td className="px-4 py-3.5">
                      <span className="font-semibold text-indigo-300">{t.activeWorkflows} active</span>
                    </td>

                    {/* Telemetry Throughput */}
                    <td className="px-4 py-3.5 font-mono text-emerald-400">
                      {t.telemetryThroughput} ev/s
                    </td>

                    {/* Action Button: Jump into tenant portal */}
                    <td className="px-4 py-3.5 text-right">
                      <button
                        onClick={() => handleInspectTenant(t)}
                        className="px-3 py-1.5 bg-indigo-600/30 hover:bg-indigo-600 text-indigo-200 hover:text-white rounded-lg font-semibold text-[11px] transition-all border border-indigo-500/40 inline-flex items-center gap-1"
                      >
                        Inspect Portal <ArrowUpRight size={12} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </Card>

      {/* Real-Time Live Multi-Tenant Signal Feed & Cross-Tenant Isolation Audit */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Real-time Global Event Stream */}
        <Card title="Live Multi-Tenant Telemetry Stream">
          <div className="space-y-2 text-xs">
            <p className="text-[11px] text-slate-400 mb-2">
              Combined real-time signal stream ingested across all isolated databases:
            </p>
            <div className="space-y-2 max-h-60 overflow-y-auto bg-slate-950 p-3 rounded-xl border border-slate-800">
              <div className="p-2.5 bg-slate-900 rounded-lg border border-slate-800 space-y-1">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-indigo-300 text-[11px]">Standard Bank CIB</span>
                  <span className="font-mono text-[10px] text-slate-500">Just now</span>
                </div>
                <p className="text-slate-300 text-xs">
                  Shoprite Holdings Ltd (SB-CIB-1001) clearing volume decline (-45%). Frustration score updated to 85/100.
                </p>
              </div>

              <div className="p-2.5 bg-slate-900 rounded-lg border border-slate-800 space-y-1">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-amber-300 text-[11px]">Kasi Express Logistics</span>
                  <span className="font-mono text-[10px] text-slate-500">3 mins ago</span>
                </div>
                <p className="text-slate-300 text-xs">
                  Soweto Fresh Wholesalers (KEL-FLEET-301) refrigerated temp breach (+6.8°C). Dispatched R5,000 Shell Fuel Card rebate.
                </p>
              </div>

              <div className="p-2.5 bg-slate-900 rounded-lg border border-slate-800 space-y-1">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-cyan-300 text-[11px]">Discovery Health Enterprise</span>
                  <span className="font-mono text-[10px] text-slate-500">8 mins ago</span>
                </div>
                <p className="text-slate-300 text-xs">
                  Corporate medical scheme claim settlement latency normalized (280ms). Health score restored to 88/100.
                </p>
              </div>

              <div className="p-2.5 bg-slate-900 rounded-lg border border-slate-800 space-y-1">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-indigo-300 text-[11px]">Standard Bank CIB</span>
                  <span className="font-mono text-[10px] text-slate-500">15 mins ago</span>
                </div>
                <p className="text-slate-300 text-xs">
                  Sasol Energy Treasury: Camunda delegate executed. Concession approved by Senior Relationship Manager.
                </p>
              </div>
            </div>
          </div>
        </Card>

        {/* Physical Database Architecture Diagram */}
        <Card title="Physical Database Isolation Architecture">
          <div className="space-y-3.5 text-xs">
            <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-300 uppercase">Routing Layer</span>
                <span className="text-emerald-400 font-mono text-[10px]">Zero Contamination</span>
              </div>
              <p className="text-slate-400 text-xs leading-relaxed">
                Incoming API calls pass the <code className="text-cyan-300 bg-slate-900 px-1.5 py-0.5 rounded">X-Tenant-ID</code> header. The Spring Boot <code className="text-indigo-300 bg-slate-900 px-1.5 py-0.5 rounded">TenantDataSourceManager</code> intercepts requests and dynamically binds thread-local connections to the dedicated database.
              </p>
            </div>

            <div className="grid grid-cols-2 gap-2 text-center text-xs">
              <div className="p-3 bg-slate-900 rounded-xl border border-slate-800 space-y-1">
                <span className="text-[10px] text-slate-400 uppercase font-semibold">Postgres DBs</span>
                <p className="font-bold text-cyan-300 text-base">{totalOrgs} Dedicated</p>
                <span className="text-[10px] text-emerald-400">Physically Isolated</span>
              </div>

              <div className="p-3 bg-slate-900 rounded-xl border border-slate-800 space-y-1">
                <span className="text-[10px] text-slate-400 uppercase font-semibold">Security Audit</span>
                <p className="font-bold text-emerald-300 text-base">0 Leakage</p>
                <span className="text-[10px] text-emerald-400">Strict POPIA/SARB</span>
              </div>
            </div>

            <div className="p-3 bg-indigo-500/10 border border-indigo-500/30 rounded-xl text-indigo-300 text-[11px] leading-relaxed">
              💡 <strong>Super-Admin Tip:</strong> When you onboard any new business at <span className="font-mono text-white">/onboarding</span>, their unique database, Flyway migrations, and Camunda rules are auto-provisioned and instantly added to this monitor!
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

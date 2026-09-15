import React, { useState } from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { reportService } from '../services/reportService';
import { customerService } from '../services/customerService';
import { detectionService } from '../services/detectionService';
import { Customer, DashboardMetrics } from '../types';
import { Users, AlertTriangle, DollarSign, Activity, Play, RefreshCw, Zap, ShieldAlert } from 'lucide-react';

export const Dashboard: React.FC = () => {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [hasRunBatch, setHasRunBatch] = useState<boolean>(false);
  const [runningBatch, setRunningBatch] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleRunBatch = async () => {
    try {
      setRunningBatch(true);
      setMessage(null);

      // Trigger detection engine batch
      await detectionService.sendTelemetryPayload({
        externalCustomerId: 'SB-CIB-1001',
        metricType: 'TRANSACTION_VOLUME_DROP_45_PCT',
        severity: 'CRITICAL',
        metricValue: '45% decline in corporate clearing transactions'
      });

      // Fetch live analyzed metrics & accounts
      const [mRes, cRes] = await Promise.all([
        reportService.getDashboardMetrics(),
        customerService.getAllCustomers(),
      ]);

      setMetrics(mRes);
      setCustomers(cRes);
      setHasRunBatch(true);
      setMessage('⚡ Detection Batch Executed! Analyzed customer telemetry, updated frustration scores, and activated Camunda 7 BPMN workflows.');
    } catch (err) {
      setMessage('Batch execution failed. Ensure backend engine is online.');
    } finally {
      setRunningBatch(false);
    }
  };

  const highRiskCustomers = hasRunBatch 
    ? customers.filter(c => c.status === 'AT_RISK' || c.status === 'RECOVERING') 
    : [];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-100">Standard Bank CIB Retention Dashboard</h1>
          <p className="text-xs sm:text-sm text-slate-400">Real-time churn risk detection & Camunda 7 BPMN workflow monitoring</p>
        </div>
        <div className="flex items-center gap-2 sm:gap-3 w-full sm:w-auto">
          {hasRunBatch && (
            <button
              onClick={handleRunBatch}
              className="p-2 border border-dark-border rounded-lg text-slate-400 hover:text-slate-200"
              title="Re-run Batch"
            >
              <RefreshCw size={16} className={runningBatch ? 'animate-spin' : ''} />
            </button>
          )}
          <button
            onClick={handleRunBatch}
            disabled={runningBatch}
            className="flex-1 sm:flex-initial px-4 py-2.5 bg-gradient-to-r from-indigo-600 to-cyan-500 hover:from-indigo-500 hover:to-cyan-400 text-white rounded-xl font-bold text-xs sm:text-sm flex items-center justify-center gap-2 shadow-lg shadow-indigo-600/30 transition-all disabled:opacity-50"
          >
            <Play size={16} className={runningBatch ? 'animate-spin' : ''} />
            {runningBatch ? 'Evaluating All Engines...' : '🚀 Run Detection Batch to Ingest & Analyze'}
          </button>
        </div>
      </div>

      {message && (
        <div className="p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-emerald-300 text-xs sm:text-sm font-medium flex items-center gap-2 shadow-md">
          <Zap size={18} className="text-emerald-400 shrink-0" />
          <span>{message}</span>
        </div>
      )}

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">Monitored Accounts</p>
              <h3 className="text-xl sm:text-2xl font-bold text-slate-100 mt-1">
                {hasRunBatch ? (metrics?.totalCustomers ?? 6) : 0}
              </h3>
            </div>
            <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-xl">
              <Users size={22} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">At-Risk Accounts</p>
              <h3 className="text-xl sm:text-2xl font-bold text-rose-400 mt-1">
                {hasRunBatch ? (metrics?.atRiskCount ?? 3) : 0}
              </h3>
            </div>
            <div className="p-3 bg-rose-500/10 text-rose-400 rounded-xl">
              <AlertTriangle size={22} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">ARR Retained (YTD)</p>
              <h3 className="text-xl sm:text-2xl font-bold text-emerald-400 mt-1">
                R {hasRunBatch ? (metrics?.savedArr ?? 16920000).toLocaleString('en-ZA') : '0'}
              </h3>
            </div>
            <div className="p-3 bg-emerald-500/10 text-emerald-400 rounded-xl">
              <DollarSign size={22} />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">Active Camunda Workflows</p>
              <h3 className="text-xl sm:text-2xl font-bold text-brand-400 mt-1">
                {hasRunBatch ? (metrics?.activeWorkflows ?? 3) : 0}
              </h3>
            </div>
            <div className="p-3 bg-brand-500/10 text-brand-400 rounded-xl">
              <Activity size={22} />
            </div>
          </div>
        </Card>
      </div>

      {/* Active High Risk Table */}
      <Card title="High Risk Customers & Camunda Workflow Status">
        {!hasRunBatch ? (
          <div className="p-10 text-center border border-dashed border-slate-800 rounded-2xl bg-slate-900/30 space-y-3">
            <ShieldAlert size={40} className="mx-auto text-indigo-400 opacity-60" />
            <h3 className="text-base font-bold text-slate-200">System Ready: Awaiting Detection Batch Execution</h3>
            <p className="text-xs text-slate-400 max-w-md mx-auto">
              No pre-analyzed clients are displayed initially. Click <strong>"Run Detection Batch to Ingest & Analyze"</strong> above to evaluate customer frustration signals and trigger all retention engines live!
            </p>
            <button
              onClick={handleRunBatch}
              disabled={runningBatch}
              className="mt-2 px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs inline-flex items-center gap-2 shadow-lg shadow-indigo-600/30 transition-all"
            >
              <Play size={14} /> Run Detection Batch Now
            </button>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-300 min-w-[600px]">
              <thead className="text-xs uppercase bg-slate-900/60 text-slate-400 border-b border-dark-border">
                <tr>
                  <th className="px-4 py-3">Ext ID</th>
                  <th className="px-4 py-3">Customer</th>
                  <th className="px-4 py-3">ARR (ZAR)</th>
                  <th className="px-4 py-3">Health Score</th>
                  <th className="px-4 py-3">Churn Risk</th>
                  <th className="px-4 py-3">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-dark-border text-xs">
                {highRiskCustomers.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-800/30 transition-colors">
                    <td className="px-4 py-3 font-mono text-slate-400">{c.externalCustomerId}</td>
                    <td className="px-4 py-3 font-semibold text-slate-100">{c.name}</td>
                    <td className="px-4 py-3">R {c.arr.toLocaleString('en-ZA')}</td>
                    <td className="px-4 py-3 font-medium text-amber-400">{c.healthScore}/100</td>
                    <td className="px-4 py-3 font-semibold text-rose-400">{c.churnProbability}%</td>
                    <td className="px-4 py-3"><Badge status={c.status} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  );
};

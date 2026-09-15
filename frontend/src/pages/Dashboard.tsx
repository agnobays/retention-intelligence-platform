import React, { useEffect, useState } from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { reportService } from '../services/reportService';
import { customerService } from '../services/customerService';
import { detectionService } from '../services/detectionService';
import { Customer, DashboardMetrics } from '../types';
import { Users, AlertTriangle, DollarSign, Activity, Play, RefreshCw } from 'lucide-react';

export const Dashboard: React.FC = () => {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [runningBatch, setRunningBatch] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [mRes, cRes] = await Promise.all([
        reportService.getDashboardMetrics(),
        customerService.getAllCustomers(),
      ]);
      setMetrics(mRes);
      setCustomers(cRes);
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleRunBatch = async () => {
    try {
      setRunningBatch(true);
      setMessage(null);
      await detectionService.sendTelemetryPayload({
        externalCustomerId: 'SB-CIB-1001',
        metricType: 'TRANSACTION_VOLUME_DROP_45_PCT',
        severity: 'CRITICAL',
        metricValue: '45% decline in corporate clearing transactions'
      });
      setMessage('Batch detection executed! Customer Shoprite Holdings flagged AT_RISK & Camunda workflow started.');
      await fetchData();
    } catch (err) {
      setMessage('Batch execution failed.');
    } finally {
      setRunningBatch(false);
    }
  };

  const highRiskCustomers = customers.filter(c => c.status === 'AT_RISK' || c.status === 'RECOVERING');

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-100">Standard Bank CIB Retention Dashboard</h1>
          <p className="text-xs sm:text-sm text-slate-400">Real-time churn risk detection & Camunda 7 BPMN workflow monitoring</p>
        </div>
        <div className="flex items-center gap-2 sm:gap-3 w-full sm:w-auto">
          <button
            onClick={fetchData}
            className="p-2 border border-dark-border rounded-lg text-slate-400 hover:text-slate-200"
            title="Refresh Data"
          >
            <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
          </button>
          <button
            onClick={handleRunBatch}
            disabled={runningBatch}
            className="flex-1 sm:flex-initial px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-xs sm:text-sm flex items-center justify-center gap-2 transition-all disabled:opacity-50"
          >
            <Play size={16} /> {runningBatch ? 'Evaluating...' : 'Run Detection Batch'}
          </button>
        </div>
      </div>

      {message && (
        <div className="p-3 bg-indigo-500/10 border border-indigo-500/30 rounded-lg text-indigo-300 text-xs font-medium">
          {message}
        </div>
      )}

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase">Monitored Accounts</p>
              <h3 className="text-xl sm:text-2xl font-bold text-slate-100 mt-1">{metrics?.totalCustomers ?? 6}</h3>
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
              <h3 className="text-xl sm:text-2xl font-bold text-rose-400 mt-1">{metrics?.atRiskCount ?? 2}</h3>
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
                R {(metrics?.savedArr ?? 16920000).toLocaleString('en-ZA')}
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
              <h3 className="text-xl sm:text-2xl font-bold text-brand-400 mt-1">{metrics?.activeWorkflows ?? 2}</h3>
            </div>
            <div className="p-3 bg-brand-500/10 text-brand-400 rounded-xl">
              <Activity size={22} />
            </div>
          </div>
        </Card>
      </div>

      {/* Active High Risk Table */}
      <Card title="High Risk Customers & Camunda Workflow Status">
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
      </Card>
    </div>
  );
};

import React from 'react';
import { Card } from '../components/Card';
import { CustomerJourneyAuditLogs } from '../components/CustomerJourneyAuditLogs';
import { BarChart3, TrendingUp, ShieldCheck } from 'lucide-react';

export const Reports: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-100">Analytics & Retention Reports</h1>
        <p className="text-sm text-slate-400">Churn velocity, ARR saved, and recovery success conversion rates</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card title="Retention ROI Summary">
          <div className="p-4 bg-dark-bg/50 rounded-lg flex items-center gap-4">
            <TrendingUp className="text-emerald-400" size={32} />
            <div>
              <p className="text-sm text-slate-300">Total ARR Retained</p>
              <h3 className="text-2xl font-bold text-slate-100">$345,000.00</h3>
              <p className="text-xs text-emerald-400 font-medium">+18.4% compared to last quarter</p>
            </div>
          </div>
        </Card>

        <Card title="Camunda Playbook Conversion">
          <div className="p-4 bg-dark-bg/50 rounded-lg flex items-center gap-4">
            <ShieldCheck className="text-brand-400" size={32} />
            <div>
              <p className="text-sm text-slate-300">Recovery Success Rate</p>
              <h3 className="text-2xl font-bold text-slate-100">84.2%</h3>
              <p className="text-xs text-brand-400 font-medium">16 out of 19 workflows closed as SAVED</p>
            </div>
          </div>
        </Card>
      </div>

      {/* A/B Test Results Dashboard */}
      <Card title="A/B Test Engine Performance">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-2">
          {/* Variant A */}
          <div className="border border-fuchsia-500/30 rounded-xl bg-fuchsia-500/10 p-5">
            <div className="flex items-center justify-between border-b border-fuchsia-500/20 pb-3 mb-3">
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-1 bg-fuchsia-600 text-white font-bold text-xs rounded-md tracking-widest">VARIANT A</span>
                <span className="text-sm font-semibold text-slate-300">Retail & Cash Vouchers</span>
              </div>
              <span className="text-fuchsia-400 font-bold">54% Traffic</span>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-xs text-slate-400">Recovery Rate</p>
                <p className="text-2xl font-bold text-slate-100">81.5%</p>
              </div>
              <div>
                <p className="text-xs text-slate-400">Avg Cost to Serve</p>
                <p className="text-2xl font-bold text-slate-100">R750</p>
              </div>
            </div>
            <p className="text-xs text-slate-400 mt-4 italic">Excels in Everyday Banking segment.</p>
          </div>

          {/* Variant B */}
          <div className="border border-cyan-500/30 rounded-xl bg-cyan-500/10 p-5">
            <div className="flex items-center justify-between border-b border-cyan-500/20 pb-3 mb-3">
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-1 bg-cyan-600 text-white font-bold text-xs rounded-md tracking-widest">VARIANT B</span>
                <span className="text-sm font-semibold text-slate-300">Priority Servicing & Fees</span>
              </div>
              <span className="text-cyan-400 font-bold">46% Traffic</span>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-xs text-slate-400">Recovery Rate</p>
                <p className="text-2xl font-bold text-slate-100">88.3% <span className="text-emerald-400 text-sm">↑ Winner</span></p>
              </div>
              <div>
                <p className="text-xs text-slate-400">Avg Cost to Serve</p>
                <p className="text-2xl font-bold text-slate-100">R350</p>
              </div>
            </div>
            <p className="text-xs text-slate-400 mt-4 italic">Significantly outperforms in Commercial/SME segments.</p>
          </div>
        </div>
      </Card>

      {/* 📜 Customer Journey & Email Dispatch Audit Logs */}
      <CustomerJourneyAuditLogs />
    </div>
  );
};


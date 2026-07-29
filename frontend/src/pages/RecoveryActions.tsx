import React from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { CheckCircle2, XCircle } from 'lucide-react';

export const RecoveryActions: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Recovery Actions & Manager Approval Queue</h1>
          <p className="text-sm text-slate-400">Review and approve Camunda recovery playbooks</p>
        </div>
      </div>

      <Card title="Pending Manager Approvals">
        <div className="space-y-4">
          <div className="p-4 border border-indigo-500/30 rounded-xl bg-indigo-950/20 flex items-center justify-between">
            <div>
              <div className="flex items-center gap-2">
                <span className="font-semibold text-slate-100">Omni Data Inc</span>
                <Badge status="PENDING_APPROVAL" />
              </div>
              <p className="text-xs text-slate-300 mt-1">Recommended Action: Executive Outreach + 15% Renewal Discount</p>
              <p className="text-xs text-slate-500 font-mono mt-0.5">Workflow Instance: 2251799813685249</p>
            </div>
            <div className="flex items-center gap-2">
              <button className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded text-xs font-semibold flex items-center gap-1">
                <CheckCircle2 size={14} /> Approve
              </button>
              <button className="px-3 py-1.5 bg-rose-600 hover:bg-rose-500 text-white rounded text-xs font-semibold flex items-center gap-1">
                <XCircle size={14} /> Reject
              </button>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
};

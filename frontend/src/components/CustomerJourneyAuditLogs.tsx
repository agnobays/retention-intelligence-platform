import React, { useEffect, useState } from 'react';
import { Card } from './Card';
import { CustomerJourneyLogItem } from '../types';
import { workflowService } from '../services/workflowService';
import { FileText, Search, User, Mail, Clock, Eye, X, ShieldCheck, RefreshCw, Copy, Check } from 'lucide-react';

export const CustomerJourneyAuditLogs: React.FC = () => {
  const [logs, setLogs] = useState<CustomerJourneyLogItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedLog, setSelectedLog] = useState<CustomerJourneyLogItem | null>(null);
  const [copiedId, setCopiedId] = useState<string | null>(null);

  const fetchLogs = async () => {
    try {
      setLoading(true);
      const data = await workflowService.getAuditLogs();
      setLogs(data);
    } catch (err) {
      console.error('Failed to load customer journey audit logs:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  const handleCopySessionId = (sessionId: string) => {
    navigator.clipboard.writeText(sessionId);
    setCopiedId(sessionId);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const filteredLogs = logs.filter((logItem) => {
    const q = searchTerm.toLowerCase();
    return (
      logItem.sessionId?.toLowerCase().includes(q) ||
      logItem.externalCustomerId?.toLowerCase().includes(q) ||
      logItem.customerName?.toLowerCase().includes(q) ||
      logItem.approver?.toLowerCase().includes(q) ||
      logItem.recipientEmail?.toLowerCase().includes(q) ||
      logItem.subject?.toLowerCase().includes(q) ||
      logItem.messageContent?.toLowerCase().includes(q)
    );
  });

  return (
    <Card title="📜 Customer Journey & Email Dispatch Audit Logs">
      <div className="space-y-4">
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3 bg-slate-900/60 p-3.5 rounded-xl border border-slate-800">
          <div className="relative flex-1">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
            <input
              type="text"
              placeholder="Search by Session ID, Customer #, Approver, Recipient, or Message..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full bg-slate-950 border border-slate-700 rounded-lg pl-9 pr-3 py-2 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-indigo-500"
            />
          </div>
          <button
            onClick={fetchLogs}
            className="px-3 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-xs font-semibold flex items-center justify-center gap-1.5 transition-all"
          >
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
            Refresh Logs
          </button>
        </div>

        {/* Audit Logs Table */}
        <div className="overflow-x-auto rounded-xl border border-slate-800 bg-slate-950/40">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-900/80 text-slate-400 font-semibold uppercase text-[10px] tracking-wider border-b border-slate-800">
              <tr>
                <th className="py-3 px-4">Session ID</th>
                <th className="py-3 px-4">Customer / Company #</th>
                <th className="py-3 px-4">Customer Name</th>
                <th className="py-3 px-4">Event & Timestamp</th>
                <th className="py-3 px-4">Approved By</th>
                <th className="py-3 px-4">Recipient Inbox</th>
                <th className="py-3 px-4 text-right">Full Message</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {filteredLogs.map((item, idx) => (
                <tr key={idx} className="hover:bg-slate-900/50 transition-colors">
                  {/* Session ID */}
                  <td className="py-3 px-4 font-mono font-bold text-indigo-400">
                    <div className="flex items-center gap-1.5">
                      <span>{item.sessionId}</span>
                      <button
                        onClick={() => handleCopySessionId(item.sessionId)}
                        className="text-slate-500 hover:text-slate-300 transition-colors"
                        title="Copy Session ID"
                      >
                        {copiedId === item.sessionId ? <Check size={12} className="text-emerald-400" /> : <Copy size={12} />}
                      </button>
                    </div>
                  </td>

                  {/* External Customer ID */}
                  <td className="py-3 px-4">
                    <span className="px-2 py-0.5 rounded font-mono font-semibold bg-slate-800 text-slate-200 border border-slate-700">
                      {item.externalCustomerId}
                    </span>
                  </td>

                  {/* Customer Name */}
                  <td className="py-3 px-4 font-semibold text-slate-100">
                    {item.customerName}
                  </td>

                  {/* Event & Timestamp */}
                  <td className="py-3 px-4">
                    <div className="space-y-0.5">
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                        {item.eventType}
                      </span>
                      <p className="text-[10px] text-slate-400 flex items-center gap-1 mt-1">
                        <Clock size={10} className="text-slate-500" />
                        {new Date(item.timestamp).toLocaleString()}
                      </p>
                    </div>
                  </td>

                  {/* Approver */}
                  <td className="py-3 px-4">
                    <div className="flex items-center gap-1.5">
                      <User size={12} className="text-indigo-400 shrink-0" />
                      <span className="text-xs text-slate-200 font-medium">{item.approver}</span>
                    </div>
                  </td>

                  {/* Recipient Inbox */}
                  <td className="py-3 px-4">
                    <div className="flex items-center gap-1.5">
                      <Mail size={12} className="text-sky-400 shrink-0" />
                      <span className="text-xs font-mono text-sky-300">{item.recipientEmail}</span>
                    </div>
                  </td>

                  {/* Action */}
                  <td className="py-3 px-4 text-right">
                    <button
                      onClick={() => setSelectedLog(item)}
                      className="px-2.5 py-1.5 bg-indigo-600/20 hover:bg-indigo-600/40 text-indigo-300 border border-indigo-500/30 rounded-lg text-xs font-semibold inline-flex items-center gap-1 transition-all"
                    >
                      <Eye size={12} /> View Message
                    </button>
                  </td>
                </tr>
              ))}

              {filteredLogs.length === 0 && !loading && (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-slate-500">
                    No customer journey logs found matching your filter.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Full Message Details Modal */}
      {selectedLog && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4 animate-fade-in">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-2xl w-full overflow-hidden shadow-2xl space-y-4">
            {/* Modal Header */}
            <div className="p-4 bg-slate-950 border-b border-slate-800 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="p-2 bg-indigo-600/20 border border-indigo-500/30 rounded-lg text-indigo-400">
                  <FileText size={18} />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-slate-100 flex items-center gap-2">
                    Customer Journey Log Record
                    <span className="px-2 py-0.5 text-[10px] font-mono bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 rounded">
                      {selectedLog.sessionId}
                    </span>
                  </h3>
                  <p className="text-xs text-slate-400">
                    Client: <strong className="text-slate-200">{selectedLog.customerName}</strong> ({selectedLog.externalCustomerId})
                  </p>
                </div>
              </div>
              <button
                onClick={() => setSelectedLog(null)}
                className="p-1.5 text-slate-400 hover:text-slate-200 hover:bg-slate-800 rounded-lg transition-colors"
              >
                <X size={18} />
              </button>
            </div>

            {/* Modal Body */}
            <div className="p-5 space-y-4 text-xs text-slate-300 max-h-[70vh] overflow-y-auto">
              <div className="grid grid-cols-2 gap-3 bg-slate-950 p-3.5 rounded-xl border border-slate-800">
                <div>
                  <span className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider block">Sent Timestamp</span>
                  <span className="text-slate-200 font-mono">{new Date(selectedLog.timestamp).toLocaleString()}</span>
                </div>
                <div>
                  <span className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider block">Approved By</span>
                  <span className="text-indigo-400 font-semibold">{selectedLog.approver}</span>
                </div>
                <div>
                  <span className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider block">Recipient Inbox</span>
                  <span className="text-sky-300 font-mono">{selectedLog.recipientEmail}</span>
                </div>
                <div>
                  <span className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider block">Concession / Voucher Offer</span>
                  <span className="text-emerald-400 font-semibold">{selectedLog.concessionReward}</span>
                </div>
              </div>

              <div>
                <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block mb-1">Email Subject</span>
                <div className="p-3 bg-slate-950 border border-slate-800 rounded-lg font-semibold text-slate-100">
                  {selectedLog.subject}
                </div>
              </div>

              <div>
                <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block mb-1">Full Message Sent Content</span>
                <div className="p-4 bg-slate-950 border border-slate-800 rounded-xl font-mono text-xs text-slate-200 whitespace-pre-wrap leading-relaxed">
                  {selectedLog.messageContent}
                </div>
              </div>
            </div>

            {/* Modal Footer */}
            <div className="p-4 bg-slate-950 border-t border-slate-800 flex justify-end">
              <button
                onClick={() => setSelectedLog(null)}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-lg text-xs transition-all shadow-md"
              >
                Close Log
              </button>
            </div>
          </div>
        </div>
      )}
    </Card>
  );
};

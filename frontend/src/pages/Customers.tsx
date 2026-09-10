import React, { useEffect, useState } from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { customerService } from '../services/customerService';
import { detectionService } from '../services/detectionService';
import { Customer } from '../types';
import { Plus, Search, Filter, Play, X, Save } from 'lucide-react';

export const Customers: React.FC = () => {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [actionMsg, setActionMsg] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [importing, setImporting] = useState(false);

  const [formData, setFormData] = useState({
    externalCustomerId: '',
    name: '',
    email: '',
    mrr: 100000,
    arr: 1200000,
  });

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const res = await customerService.getAllCustomers();
      setCustomers(res);
    } catch (err) {
      console.error('Failed to fetch customers:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  const handleEvaluateCustomer = async (customerId: string, name: string) => {
    try {
      setActionMsg(null);
      await detectionService.runCustomerDetection(customerId);
      setActionMsg(`Evaluated churn risk for ${name}. Risk status updated & BPMN process evaluated.`);
      await fetchCustomers();
    } catch (err) {
      setActionMsg(`Evaluation failed for ${name}.`);
    }
  };

  const handleImportSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setImporting(true);
      setActionMsg(null);
      await customerService.importCustomer(formData);
      setActionMsg(`Successfully imported corporate client ${formData.name} to DB.`);
      setIsModalOpen(false);
      setFormData({
        externalCustomerId: '',
        name: '',
        email: '',
        mrr: 100000,
        arr: 1200000,
      });
      await fetchCustomers();
    } catch (err) {
      setActionMsg('Failed to import customer to database.');
    } finally {
      setImporting(false);
    }
  };

  const filteredCustomers = customers.filter(
    (c) =>
      c.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.externalCustomerId.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Standard Bank CIB Customer Accounts</h1>
          <p className="text-sm text-slate-400">Monitored corporate accounts, health scores, and telemetry status</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-sm flex items-center gap-2 shadow-lg shadow-brand-600/20"
        >
          <Plus size={16} /> Import Customer Telemetry
        </button>
      </div>

      {actionMsg && (
        <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-lg text-emerald-300 text-xs font-medium">
          {actionMsg}
        </div>
      )}

      {/* Import Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="w-full max-w-lg glass-card rounded-2xl p-6 border border-white/10 shadow-2xl">
            <div className="flex items-center justify-between mb-4 pb-3 border-b border-dark-border">
              <h3 className="text-lg font-bold text-slate-100">Import Corporate Customer to Database</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-slate-400 hover:text-slate-200">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleImportSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">External Customer ID</label>
                <input
                  type="text"
                  placeholder="e.g. SB-CIB-1007"
                  value={formData.externalCustomerId}
                  onChange={(e) => setFormData({ ...formData, externalCustomerId: e.target.value })}
                  className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Company / Customer Name</label>
                <input
                  type="text"
                  placeholder="e.g. Vodacom South Africa"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Corporate Email</label>
                <input
                  type="email"
                  placeholder="e.g. treasury@vodacom.co.za"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">MRR (ZAR)</label>
                  <input
                    type="number"
                    value={formData.mrr}
                    onChange={(e) => setFormData({ ...formData, mrr: Number(e.target.value) })}
                    className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">ARR (ZAR)</label>
                  <input
                    type="number"
                    value={formData.arr}
                    onChange={(e) => setFormData({ ...formData, arr: Number(e.target.value) })}
                    className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                    required
                  />
                </div>
              </div>

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-dark-border">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-dark-border rounded-lg text-slate-400 hover:text-slate-200 text-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={importing}
                  className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-semibold text-sm flex items-center gap-2 disabled:opacity-50"
                >
                  <Save size={16} /> {importing ? 'Saving to DB...' : 'Save to Database'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <Card>
        <div className="flex items-center gap-4 mb-4">
          <div className="relative flex-1">
            <Search size={16} className="absolute left-3 top-3 text-slate-500" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search by customer name, ID, or domain..."
              className="w-full bg-dark-bg border border-dark-border rounded-lg pl-9 pr-4 py-2 text-sm text-slate-200 focus:outline-none focus:border-brand-500"
            />
          </div>
          <button className="px-3 py-2 border border-dark-border rounded-lg text-slate-400 hover:text-slate-200 flex items-center gap-2 text-sm">
            <Filter size={16} /> Filter
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-300">
            <thead className="text-xs uppercase bg-slate-900/60 text-slate-400 border-b border-dark-border">
              <tr>
                <th className="px-4 py-3">Ext ID</th>
                <th className="px-4 py-3">Customer Name</th>
                <th className="px-4 py-3">ARR (ZAR)</th>
                <th className="px-4 py-3">Health Score</th>
                <th className="px-4 py-3">Churn Risk</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dark-border">
              {filteredCustomers.map((c) => (
                <tr key={c.id}>
                  <td className="px-4 py-3 font-mono text-xs text-slate-400">{c.externalCustomerId}</td>
                  <td className="px-4 py-3 font-semibold text-slate-100">{c.name}</td>
                  <td className="px-4 py-3">R {c.arr ? c.arr.toLocaleString('en-ZA') : '0'}</td>
                  <td className={`px-4 py-3 font-semibold ${c.healthScore < 50 ? 'text-rose-400' : 'text-emerald-400'}`}>
                    {c.healthScore}/100
                  </td>
                  <td className={`px-4 py-3 font-semibold ${c.churnProbability > 50 ? 'text-rose-400' : 'text-slate-300'}`}>
                    {c.churnProbability}%
                  </td>
                  <td className="px-4 py-3"><Badge status={c.status} /></td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() => handleEvaluateCustomer(c.id, c.name)}
                      className="px-2.5 py-1 bg-indigo-600/20 hover:bg-indigo-600/30 text-indigo-400 border border-indigo-500/30 rounded text-xs flex items-center gap-1 font-medium"
                    >
                      <Play size={12} /> Evaluate Risk
                    </button>
                  </td>
                </tr>
              ))}
              {filteredCustomers.length === 0 && !loading && (
                <tr>
                  <td colSpan={7} className="px-4 py-6 text-center text-slate-500">
                    No customers found matching search criteria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};

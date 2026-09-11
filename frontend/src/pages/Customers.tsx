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

  const [batchResult, setBatchResult] = useState<any | null>(null);
  const [batchProcessing, setBatchProcessing] = useState(false);

  const handleProcessCSVText = async (csvText: string) => {
    try {
      setBatchProcessing(true);
      setActionMsg(null);
      setBatchResult(null);

      const parsedRows = customerService.parseCSVText(csvText);
      if (parsedRows.length === 0) {
        setActionMsg('CSV file is empty or invalid format.');
        return;
      }

      const result = await customerService.importSpreadsheetBatch(parsedRows);
      setBatchResult(result);
      setActionMsg(`🎉 Batch Spreadsheet Analyzed! ${result.totalImported} accounts imported, ${result.workflowsLaunched} Camunda BPMN workflows launched.`);
      await fetchCustomers();
    } catch (err) {
      setActionMsg('Failed to process batch spreadsheet import.');
    } finally {
      setBatchProcessing(false);
    }
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (event) => {
      const content = event.target?.result as string;
      if (content) {
        handleProcessCSVText(content);
      }
    };
    reader.readAsText(file);
  };

  const handleLoadDemoCSV = () => {
    const demoCSV = `externalCustomerId,name,email,mrr,arr,healthScore,churnProbability,status
SB-CIB-2001,Woolworths South Africa Corporate,finance@woolworths.co.za,450000.00,5400000.00,38,84.50,AT_RISK
SB-CIB-2002,Nedbank Corporate & Investment,treasury@nedbank.co.za,620000.00,7440000.00,41,79.20,AT_RISK
SB-CIB-2003,FirstRand Group Treasury,payments@firstrand.co.za,510000.00,6120000.00,85,14.00,ACTIVE
SB-CIB-2004,Pick n Pay Enterprise Services,treasury@pnp.co.za,280000.00,3360000.00,34,88.10,AT_RISK
SB-CIB-2005,Discovery Health Corporate,corporate@discovery.co.za,390000.00,4680000.00,48,72.00,AT_RISK
SB-CIB-2006,Sanlam Life Insurance,finance@sanlam.co.za,410000.00,4920000.00,92,6.50,ACTIVE`;
    handleProcessCSVText(demoCSV);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Standard Bank CIB Customer Accounts</h1>
          <p className="text-sm text-slate-400">Monitored corporate accounts, health scores, and telemetry status</p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => setIsModalOpen(true)}
            className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-sm flex items-center gap-2 shadow-lg shadow-brand-600/20"
          >
            <Plus size={16} /> Add Single Account
          </button>
        </div>
      </div>

      {actionMsg && (
        <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-lg text-emerald-300 text-xs font-medium">
          {actionMsg}
        </div>
      )}

      {/* Spreadsheet Batch Import Control Panel */}
      <Card title="📄 Spreadsheet Batch Import & AI Camunda Workflow Trigger">
        <div className="p-4 bg-indigo-950/30 border border-indigo-500/30 rounded-xl space-y-4">
          <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
            <div>
              <h3 className="text-sm font-semibold text-slate-100">Import Corporate Accounts Spreadsheet (.CSV)</h3>
              <p className="text-xs text-slate-400">Upload corporate telemetry metrics. The system automatically analyzes churn risk, updates database records, and triggers corresponding Camunda 7 BPMN workflows.</p>
            </div>
            <div className="flex flex-wrap items-center gap-2">
              <button
                onClick={handleLoadDemoCSV}
                disabled={batchProcessing}
                className="px-3.5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all shadow-md disabled:opacity-50"
              >
                ⚡ Load Demo CIB Batch CSV
              </button>
              <label className="px-3.5 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 cursor-pointer transition-all shadow-md">
                📂 Choose CSV File...
                <input type="file" accept=".csv" onChange={handleFileUpload} className="hidden" />
              </label>
              <a
                href="/cib_corporate_accounts_batch.csv"
                download="cib_corporate_accounts_batch.csv"
                className="px-3 py-2 border border-slate-700 hover:bg-slate-800 text-slate-300 rounded-lg text-xs font-medium transition-all"
              >
                📥 Sample CSV Template
              </a>
            </div>
          </div>

          {batchProcessing && (
            <div className="p-3 bg-amber-500/10 border border-amber-500/30 rounded-lg text-amber-300 text-xs flex items-center gap-2">
              <div className="w-4 h-4 border-2 border-amber-400 border-t-transparent rounded-full animate-spin"></div>
              <span>Analyzing spreadsheet rows, calculating health scores, and launching Camunda 7 BPMN workflows...</span>
            </div>
          )}

          {batchResult && (
            <div className="p-4 bg-slate-900/80 border border-slate-800 rounded-xl space-y-3">
              <div className="flex items-center justify-between border-b border-slate-800 pb-2">
                <span className="text-xs font-bold text-emerald-400 uppercase tracking-wider">Spreadsheet AI Analysis Summary</span>
                <span className="text-xs text-slate-400">{batchResult.summaryMessage}</span>
              </div>

              <div className="grid grid-cols-3 gap-3 text-center">
                <div className="p-2 bg-slate-950 rounded-lg border border-slate-800">
                  <div className="text-xs text-slate-400">Total Accounts</div>
                  <div className="text-lg font-bold text-slate-100">{batchResult.totalImported}</div>
                </div>
                <div className="p-2 bg-slate-950 rounded-lg border border-slate-800">
                  <div className="text-xs text-slate-400">At Risk Flagged</div>
                  <div className="text-lg font-bold text-rose-400">{batchResult.atRiskCount}</div>
                </div>
                <div className="p-2 bg-slate-950 rounded-lg border border-slate-800">
                  <div className="text-xs text-slate-400">Camunda Workflows Launched</div>
                  <div className="text-lg font-bold text-indigo-400">{batchResult.workflowsLaunched}</div>
                </div>
              </div>

              <div className="max-h-48 overflow-y-auto rounded-lg border border-slate-800">
                <table className="w-full text-left text-xs text-slate-300">
                  <thead className="bg-slate-950 text-slate-400 sticky top-0">
                    <tr>
                      <th className="p-2">Ext ID</th>
                      <th className="p-2">Account Name</th>
                      <th className="p-2">Health</th>
                      <th className="p-2">Churn Risk</th>
                      <th className="p-2">Status</th>
                      <th className="p-2">Launched Camunda BPMN Workflow</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800 bg-slate-900">
                    {batchResult.accounts.map((acc: any, i: number) => (
                      <tr key={i}>
                        <td className="p-2 font-mono text-slate-400">{acc.externalCustomerId}</td>
                        <td className="p-2 font-semibold text-slate-100">{acc.customerName}</td>
                        <td className={`p-2 font-bold ${acc.healthScore < 50 ? 'text-rose-400' : 'text-emerald-400'}`}>{acc.healthScore}/100</td>
                        <td className={`p-2 font-bold ${acc.churnProbability > 50 ? 'text-rose-400' : 'text-slate-300'}`}>{acc.churnProbability}%</td>
                        <td className="p-2"><Badge status={acc.status} /></td>
                        <td className="p-2 font-medium text-indigo-300">{acc.launchedWorkflowKey} ({acc.workflowInstanceId})</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      </Card>

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

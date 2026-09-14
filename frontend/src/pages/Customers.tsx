import React, { useEffect, useState } from 'react';
import { Card } from '../components/Card';
import { Badge } from '../components/Badge';
import { customerService } from '../services/customerService';
import { detectionService } from '../services/detectionService';
import { Customer } from '../types';
import { 
  Plus, 
  Search, 
  Filter, 
  Play, 
  X, 
  Save, 
  Zap, 
  Gift, 
  AlertTriangle, 
  Sparkles, 
  Award, 
  Building2, 
  UserCheck, 
  CreditCard 
} from 'lucide-react';

export const Customers: React.FC = () => {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [segmentFilter, setSegmentFilter] = useState<string>('ALL');
  const [loading, setLoading] = useState(true);
  const [actionMsg, setActionMsg] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalTab, setModalTab] = useState<'csv' | 'single'>('csv');
  const [importing, setImporting] = useState(false);

  const [formData, setFormData] = useState({
    externalCustomerId: '',
    name: '',
    email: '',
    customerSegment: 'PRIVATE_CLIENT',
    mrr: 45000,
    arr: 540000,
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
      setActionMsg(`Evaluated Sanisa retention risk for ${name}. Risk status updated & Camunda BPMN workflow evaluated.`);
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
      setActionMsg(`Successfully imported client ${formData.name} to Sanisa Retention Intelligence Platform.`);
      setIsModalOpen(false);
      setFormData({
        externalCustomerId: '',
        name: '',
        email: '',
        customerSegment: 'PRIVATE_CLIENT',
        mrr: 45000,
        arr: 540000,
      });
      await fetchCustomers();
    } catch (err) {
      setActionMsg('Failed to import customer to database.');
    } finally {
      setImporting(false);
    }
  };

  const filteredCustomers = customers.filter((c) => {
    const matchesSearch =
      c.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.externalCustomerId.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (c.customerSegment && c.customerSegment.toLowerCase().includes(searchTerm.toLowerCase()));
    
    if (segmentFilter === 'ALL') return matchesSearch;
    return matchesSearch && c.customerSegment === segmentFilter;
  });

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
      setActionMsg(`🎉 Sanisa Retention Intelligence Batch Analyzed! ${result.totalImported} accounts imported, ${result.workflowsLaunched} Camunda BPMN workflows launched.`);
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
    const demoCSV = `externalCustomerId,name,email,customerSegment,tenure,productsHeld,mrr,arr,frustrationScore,healthScore,churnProbability,status,issueCategory
SB-PC-001,Dr. Anele Nkosi (Private Client),anele.nkosi@privateclient.co.za,PRIVATE_CLIENT,8 years,"Current Account, Investments, Credit Card, Home Loan",45000.00,540000.00,88,12,88.00,AT_RISK,Service Delay (Investment Request)
SB-CC-002,Apex Logistics Enterprise (SME),treasury@apexlogistics.co.za,COMMERCIAL_SME,4 years,"Business Account, Merchant Services, Business Lending",66666.67,800000.00,76,24,76.00,AT_RISK,Merchant Settlement / Payment Disruption
SB-EC-003,Thabo Khumalo (Everyday Banking),thabo.khumalo@gmail.com,EVERYDAY_BANKING,2 years,"Current Account + Debit Card",708.33,8500.00,62,38,62.00,AT_RISK,Card Transaction Dispute SLA
SB-CIB-2001,Woolworths South Africa Corporate,finance@woolworths.co.za,COMMERCIAL_SME,12 years,"Merchant Clearing, Corporate Credit",450000.00,5400000.00,82,38,84.50,AT_RISK,API Gateway Merchant Disruption
SB-CIB-2003,FirstRand Group Treasury,payments@firstrand.co.za,COMMERCIAL_SME,15 years,"Corporate Treasury",510000.00,6120000.00,15,85,14.00,ACTIVE,None`;
    handleProcessCSVText(demoCSV);
  };

  const getSegmentBadge = (segment?: string) => {
    switch (segment) {
      case 'PRIVATE_CLIENT':
        return <span className="px-2 py-0.5 text-[11px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30 rounded flex items-center gap-1"><Sparkles size={11} /> Private Client</span>;
      case 'COMMERCIAL_SME':
        return <span className="px-2 py-0.5 text-[11px] font-bold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 rounded flex items-center gap-1"><Building2 size={11} /> Commercial / SME</span>;
      case 'EVERYDAY_BANKING':
        return <span className="px-2 py-0.5 text-[11px] font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 rounded flex items-center gap-1"><CreditCard size={11} /> Everyday Banking</span>;
      default:
        return <span className="px-2 py-0.5 text-[11px] font-medium bg-slate-800 text-slate-300 rounded">CIB Account</span>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Sanisa Header Banner */}
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold text-slate-100">SANISA Retention Intelligence Platform™</h1>
            <span className="px-2.5 py-0.5 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-bold text-[10px] uppercase rounded-full tracking-wider shadow-sm">
              Pilot MVP
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-1 italic">
            "Loyalty begins after the complaint." — Detect. Understand. Intervene. Reward. Retain.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => { setModalTab('csv'); setIsModalOpen(true); }}
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg font-medium text-sm flex items-center gap-2 shadow-lg shadow-indigo-600/20 transition-all"
          >
            📄 Batch CSV Import & Trigger Camunda
          </button>
          <button
            onClick={() => { setModalTab('single'); setIsModalOpen(true); }}
            className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg font-medium text-sm flex items-center gap-2 shadow-lg shadow-brand-600/20 transition-all"
          >
            <Plus size={16} /> Add Single Account
          </button>
        </div>
      </div>

      {actionMsg && (
        <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-lg text-emerald-300 text-xs font-medium flex items-center gap-2">
          <Zap size={14} className="text-emerald-400" />
          <span>{actionMsg}</span>
        </div>
      )}

      {/* The Retention Intelligence Loop Banner */}
      <div className="p-4 bg-gradient-to-r from-slate-900 via-indigo-950/60 to-slate-900 border border-indigo-500/30 rounded-2xl shadow-xl space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Sparkles size={16} className="text-amber-400" />
            <span className="text-xs font-bold uppercase tracking-wider text-amber-300">
              The Retention Intelligence Loop
            </span>
          </div>
          <span className="text-[11px] text-slate-400 font-medium">Sanisa Engine Pipeline v2.4</span>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-5 gap-2 text-center text-[11px]">
          <div className="p-2 bg-slate-950/80 rounded-xl border border-slate-800">
            <div className="text-slate-400 font-semibold">1. Customer Frustration</div>
            <div className="text-slate-200 text-[10px] mt-0.5">Complaint & Interaction Data</div>
          </div>
          <div className="p-2 bg-slate-950/80 rounded-xl border border-slate-800">
            <div className="text-slate-400 font-semibold">2. Value & Risk Analysis</div>
            <div className="text-slate-200 text-[10px] mt-0.5">Frustration vs Value Score</div>
          </div>
          <div className="p-2 bg-slate-950/80 rounded-xl border border-slate-800">
            <div className="text-slate-400 font-semibold">3. Decision Engine</div>
            <div className="text-indigo-300 font-bold text-[10px] mt-0.5">Camunda BPMN Trigger</div>
          </div>
          <div className="p-2 bg-slate-950/80 rounded-xl border border-slate-800">
            <div className="text-slate-400 font-semibold">4. Intelligent Loyalty Reward</div>
            <div className="text-emerald-300 font-bold text-[10px] mt-0.5">Personalised Recovery</div>
          </div>
          <div className="p-2 bg-slate-950/80 rounded-xl border border-slate-800 col-span-2 md:col-span-1">
            <div className="text-slate-400 font-semibold">5. Retention Outcome</div>
            <div className="text-amber-300 font-bold text-[10px] mt-0.5">Feeds Back Into Intelligence</div>
          </div>
        </div>
      </div>

      {/* Spreadsheet Batch Import Control Panel */}
      <Card title="📄 Spreadsheet Batch Import & AI Camunda Workflow Trigger">
        <div className="p-4 bg-indigo-950/30 border border-indigo-500/30 rounded-xl space-y-4">
          <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
            <div>
              <h3 className="text-sm font-semibold text-slate-100">Import Customer Telemetry Spreadsheet (.CSV)</h3>
              <p className="text-xs text-slate-400">Upload customer frustration metrics. The system calculates frustration scores, retention risk, and triggers personalized loyalty recovery actions.</p>
            </div>
            <div className="flex flex-wrap items-center gap-2">
              <button
                onClick={handleLoadDemoCSV}
                disabled={batchProcessing}
                className="px-3.5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all shadow-md disabled:opacity-50"
              >
                ⚡ Load Sanisa Pilot Scenarios CSV
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
              <span>Analyzing frustration scores, evaluating customer value, and launching Camunda 7 BPMN workflows...</span>
            </div>
          )}

          {batchResult && (
            <div className="p-4 bg-slate-900/80 border border-slate-800 rounded-xl space-y-3">
              <div className="flex items-center justify-between border-b border-slate-800 pb-2">
                <span className="text-xs font-bold text-emerald-400 uppercase tracking-wider">Sanisa AI Analysis Summary</span>
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
                      <th className="p-2">Customer Name</th>
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
          <div className="w-full max-w-2xl glass-card rounded-2xl p-6 border border-white/10 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-dark-border pb-3">
              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setModalTab('csv')}
                  className={`px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                    modalTab === 'csv'
                      ? 'bg-indigo-600 text-white shadow-md'
                      : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                  }`}
                >
                  📄 Batch CSV Import & Camunda Trigger
                </button>
                <button
                  type="button"
                  onClick={() => setModalTab('single')}
                  className={`px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                    modalTab === 'single'
                      ? 'bg-indigo-600 text-white shadow-md'
                      : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                  }`}
                >
                  ➕ Single Account Entry
                </button>
              </div>
              <button onClick={() => setIsModalOpen(false)} className="text-slate-400 hover:text-slate-200">
                <X size={18} />
              </button>
            </div>

            {modalTab === 'csv' ? (
              <div className="space-y-4">
                <div className="p-4 bg-indigo-950/30 border border-indigo-500/30 rounded-xl space-y-3">
                  <h3 className="text-sm font-semibold text-slate-100">Upload Customer Telemetry Spreadsheet (.CSV)</h3>
                  <p className="text-xs text-slate-400">Upload CSV file to run Sanisa frustration intelligence, calculate risk metrics, and trigger Camunda 7 BPMN workflows.</p>

                  <div className="flex flex-wrap items-center gap-2 pt-2">
                    <button
                      type="button"
                      onClick={handleLoadDemoCSV}
                      disabled={batchProcessing}
                      className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all shadow-md disabled:opacity-50"
                    >
                      ⚡ Load Sanisa Pilot Scenarios CSV
                    </button>
                    <label className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 cursor-pointer transition-all shadow-md">
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
              </div>
            ) : (
              <form onSubmit={handleImportSubmit} className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">External Customer ID</label>
                  <input
                    type="text"
                    placeholder="e.g. SB-PC-001"
                    value={formData.externalCustomerId}
                    onChange={(e) => setFormData({ ...formData, externalCustomerId: e.target.value })}
                    className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Customer / Company Name</label>
                  <input
                    type="text"
                    placeholder="e.g. Dr. Anele Nkosi (Private Client)"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Customer Segment</label>
                  <select
                    value={formData.customerSegment}
                    onChange={(e) => setFormData({ ...formData, customerSegment: e.target.value })}
                    className="w-full bg-dark-bg border border-dark-border rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                  >
                    <option value="PRIVATE_CLIENT">Private Client (High Value)</option>
                    <option value="COMMERCIAL_SME">Commercial / SME Client</option>
                    <option value="EVERYDAY_BANKING">Everyday Banking Customer</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Email Address</label>
                  <input
                    type="email"
                    placeholder="e.g. anele.nkosi@privateclient.co.za"
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
                    <Save size={16} /> {importing ? 'Saving...' : 'Save to Database'}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}

      {/* Customer Accounts Table with Segment Tabs */}
      <Card>
        {/* Segment Filter Tabs */}
        <div className="flex flex-wrap items-center justify-between gap-4 border-b border-dark-border pb-4 mb-4">
          <div className="flex items-center gap-2">
            <button
              onClick={() => setSegmentFilter('ALL')}
              className={`px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                segmentFilter === 'ALL'
                  ? 'bg-brand-600 text-white shadow-md'
                  : 'bg-slate-800/60 text-slate-400 hover:text-slate-200'
              }`}
            >
              All Clients ({customers.length})
            </button>
            <button
              onClick={() => setSegmentFilter('PRIVATE_CLIENT')}
              className={`px-3.5 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all ${
                segmentFilter === 'PRIVATE_CLIENT'
                  ? 'bg-amber-600 text-white shadow-md'
                  : 'bg-slate-800/60 text-slate-400 hover:text-amber-300'
              }`}
            >
              <Sparkles size={12} /> Scenario 01: Private Client
            </button>
            <button
              onClick={() => setSegmentFilter('COMMERCIAL_SME')}
              className={`px-3.5 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all ${
                segmentFilter === 'COMMERCIAL_SME'
                  ? 'bg-indigo-600 text-white shadow-md'
                  : 'bg-slate-800/60 text-slate-400 hover:text-indigo-300'
              }`}
            >
              <Building2 size={12} /> Scenario 02: Commercial / SME
            </button>
            <button
              onClick={() => setSegmentFilter('EVERYDAY_BANKING')}
              className={`px-3.5 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all ${
                segmentFilter === 'EVERYDAY_BANKING'
                  ? 'bg-emerald-600 text-white shadow-md'
                  : 'bg-slate-800/60 text-slate-400 hover:text-emerald-300'
              }`}
            >
              <CreditCard size={12} /> Scenario 03: Everyday Banking
            </button>
          </div>

          <div className="relative flex-1 max-w-xs">
            <Search size={14} className="absolute left-3 top-2.5 text-slate-500" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search client or segment..."
              className="w-full bg-dark-bg border border-dark-border rounded-lg pl-8 pr-3 py-1.5 text-xs text-slate-200 focus:outline-none focus:border-brand-500"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-300">
            <thead className="text-xs uppercase bg-slate-900/60 text-slate-400 border-b border-dark-border">
              <tr>
                <th className="px-4 py-3">Ext ID</th>
                <th className="px-4 py-3">Customer Name</th>
                <th className="px-4 py-3">Segment</th>
                <th className="px-4 py-3">Frustration</th>
                <th className="px-4 py-3">Health Score</th>
                <th className="px-4 py-3">Retention Risk</th>
                <th className="px-4 py-3">Recommended Loyalty Reward</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dark-border text-xs">
              {filteredCustomers.map((c) => (
                <tr key={c.id} className="hover:bg-slate-800/30 transition-colors">
                  <td className="px-4 py-3 font-mono text-slate-400">{c.externalCustomerId}</td>
                  <td className="px-4 py-3">
                    <div className="font-semibold text-slate-100">{c.name}</div>
                    <div className="text-[10px] text-slate-500">{c.productsHeld || c.email}</div>
                  </td>
                  <td className="px-4 py-3">{getSegmentBadge(c.customerSegment)}</td>
                  <td className="px-4 py-3 font-bold text-rose-400">
                    {c.frustrationScore != null ? `${c.frustrationScore}/100` : '78/100'}
                  </td>
                  <td className={`px-4 py-3 font-bold ${c.healthScore < 50 ? 'text-rose-400' : 'text-emerald-400'}`}>
                    {c.healthScore}/100
                  </td>
                  <td className={`px-4 py-3 font-bold ${c.churnProbability > 50 ? 'text-rose-400' : 'text-slate-300'}`}>
                    {c.churnProbability}%
                  </td>
                  <td className="px-4 py-3 font-medium text-amber-300">
                    {c.rewardValue || 'Personalized Loyalty Voucher / Fee Credit'}
                  </td>
                  <td className="px-4 py-3"><Badge status={c.status} /></td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() => handleEvaluateCustomer(c.id, c.name)}
                      className="px-2.5 py-1 bg-indigo-600/20 hover:bg-indigo-600/30 text-indigo-400 border border-indigo-500/30 rounded text-xs flex items-center gap-1 font-medium transition-all"
                    >
                      <Play size={12} /> Evaluate Risk
                    </button>
                  </td>
                </tr>
              ))}
              {filteredCustomers.length === 0 && !loading && (
                <tr>
                  <td colSpan={9} className="px-4 py-6 text-center text-slate-500">
                    No clients found matching the selected segment or search filter.
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

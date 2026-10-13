import React, { useEffect, useState } from 'react';
import { Card } from '../components/Card';
import { UserPlus, Target, TrendingUp, CheckCircle, Zap } from 'lucide-react';
import { leadService, Lead } from '../services/leadService';

export const Acquisition: React.FC = () => {
  const [leads, setLeads] = useState<Lead[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionMsg, setActionMsg] = useState<string | null>(null);
  
  const [showModal, setShowModal] = useState(false);
  const [newLeadData, setNewLeadData] = useState<Partial<Lead>>({
    name: '',
    industrySegment: 'COMMERCIAL_SME',
    propensityToBuy: 50,
    estimatedArr: 0,
    status: 'IN TALKS'
  });

  const fetchLeads = async () => {
    try {
      setLoading(true);
      const res = await leadService.getAllLeads();
      setLeads(res);
    } catch (err) {
      console.error('Failed to fetch leads:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeads();
  }, []);

  const handleCreateLead = async () => {
    try {
      setActionMsg(null);
      const newLead = await leadService.createLead(newLeadData);
      setActionMsg(`Successfully acquired new prospect: ${newLead.name}`);
      setTimeout(() => setActionMsg(null), 3000);
      setShowModal(false);
      setNewLeadData({ name: '', industrySegment: 'COMMERCIAL_SME', propensityToBuy: 50, estimatedArr: 0, status: 'IN TALKS' });
      await fetchLeads();
    } catch (err) {
      setActionMsg('Failed to create lead. Please fill all required fields.');
      setTimeout(() => setActionMsg(null), 3000);
    }
  };

  const handleConvertLead = async (id: string, name: string) => {
    try {
      setActionMsg(null);
      await leadService.convertLead(id);
      setActionMsg(`Lead ${name} successfully converted to an active Customer and deployed to Retention Engine.`);
      setTimeout(() => setActionMsg(null), 4000);
      await fetchLeads();
    } catch (err) {
      setActionMsg(`Failed to convert lead ${name}.`);
      setTimeout(() => setActionMsg(null), 4000);
    }
  };

  const safeLeads = Array.isArray(leads) ? leads : [];
  const activeLeads = safeLeads.filter(l => l && l.status !== 'CONVERTED');
  const convertedLeads = safeLeads.filter(l => l && l.status === 'CONVERTED');
  
  const conversionRate = leads.length > 0 
    ? ((convertedLeads.length / leads.length) * 100).toFixed(1)
    : '0.0';

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">AI Acquisition Pipeline</h1>
          <p className="text-sm text-slate-400">Identify, target, and convert high-value prospects seamlessly</p>
        </div>
        <button 
          onClick={() => setShowModal(true)}
          className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg font-medium text-sm flex items-center gap-2 shadow-lg shadow-emerald-600/20 transition-all"
        >
          <UserPlus size={16} /> Generate New Lead
        </button>
      </div>

      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-700 rounded-xl w-full max-w-md shadow-2xl p-6">
            <h2 className="text-xl font-bold text-slate-100 mb-4">Create New Lead</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">Lead Name</label>
                <input 
                  type="text" 
                  value={newLeadData.name} 
                  onChange={e => setNewLeadData({...newLeadData, name: e.target.value})}
                  className="w-full bg-slate-800 border border-slate-700 rounded px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500" 
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">Industry Segment</label>
                <select 
                  value={newLeadData.industrySegment} 
                  onChange={e => setNewLeadData({...newLeadData, industrySegment: e.target.value})}
                  className="w-full bg-slate-800 border border-slate-700 rounded px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
                >
                  <option value="EVERYDAY_BANKING">Everyday Banking</option>
                  <option value="PRIVATE_CLIENT">Private Client</option>
                  <option value="COMMERCIAL_SME">Commercial & SME</option>
                  <option value="CORPORATE_CIB">Corporate CIB</option>
                </select>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Propensity to Buy (%)</label>
                  <input 
                    type="number" min="0" max="100"
                    value={newLeadData.propensityToBuy} 
                    onChange={e => setNewLeadData({...newLeadData, propensityToBuy: parseInt(e.target.value)})}
                    className="w-full bg-slate-800 border border-slate-700 rounded px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500" 
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Estimated ARR</label>
                  <input 
                    type="number" min="0"
                    value={newLeadData.estimatedArr} 
                    onChange={e => setNewLeadData({...newLeadData, estimatedArr: parseFloat(e.target.value)})}
                    className="w-full bg-slate-800 border border-slate-700 rounded px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-brand-500" 
                  />
                </div>
              </div>
            </div>
            <div className="mt-6 flex justify-end gap-3">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-slate-300 hover:text-white">Cancel</button>
              <button onClick={handleCreateLead} className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded text-sm font-medium">Create Lead</button>
            </div>
          </div>
        </div>
      )}

      {actionMsg && (
        <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-lg text-emerald-300 text-xs font-medium flex items-center gap-2">
          <Zap size={14} className="text-emerald-400" />
          <span>{actionMsg}</span>
        </div>
      )}

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <div className="flex items-center gap-4">
            <div className="p-3 bg-blue-500/20 rounded-lg">
              <Target className="text-blue-400" size={24} />
            </div>
            <div>
              <p className="text-sm text-slate-400">Identified Prospects</p>
              <h3 className="text-2xl font-bold text-slate-100">{leads.length + 1245}</h3>
            </div>
          </div>
        </Card>
        
        <Card>
          <div className="flex items-center gap-4">
            <div className="p-3 bg-amber-500/20 rounded-lg">
              <TrendingUp className="text-amber-400" size={24} />
            </div>
            <div>
              <p className="text-sm text-slate-400">Conversion Rate</p>
              <h3 className="text-2xl font-bold text-slate-100">{leads.length > 0 ? conversionRate : '18.4'}%</h3>
            </div>
          </div>
        </Card>
        
        <Card>
          <div className="flex items-center gap-4">
            <div className="p-3 bg-emerald-500/20 rounded-lg">
              <CheckCircle className="text-emerald-400" size={24} />
            </div>
            <div>
              <p className="text-sm text-slate-400">Acquired Customers</p>
              <h3 className="text-2xl font-bold text-slate-100">{convertedLeads.length + 229}</h3>
            </div>
          </div>
        </Card>
      </div>

      {/* Leads Table */}
      <Card title="Active High-Value Leads">
        <div className="overflow-x-auto mt-4">
          <table className="w-full text-left text-sm text-slate-300">
            <thead className="text-xs uppercase bg-slate-900/60 text-slate-400 border-b border-dark-border">
              <tr>
                <th className="px-4 py-3">Lead Name</th>
                <th className="px-4 py-3">Industry Segment</th>
                <th className="px-4 py-3">Propensity to Buy</th>
                <th className="px-4 py-3">Est. ARR</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dark-border text-xs">
              {activeLeads.map((lead) => (
                <tr key={lead.id} className="hover:bg-slate-800/30 transition-colors">
                  <td className="px-4 py-3 font-semibold text-slate-100">{lead.name}</td>
                  <td className="px-4 py-3">{lead.industrySegment}</td>
                  <td className="px-4 py-3 font-bold text-emerald-400">{lead.propensityToBuy}%</td>
                  <td className="px-4 py-3 font-mono text-slate-300">R{(lead.estimatedArr / 1000).toFixed(0)}k</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      lead.status === 'WARM LEAD' ? 'bg-amber-500/20 text-amber-300' : 'bg-blue-500/20 text-blue-300'
                    }`}>
                      {lead.status}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <button 
                      onClick={() => handleConvertLead(lead.id, lead.name)}
                      className="px-3 py-1 bg-brand-600 hover:bg-brand-500 text-white rounded text-xs transition-all"
                    >
                      Convert
                    </button>
                  </td>
                </tr>
              ))}
              {activeLeads.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-4 py-6 text-center text-slate-500">
                    No active leads found. Click 'Generate New Lead' to prospect.
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

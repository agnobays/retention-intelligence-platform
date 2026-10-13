import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { authService } from '../services/authService';
import { ArrowRight, Building2, UserPlus, Sparkles } from 'lucide-react';

export const Login: React.FC = () => {
  const [email, setEmail] = useState('admin@standardbank.co.za');
  const [password, setPassword] = useState('Password123!');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = await authService.login(email, password);
      login(
        response.token,
        response.email,
        response.role,
        response.companyId,
        response.companyName,
        response.industry
      );
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Authentication failed. Please check credentials.');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectDemo = (demoEmail: string) => {
    setEmail(demoEmail);
    setPassword('Password123!');
  };

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-slate-900 rounded-2xl p-8 border border-slate-800 shadow-2xl space-y-6">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-gradient-to-tr from-indigo-600 to-cyan-500 text-white rounded-xl shadow-lg shadow-indigo-600/30">
            <Building2 size={28} />
          </div>
          <div>
            <h1 className="text-xl font-bold text-slate-100">Retention Intelligence Platform</h1>
            <p className="text-xs text-slate-400">Multi-Tenant Isolated Enterprise Access</p>
          </div>
        </div>

        {error && (
          <div className="p-3 bg-rose-500/10 border border-rose-500/30 rounded-lg text-rose-400 text-xs font-medium">
            {error}
          </div>
        )}

        {/* Demo Fast Selection Bar */}
        <div className="p-3 bg-slate-950 rounded-xl border border-slate-800/80 space-y-2">
          <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Select Demo Business Login:</p>
          <div className="grid grid-cols-2 gap-2">
            <button
              type="button"
              onClick={() => handleSelectDemo('admin@standardbank.co.za')}
              className={`p-2 rounded-lg text-left text-xs transition-all border ${
                email.includes('standard')
                  ? 'border-indigo-500/50 bg-indigo-950/30 text-indigo-200'
                  : 'border-slate-800 bg-slate-900 text-slate-400 hover:text-slate-200'
              }`}
            >
              <div className="font-bold flex items-center gap-1">🏦 Standard Bank</div>
              <div className="text-[10px] text-slate-500 truncate">Corporate CIB</div>
            </button>
            <button
              type="button"
              onClick={() => handleSelectDemo('manager@kasiexpress.co.za')}
              className={`p-2 rounded-lg text-left text-xs transition-all border ${
                email.includes('kasi')
                  ? 'border-amber-500/50 bg-amber-950/30 text-amber-200'
                  : 'border-slate-800 bg-slate-900 text-slate-400 hover:text-slate-200'
              }`}
            >
              <div className="font-bold flex items-center gap-1">🚚 Kasi Logistics</div>
              <div className="text-[10px] text-slate-500 truncate">Cold Chain Local Biz</div>
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Corporate Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-slate-950 border border-slate-700 rounded-lg px-4 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-slate-950 border border-slate-700 rounded-lg px-4 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-gradient-to-r from-indigo-600 to-cyan-500 hover:from-indigo-500 hover:to-cyan-400 text-white font-semibold py-2.5 rounded-lg flex items-center justify-center gap-2 transition-all shadow-lg shadow-indigo-600/25 disabled:opacity-50 text-sm"
          >
            {loading ? 'Authenticating...' : 'Sign In to Tenant Portal'} <ArrowRight size={16} />
          </button>
        </form>

        {/* Link to Onboarding Wizard */}
        <div className="pt-4 border-t border-slate-800 text-center">
          <p className="text-xs text-slate-400 mb-2">Need an isolated retention engine for your business?</p>
          <button
            type="button"
            onClick={() => navigate('/onboarding')}
            className="w-full px-4 py-2 bg-slate-800 hover:bg-slate-700 text-indigo-300 hover:text-indigo-200 border border-indigo-500/30 rounded-xl text-xs font-semibold flex items-center justify-center gap-2 transition-all"
          >
            <Sparkles size={14} className="text-indigo-400" />
            <span>Onboard New Business (Create Isolated DB)</span>
          </button>
        </div>
      </div>
    </div>
  );
};

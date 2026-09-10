import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { authService } from '../services/authService';
import { ShieldCheck, ArrowRight, Building2 } from 'lucide-react';

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
      login(response.token, response.email, response.role);
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Authentication failed. Please check credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-dark-bg flex items-center justify-center p-4">
      <div className="w-full max-w-md glass-card rounded-2xl p-8 border border-white/10 shadow-2xl">
        <div className="flex items-center gap-3 mb-6">
          <div className="p-3 bg-brand-600/20 text-brand-500 rounded-xl border border-brand-500/30">
            <Building2 size={28} />
          </div>
          <div>
            <h1 className="text-xl font-bold text-slate-100">Standard Bank CIB</h1>
            <p className="text-xs text-slate-400">Retention Intelligence Platform</p>
          </div>
        </div>

        {error && (
          <div className="mb-4 p-3 bg-rose-500/10 border border-rose-500/30 rounded-lg text-rose-400 text-xs font-medium">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Corporate Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-dark-bg/80 border border-slate-700 rounded-lg px-4 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-dark-bg/80 border border-slate-700 rounded-lg px-4 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-brand-500"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-brand-600 hover:bg-brand-500 text-white font-semibold py-2.5 rounded-lg flex items-center justify-center gap-2 transition-all shadow-lg shadow-brand-500/25 disabled:opacity-50"
          >
            {loading ? 'Authenticating...' : 'Sign In to Standard Bank Portal'} <ArrowRight size={16} />
          </button>
        </form>

        <div className="mt-6 pt-4 border-t border-slate-800 text-center space-y-1">
          <p className="text-xs text-slate-400 font-semibold">Demo Accounts (Standard Bank CIB):</p>
          <p className="text-xs text-slate-500 font-mono">admin@standardbank.co.za / Password123!</p>
          <p className="text-xs text-slate-500 font-mono">manager@standardbank.co.za / Password123!</p>
        </div>
      </div>
    </div>
  );
};

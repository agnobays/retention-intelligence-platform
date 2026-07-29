import React from 'react';
import { Bell, ShieldCheck, UserCheck } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

export const Navbar: React.FC = () => {
  const { userEmail, userRole } = useAuth();

  return (
    <header className="h-16 border-b border-dark-border bg-dark-card/50 backdrop-blur-md px-6 flex items-center justify-between sticky top-0 z-30">
      <div className="flex items-center gap-3">
        <span className="text-xl font-bold bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">
          Retention Intelligence
        </span>
        <span className="px-2 py-0.5 text-xs font-semibold bg-indigo-500/20 text-indigo-300 rounded border border-indigo-500/30">
          Camunda 8 Enabled
        </span>
      </div>

      <div className="flex items-center gap-4">
        <button className="p-2 text-slate-400 hover:text-slate-200 hover:bg-slate-800/60 rounded-lg transition-colors relative">
          <Bell size={18} />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-indigo-500 rounded-full"></span>
        </button>

        <div className="h-5 w-[1px] bg-slate-800"></div>

        <div className="flex items-center gap-2 text-sm text-slate-300">
          <div className="w-8 h-8 rounded-full bg-indigo-600/30 border border-indigo-500/40 flex items-center justify-center font-bold text-indigo-400">
            {userEmail ? userEmail.substring(0, 2).toUpperCase() : 'US'}
          </div>
          <div>
            <div className="font-medium">{userEmail || 'admin@retention.io'}</div>
            <div className="text-xs text-indigo-400 flex items-center gap-1">
              <ShieldCheck size={12} /> {userRole}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

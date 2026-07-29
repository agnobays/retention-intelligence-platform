import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Users, 
  Building2, 
  Activity, 
  PlayCircle, 
  BarChart3, 
  Settings,
  GitBranch
} from 'lucide-react';

const navItems = [
  { path: '/', label: 'Executive Dashboard', icon: LayoutDashboard },
  { path: '/customers', label: 'Customers', icon: Users },
  { path: '/companies', label: 'Companies', icon: Building2 },
  { path: '/detection', label: 'Detection Rules', icon: Activity },
  { path: '/recovery', label: 'Recovery Actions', icon: PlayCircle },
  { path: '/reports', label: 'Analytics Reports', icon: BarChart3 },
  { path: '/settings', label: 'System Settings', icon: Settings },
];

export const Sidebar: React.FC = () => {
  return (
    <aside className="w-64 border-r border-dark-border bg-dark-card/30 flex flex-col justify-between py-4 px-3 min-h-[calc(100vh-4rem)]">
      <nav className="space-y-1">
        <div className="px-3 py-2 text-xs font-semibold uppercase tracking-wider text-slate-500">
          Core Modules
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-brand-600 text-white shadow-lg shadow-brand-500/20'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
                }`
              }
            >
              <Icon size={18} />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>

      <div className="p-3 bg-indigo-950/40 border border-indigo-500/20 rounded-lg">
        <div className="flex items-center gap-2 text-xs text-indigo-300 font-semibold mb-1">
          <GitBranch size={14} /> Workflow Engine
        </div>
        <p className="text-[11px] text-slate-400">
          CustomerRecoveryProcess.bpmn active on Camunda 8 Zeebe cluster.
        </p>
      </div>
    </aside>
  );
};

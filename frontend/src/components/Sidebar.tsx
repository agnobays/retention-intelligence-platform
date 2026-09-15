import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Users, 
  Building2, 
  Activity, 
  PlayCircle, 
  BarChart3, 
  Settings,
  GitBranch,
  LogOut,
  X
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

const navItems = [
  { path: '/', label: 'Executive Dashboard', icon: LayoutDashboard },
  { path: '/customers', label: 'Customers', icon: Users },
  { path: '/companies', label: 'Companies', icon: Building2 },
  { path: '/detection', label: 'Detection Rules', icon: Activity },
  { path: '/recovery', label: 'Recovery Actions', icon: PlayCircle },
  { path: '/reports', label: 'Analytics Reports', icon: BarChart3 },
  { path: '/settings', label: 'System Settings', icon: Settings },
];

interface SidebarProps {
  mobileOpen?: boolean;
  onCloseMobile?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ mobileOpen = false, onCloseMobile }) => {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    if (onCloseMobile) onCloseMobile();
    navigate('/login');
  };

  const content = (
    <div className="flex flex-col justify-between h-full py-4 px-3">
      <nav className="space-y-1">
        <div className="flex items-center justify-between px-3 py-2">
          <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
            Core Modules
          </span>
          {onCloseMobile && (
            <button
              onClick={onCloseMobile}
              className="md:hidden text-slate-400 hover:text-white p-1"
            >
              <X size={18} />
            </button>
          )}
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              onClick={() => {
                if (onCloseMobile) onCloseMobile();
              }}
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

      <div className="space-y-3">
        <div className="p-3 bg-indigo-950/40 border border-indigo-500/20 rounded-lg">
          <div className="flex items-center gap-2 text-xs text-indigo-300 font-semibold mb-1">
            <GitBranch size={14} /> Workflow Engine
          </div>
          <p className="text-[11px] text-slate-400">
            CustomerRecoveryProcess.bpmn active on Camunda 7 engine.
          </p>
        </div>

        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center gap-2 px-3 py-2 bg-rose-600/10 hover:bg-rose-600/20 border border-rose-500/20 text-rose-400 rounded-lg text-xs font-semibold transition-all"
        >
          <LogOut size={16} /> Sign Out / Logout
        </button>
      </div>
    </div>
  );

  return (
    <>
      {/* Desktop Fixed Sidebar */}
      <aside className="hidden md:flex w-64 border-r border-dark-border bg-dark-card/30 flex-col min-h-[calc(100vh-4rem)] shrink-0">
        {content}
      </aside>

      {/* Mobile Backdrop & Drawer Overlay */}
      {mobileOpen && (
        <div className="fixed inset-0 z-50 md:hidden flex">
          {/* Backdrop blur */}
          <div
            className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm transition-opacity"
            onClick={onCloseMobile}
          />
          {/* Drawer Container */}
          <aside className="relative w-72 max-w-[80vw] bg-slate-900 border-r border-slate-800 shadow-2xl z-50 flex flex-col h-full">
            {content}
          </aside>
        </div>
      )}
    </>
  );
};

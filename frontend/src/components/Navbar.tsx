import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Bell, 
  ShieldCheck, 
  Building2, 
  LogOut, 
  CheckCheck, 
  Trash2, 
  X, 
  AlertTriangle, 
  PlayCircle, 
  MailCheck, 
  CheckCircle2,
  Radio
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { useRealtimeNotifications, NotificationItem } from '../hooks/useRealtimeNotifications';

export const Navbar: React.FC = () => {
  const { userEmail, userRole, logout } = useAuth();
  const navigate = useNavigate();

  const {
    notifications,
    unreadCount,
    isConnected,
    markAllRead,
    clearAll,
    markSingleRead,
  } = useRealtimeNotifications();

  const [isNotifOpen, setIsNotifOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getNotifIcon = (type: NotificationItem['type']) => {
    switch (type) {
      case 'alert':
        return <AlertTriangle size={16} className="text-rose-400" />;
      case 'workflow':
        return <PlayCircle size={16} className="text-indigo-400" />;
      case 'email':
        return <MailCheck size={16} className="text-emerald-400" />;
      case 'success':
        return <CheckCircle2 size={16} className="text-blue-400" />;
      default:
        return <Bell size={16} className="text-indigo-400" />;
    }
  };

  return (
    <header className="bg-slate-900/80 backdrop-blur-md border-b border-slate-800/80 sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          
          {/* Logo & Brand Identity */}
          <div className="flex items-center gap-3 cursor-pointer" onClick={() => navigate('/dashboard')}>
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-cyan-400 flex items-center justify-center shadow-lg shadow-indigo-500/20 ring-1 ring-white/20">
              <Building2 className="w-5 h-5 text-white" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-bold text-slate-100 text-base tracking-tight">Standard Bank</span>
                <span className="px-2 py-0.5 text-[10px] font-semibold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 rounded-md">
                  CIB Retention Intelligence
                </span>
              </div>
              <p className="text-[11px] text-slate-400">Sanisa Platform™ • Executive Desk</p>
            </div>
          </div>

          {/* User Controls & Quick Tools */}
          <div className="flex items-center gap-4">

            {/* Live Realtime SSE Connection Indicator */}
            <div className="hidden md:flex items-center gap-1.5 px-2.5 py-1 bg-slate-950/60 border border-slate-800/80 rounded-full text-[11px]">
              <span className={`w-2 h-2 rounded-full ${isConnected ? 'bg-emerald-400 animate-pulse' : 'bg-amber-400'}`}></span>
              <span className="text-slate-300 font-medium">{isConnected ? 'SSE Live Stream' : 'Connecting Stream...'}</span>
            </div>
            
            {/* Notification Bell Icon */}
            <div className="relative">
              <button
                onClick={() => setIsNotifOpen(!isNotifOpen)}
                className="relative p-2 rounded-xl text-slate-300 hover:text-white hover:bg-slate-800/80 transition-all border border-transparent hover:border-slate-700/50"
                title="Notifications"
              >
                <Bell size={18} />
                {unreadCount > 0 && (
                  <span className="absolute top-1 right-1 px-1.5 py-0.2 text-[10px] font-bold bg-indigo-500 text-white rounded-full min-w-4 text-center shadow-md animate-pulse">
                    {unreadCount}
                  </span>
                )}
              </button>

              {/* Notifications Panel */}
              {isNotifOpen && (
                <div className="absolute right-0 mt-3 w-80 md:w-96 bg-slate-900 border border-slate-800 rounded-xl shadow-2xl z-50 overflow-hidden space-y-0">
                  <div className="p-3.5 bg-slate-950 border-b border-slate-800 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <Radio size={14} className="text-emerald-400 animate-pulse" />
                      <span className="text-xs font-bold text-slate-100 uppercase tracking-wider">
                        Live Notifications ({unreadCount} unread)
                      </span>
                    </div>
                    <div className="flex items-center gap-2">
                      {unreadCount > 0 && (
                        <button
                          onClick={markAllRead}
                          className="text-[11px] font-medium text-indigo-400 hover:text-indigo-300 flex items-center gap-1"
                          title="Mark all as read"
                        >
                          <CheckCheck size={12} /> Read All
                        </button>
                      )}
                      {notifications.length > 0 && (
                        <button
                          onClick={clearAll}
                          className="text-[11px] font-medium text-slate-400 hover:text-rose-400 flex items-center gap-1"
                          title="Clear notifications"
                        >
                          <Trash2 size={12} /> Clear
                        </button>
                      )}
                      <button
                        onClick={() => setIsNotifOpen(false)}
                        className="text-slate-400 hover:text-slate-200"
                      >
                        <X size={16} />
                      </button>
                    </div>
                  </div>

                  <div className="max-h-80 overflow-y-auto divide-y divide-slate-800/60 bg-slate-900">
                    {notifications.length === 0 ? (
                      <div className="p-6 text-center text-xs text-slate-400">
                        No active notifications. Live stream listening...
                      </div>
                    ) : (
                      notifications.map((item) => (
                        <div
                          key={item.id}
                          onClick={() => markSingleRead(item.id)}
                          className={`p-3.5 transition-colors cursor-pointer flex gap-3 ${
                            item.read ? 'bg-slate-900/40 hover:bg-slate-800/50 opacity-70' : 'bg-slate-850 hover:bg-slate-800/90'
                          }`}
                        >
                          <div className="mt-0.5 shrink-0">{getNotifIcon(item.type)}</div>
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center justify-between gap-2">
                              <h4 className={`text-xs font-semibold truncate ${item.read ? 'text-slate-300' : 'text-slate-100'}`}>
                                {item.title}
                              </h4>
                              <span className="text-[10px] text-slate-500 whitespace-nowrap">{item.time}</span>
                            </div>
                            <p className="text-xs text-slate-400 mt-1 leading-relaxed line-clamp-2">
                              {item.message}
                            </p>
                          </div>
                          {!item.read && (
                            <div className="w-2 h-2 rounded-full bg-indigo-500 self-center shrink-0"></div>
                          )}
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>

            {/* User Profile & Auth Menu */}
            <div className="relative">
              <button
                onClick={() => setIsProfileOpen(!isProfileOpen)}
                className="flex items-center gap-2.5 p-1.5 rounded-xl hover:bg-slate-800/60 transition-all border border-transparent hover:border-slate-700/50"
              >
                <div className="w-8 h-8 rounded-lg bg-indigo-500/20 border border-indigo-500/40 flex items-center justify-center text-indigo-300 font-bold text-xs">
                  {userEmail ? userEmail[0].toUpperCase() : 'S'}
                </div>
                <div className="hidden sm:block text-left">
                  <p className="text-xs font-semibold text-slate-200 truncate max-w-[120px]">
                    {userEmail || 'sipho.dlamini@standardbank.co.za'}
                  </p>
                  <p className="text-[10px] text-indigo-400 font-medium flex items-center gap-1">
                    <ShieldCheck size={10} /> {userRole || 'Executive Admin'}
                  </p>
                </div>
              </button>

              {/* Profile Dropdown */}
              {isProfileOpen && (
                <div className="absolute right-0 mt-3 w-56 bg-slate-900 border border-slate-800 rounded-xl shadow-2xl z-50 p-2 space-y-1">
                  <div className="px-3 py-2 border-b border-slate-800/80 mb-1">
                    <p className="text-xs font-medium text-slate-400">Signed in as</p>
                    <p className="text-xs font-bold text-slate-200 truncate">{userEmail}</p>
                    <span className="inline-block mt-1 px-2 py-0.5 text-[10px] font-semibold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 rounded">
                      Standard Bank CIB
                    </span>
                  </div>

                  <button
                    onClick={handleLogout}
                    className="w-full text-left px-3 py-2 text-xs font-semibold text-rose-400 hover:bg-rose-500/10 rounded-lg flex items-center gap-2 transition-colors"
                  >
                    <LogOut size={14} /> Sign Out
                  </button>
                </div>
              )}
            </div>

          </div>

        </div>
      </div>
    </header>
  );
};

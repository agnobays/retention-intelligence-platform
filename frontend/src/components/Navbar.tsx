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
  CheckCircle2 
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

interface NotificationItem {
  id: string;
  title: string;
  message: string;
  time: string;
  type: 'alert' | 'workflow' | 'email' | 'success';
  read: boolean;
}

const initialNotifications: NotificationItem[] = [
  {
    id: 'n1',
    title: 'High Churn Risk Detected',
    message: 'Shoprite Holdings Ltd (SB-CIB-1001) health score dropped to 42/100 (82.4% risk probability).',
    time: '2 mins ago',
    type: 'alert',
    read: false,
  },
  {
    id: 'n2',
    title: 'Camunda Workflow Triggered',
    message: 'CustomerRecoveryProcess & ExecutiveEscalationProcess launched for Woolworths SA (ARR: R5.4M).',
    time: '12 mins ago',
    type: 'workflow',
    read: false,
  },
  {
    id: 'n3',
    title: 'Retention Email Delivered',
    message: 'Executive Concession Email (15% Fee Concession) successfully delivered via Resend API to zolani1999@gmail.com.',
    time: '35 mins ago',
    type: 'email',
    read: false,
  },
  {
    id: 'n4',
    title: 'Recovery Case Saved',
    message: 'Sasol Enterprise Solutions retention case successfully closed with status SAVED.',
    time: '1 hour ago',
    type: 'success',
    read: true,
  },
];

export const Navbar: React.FC = () => {
  const { userEmail, userRole, logout } = useAuth();
  const navigate = useNavigate();

  const [notifications, setNotifications] = useState<NotificationItem[]>(initialNotifications);
  const [isNotifOpen, setIsNotifOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleMarkAllRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  const handleClearAll = () => {
    setNotifications([]);
  };

  const handleMarkSingleRead = (id: string) => {
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
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
    }
  };

  return (
    <header className="h-16 border-b border-dark-border bg-dark-card/50 backdrop-blur-md px-6 flex items-center justify-between sticky top-0 z-30">
      {/* Brand Title */}
      <div className="flex items-center gap-3">
        <div className="p-1.5 bg-brand-600/20 text-brand-400 rounded-lg border border-brand-500/30">
          <Building2 size={20} />
        </div>
        <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-indigo-300 bg-clip-text text-transparent">
          Standard Bank CIB
        </span>
        <span className="px-2 py-0.5 text-xs font-semibold bg-indigo-500/20 text-indigo-300 rounded border border-indigo-500/30">
          Camunda 7 BPMN Engine
        </span>
      </div>

      {/* Control Actions */}
      <div className="flex items-center gap-4 relative">
        {/* Notification Bell & Dropdown */}
        <div className="relative">
          <button
            onClick={() => {
              setIsNotifOpen(!isNotifOpen);
              setIsProfileOpen(false);
            }}
            className="p-2 text-slate-400 hover:text-slate-200 hover:bg-slate-800/60 rounded-lg transition-colors relative"
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
                  <Bell size={16} className="text-indigo-400" />
                  <span className="text-xs font-bold text-slate-100 uppercase tracking-wider">
                    Notifications ({unreadCount} unread)
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  {unreadCount > 0 && (
                    <button
                      onClick={handleMarkAllRead}
                      className="text-[11px] font-medium text-indigo-400 hover:text-indigo-300 flex items-center gap-1"
                      title="Mark all as read"
                    >
                      <CheckCheck size={12} /> Read All
                    </button>
                  )}
                  {notifications.length > 0 && (
                    <button
                      onClick={handleClearAll}
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
                    No new notifications.
                  </div>
                ) : (
                  notifications.map((item) => (
                    <div
                      key={item.id}
                      onClick={() => handleMarkSingleRead(item.id)}
                      className={`p-3 text-xs transition-colors cursor-pointer flex items-start gap-2.5 ${
                        !item.read ? 'bg-indigo-950/30' : 'hover:bg-slate-800/40 opacity-80'
                      }`}
                    >
                      <div className="mt-0.5">{getNotifIcon(item.type)}</div>
                      <div className="flex-1 space-y-1">
                        <div className="flex items-center justify-between">
                          <span className="font-semibold text-slate-200">{item.title}</span>
                          <span className="text-[10px] text-slate-500">{item.time}</span>
                        </div>
                        <p className="text-[11px] text-slate-400 leading-relaxed">{item.message}</p>
                      </div>
                      {!item.read && (
                        <span className="w-1.5 h-1.5 bg-indigo-400 rounded-full mt-1.5"></span>
                      )}
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        <div className="h-5 w-[1px] bg-slate-800"></div>

        {/* Profile Avatar & Dropdown */}
        <div className="relative">
          <button
            onClick={() => {
              setIsProfileOpen(!isProfileOpen);
              setIsNotifOpen(false);
            }}
            className="flex items-center gap-2 text-sm text-slate-300 hover:text-slate-100 p-1 rounded-lg hover:bg-slate-800/50 transition-colors"
          >
            <div className="w-8 h-8 rounded-full bg-indigo-600/30 border border-indigo-500/40 flex items-center justify-center font-bold text-indigo-400 text-xs">
              {userEmail ? userEmail.substring(0, 2).toUpperCase() : 'SB'}
            </div>
            <div className="hidden md:block text-left">
              <div className="font-medium text-xs leading-tight">{userEmail || 'admin@standardbank.co.za'}</div>
              <div className="text-[10px] text-indigo-400 flex items-center gap-1">
                <ShieldCheck size={10} /> {userRole || 'COMPANY_ADMIN'}
              </div>
            </div>
          </button>

          {/* Profile Menu */}
          {isProfileOpen && (
            <div className="absolute right-0 mt-3 w-56 bg-slate-900 border border-slate-800 rounded-xl shadow-2xl z-50 p-2 space-y-1">
              <div className="px-3 py-2 border-b border-slate-800">
                <div className="text-xs font-semibold text-slate-200 truncate">{userEmail || 'admin@standardbank.co.za'}</div>
                <div className="text-[10px] text-indigo-400 flex items-center gap-1 mt-0.5">
                  <ShieldCheck size={10} /> Role: {userRole || 'COMPANY_ADMIN'}
                </div>
              </div>
              <button
                onClick={handleLogout}
                className="w-full px-3 py-2 text-left text-xs font-semibold text-rose-400 hover:bg-rose-500/10 rounded-lg flex items-center gap-2 transition-colors"
              >
                <LogOut size={14} /> Sign Out / Logout
              </button>
            </div>
          )}
        </div>

        {/* Direct Logout Button */}
        <button
          onClick={handleLogout}
          className="px-3 py-1.5 bg-rose-600/20 hover:bg-rose-600/30 border border-rose-500/30 text-rose-300 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-all shadow-sm"
          title="Sign out of system"
        >
          <LogOut size={14} /> Logout
        </button>
      </div>
    </header>
  );
};

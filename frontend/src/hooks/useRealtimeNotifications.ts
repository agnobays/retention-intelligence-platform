import { useState, useEffect } from 'react';

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  time: string;
  type: 'alert' | 'workflow' | 'email' | 'success';
  read: boolean;
  timestamp?: number;
}

const STORAGE_KEY = 'sanisa_notifications_v1';

const getBaseUrl = () => {
  if (typeof window !== 'undefined' && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
    return 'http://localhost:8080/api/v1';
  }
  return 'https://retention-intelligence-backend.onrender.com/api/v1';
};

const INITIAL_NOTIFICATIONS: NotificationItem[] = [
  {
    id: 'n1',
    title: 'High Frustration Alert (88/100)',
    message: 'Dr. Anele Nkosi (Private Client) experienced a 72h Investment Request delay. Churn risk 88%.',
    time: '2 mins ago',
    type: 'alert',
    read: true,
  },
  {
    id: 'n2',
    title: 'Camunda Workflow Triggered',
    message: 'CustomerRecoveryProcess & ExecutiveEscalationProcess activated for Apex Logistics Enterprise.',
    time: '15 mins ago',
    type: 'workflow',
    read: true,
  },
];

const loadStoredNotifications = (): NotificationItem[] => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved !== null) {
      const parsed = JSON.parse(saved);
      if (Array.isArray(parsed)) {
        return parsed;
      }
    }
  } catch (e) {
    console.error('Error reading notifications from localStorage', e);
  }
  return INITIAL_NOTIFICATIONS;
};

export function useRealtimeNotifications() {
  const [notifications, setNotificationsState] = useState<NotificationItem[]>(loadStoredNotifications);
  const [isConnected, setIsConnected] = useState(false);

  const safeNotifications = Array.isArray(notifications) ? notifications : INITIAL_NOTIFICATIONS;

  const saveAndSetNotifications = (next: NotificationItem[]) => {
    const safeNext = Array.isArray(next) ? next : [];
    setNotificationsState(safeNext);
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(safeNext));
    } catch (e) {
      console.error('Error saving notifications to localStorage', e);
    }
  };

  useEffect(() => {
    const sseUrl = `${getBaseUrl()}/notifications/stream`;
    let eventSource: EventSource | null = null;

    try {
      eventSource = new EventSource(sseUrl);

      eventSource.onopen = () => {
        setIsConnected(true);
      };

      eventSource.addEventListener('NOTIFICATION', (event: MessageEvent) => {
        try {
          const data: NotificationItem = JSON.parse(event.data);
          setNotificationsState((prev) => {
            const prevList = Array.isArray(prev) ? prev : [];
            if (prevList.some((n) => n.id === data.id)) return prevList;
            const updated = [data, ...prevList];
            try {
              localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
            } catch (e) {}
            return updated;
          });
        } catch (err) {
          console.error('Error parsing SSE notification event:', err);
        }
      });

      eventSource.addEventListener('INIT', () => {
        setIsConnected(true);
      });

      eventSource.onerror = () => {
        setIsConnected(false);
        if (eventSource) {
          eventSource.close();
        }
      };
    } catch (e) {
      console.warn('SSE EventSource fallback active:', e);
    }

    return () => {
      if (eventSource) {
        eventSource.close();
      }
    };
  }, []);

  const markAllRead = () => {
    const updated = safeNotifications.map((n) => ({ ...n, read: true }));
    saveAndSetNotifications(updated);
  };

  const clearAll = () => {
    saveAndSetNotifications([]);
  };

  const markSingleRead = (id: string) => {
    const updated = safeNotifications.map((n) => (n.id === id ? { ...n, read: true } : n));
    saveAndSetNotifications(updated);
  };

  const addNotification = (notif: NotificationItem) => {
    const updated = [notif, ...safeNotifications];
    saveAndSetNotifications(updated);
  };

  return {
    notifications: safeNotifications,
    unreadCount: safeNotifications.filter((n) => !n.read).length,
    isConnected,
    markAllRead,
    clearAll,
    markSingleRead,
    addNotification,
  };
}

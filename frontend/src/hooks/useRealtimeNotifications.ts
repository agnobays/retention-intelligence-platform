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
    read: false,
  },
  {
    id: 'n2',
    title: 'Camunda Workflow Triggered',
    message: 'CustomerRecoveryProcess & ExecutiveEscalationProcess activated for Apex Logistics Enterprise.',
    time: '15 mins ago',
    type: 'workflow',
    read: false,
  },
  {
    id: 'n3',
    title: 'Autonomous AI Email Dispatched',
    message: 'Personalized retention compensation email sent to treasury@apexlogistics.co.za (Fee Waiver Reserved).',
    time: '42 mins ago',
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

export function useRealtimeNotifications() {
  const [notifications, setNotifications] = useState<NotificationItem[]>(INITIAL_NOTIFICATIONS);
  const [isConnected, setIsConnected] = useState(false);

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
          setNotifications((prev) => [data, ...prev]);
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
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  const clearAll = () => {
    setNotifications([]);
  };

  const markSingleRead = (id: string) => {
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
  };

  const addNotification = (notif: NotificationItem) => {
    setNotifications((prev) => [notif, ...prev]);
  };

  return {
    notifications,
    unreadCount: notifications.filter((n) => !n.read).length,
    isConnected,
    markAllRead,
    clearAll,
    markSingleRead,
    addNotification,
  };
}

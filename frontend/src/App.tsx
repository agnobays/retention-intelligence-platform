import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { ErrorBoundary } from './components/ErrorBoundary';
import { DashboardLayout } from './layouts/DashboardLayout';
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { Customers } from './pages/Customers';
import { Companies } from './pages/Companies';
import { DetectionRules } from './pages/DetectionRules';
import { RecoveryActions } from './pages/RecoveryActions';
import { Reports } from './pages/Reports';
import { Acquisition } from './pages/Acquisition';
import { Settings } from './pages/Settings';
import { Onboarding } from './pages/Onboarding';
import { MasterPlatformMonitor } from './pages/MasterPlatformMonitor';

const queryClient = new QueryClient();

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

const SuperAdminRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, userRole, userEmail } = useAuth();
  const isSuperAdmin = userRole === 'SUPER_ADMIN' || 
                       userEmail === 'owner@retention.ai' || 
                       userEmail === 'superadmin@retention.ai' ||
                       userEmail === 'zolani1999@gmail.com';
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (!isSuperAdmin) return <Navigate to="/" replace />;
  return <>{children}</>;
};

export const App: React.FC = () => {
  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/onboarding" element={<Onboarding />} />

              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <DashboardLayout />
                  </ProtectedRoute>
                }
              >
                <Route index element={<Dashboard />} />
                <Route path="customers" element={<Customers />} />
                <Route path="acquisition" element={<Acquisition />} />
                <Route path="companies" element={<Companies />} />
                <Route 
                  path="platform-monitor" 
                  element={
                    <SuperAdminRoute>
                      <MasterPlatformMonitor />
                    </SuperAdminRoute>
                  } 
                />
                <Route path="detection" element={<DetectionRules />} />
                <Route path="recovery" element={<RecoveryActions />} />
                <Route path="reports" element={<Reports />} />
                <Route path="settings" element={<Settings />} />
              </Route>

              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </BrowserRouter>
        </AuthProvider>
      </QueryClientProvider>
    </ErrorBoundary>
  );
};

export default App;

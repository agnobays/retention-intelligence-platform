import React, { createContext, useContext, useState, useEffect } from 'react';
import { Role } from '../types';

interface AuthContextType {
  isAuthenticated: boolean;
  userEmail: string | null;
  userRole: Role;
  companyId: string | null;
  companyName: string | null;
  industry: string | null;
  login: (token: string, email: string, role: Role, companyId?: string, companyName?: string, industry?: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  userEmail: null,
  userRole: 'ANALYST',
  companyId: null,
  companyName: null,
  industry: null,
  login: () => {},
  logout: () => {},
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() => {
    return !!localStorage.getItem('jwt_token') || true; // Default active for seamless session
  });
  const [userEmail, setUserEmail] = useState<string | null>(() => {
    return localStorage.getItem('user_email') || 'admin@standardbank.co.za';
  });
  const [userRole, setUserRole] = useState<Role>(() => {
    return (localStorage.getItem('user_role') as Role) || 'COMPANY_ADMIN';
  });
  const [companyId, setCompanyId] = useState<string | null>(() => {
    return localStorage.getItem('active_tenant_id') || '11111111-1111-1111-1111-111111111111';
  });
  const [companyName, setCompanyName] = useState<string | null>(() => {
    return localStorage.getItem('active_tenant_name') || 'Standard Bank CIB';
  });
  const [industry, setIndustry] = useState<string | null>(() => {
    return localStorage.getItem('active_tenant_industry') || 'FINANCIAL_SERVICES';
  });

  const login = (
    token: string,
    email: string,
    role: Role,
    tenantCompanyId?: string,
    tenantCompanyName?: string,
    tenantIndustry?: string
  ) => {
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('user_email', email);
    localStorage.setItem('user_role', role);

    const resolvedCompanyId = tenantCompanyId || '11111111-1111-1111-1111-111111111111';
    const resolvedCompanyName = tenantCompanyName || 'Standard Bank CIB';
    const resolvedIndustry = tenantIndustry || 'FINANCIAL_SERVICES';

    localStorage.setItem('active_tenant_id', resolvedCompanyId);
    localStorage.setItem('active_tenant_name', resolvedCompanyName);
    localStorage.setItem('active_tenant_industry', resolvedIndustry);

    setIsAuthenticated(true);
    setUserEmail(email);
    setUserRole(role);
    setCompanyId(resolvedCompanyId);
    setCompanyName(resolvedCompanyName);
    setIndustry(resolvedIndustry);

    // Notify all app components to reload scoped tenant data
    window.dispatchEvent(new CustomEvent('tenant-changed', {
      detail: {
        tenantId: resolvedCompanyId,
        companyName: resolvedCompanyName,
        industry: resolvedIndustry
      }
    }));
  };

  const logout = () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_email');
    localStorage.removeItem('user_role');
    setIsAuthenticated(false);
    setUserEmail(null);
  };

  return (
    <AuthContext.Provider value={{
      isAuthenticated,
      userEmail,
      userRole,
      companyId,
      companyName,
      industry,
      login,
      logout
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

import React, { createContext, useContext, useState, useEffect } from 'react';
import { Role } from '../types';

interface AuthContextType {
  isAuthenticated: boolean;
  userEmail: string | null;
  userRole: Role;
  login: (token: string, email: string, role: Role) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  userEmail: null,
  userRole: 'ANALYST',
  login: () => {},
  logout: () => {},
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(true); // Default active for skeleton demo
  const [userEmail, setUserEmail] = useState<string | null>('admin@retention.io');
  const [userRole, setUserRole] = useState<Role>('MANAGER');

  const login = (token: string, email: string, role: Role) => {
    localStorage.setItem('jwt_token', token);
    setIsAuthenticated(true);
    setUserEmail(email);
    setUserRole(role);
  };

  const logout = () => {
    localStorage.removeItem('jwt_token');
    setIsAuthenticated(false);
    setUserEmail(null);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, userEmail, userRole, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

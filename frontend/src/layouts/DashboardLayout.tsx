import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from '../components/Navbar';
import { Sidebar } from '../components/Sidebar';

export const DashboardLayout: React.FC = () => {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="min-h-screen bg-dark-bg flex flex-col overflow-x-hidden">
      <Navbar
        isMobileOpen={mobileOpen}
        onToggleMobileMenu={() => setMobileOpen(!mobileOpen)}
      />
      <div className="flex flex-1 relative">
        <Sidebar
          mobileOpen={mobileOpen}
          onCloseMobile={() => setMobileOpen(false)}
        />
        <main className="flex-1 p-3 sm:p-6 overflow-y-auto max-w-full">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

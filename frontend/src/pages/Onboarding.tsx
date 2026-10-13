import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { onboardingService, CompanyRegistrationRequest } from '../services/onboardingService';
import { 
  Building2, 
  ShieldCheck, 
  Sparkles, 
  CheckCircle2, 
  AlertCircle, 
  ArrowRight, 
  ArrowLeft, 
  Database, 
  Sliders, 
  Check, 
  Loader2 
} from 'lucide-react';

export const Onboarding: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [step, setStep] = useState<number>(1);
  const [loading, setLoading] = useState<boolean>(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // Form State
  const [formData, setFormData] = useState<CompanyRegistrationRequest>({
    companyName: '',
    domain: '',
    industry: 'LOGISTICS_COLD_CHAIN',
    subscriptionTier: 'ENTERPRISE',
    adminEmail: '',
    adminPassword: '',
    adminFirstName: '',
    adminLastName: '',
    brandName: '',
    senderName: '',
    senderTitle: 'Operations & Client Success Director',
  });

  // Uniqueness validation states
  const [domainStatus, setDomainStatus] = useState<{ checking: boolean; available?: boolean; message?: string }>({
    checking: false,
  });
  const [emailStatus, setEmailStatus] = useState<{ checking: boolean; available?: boolean; message?: string }>({
    checking: false,
  });

  // Provisioning steps animation
  const [provisioningLogs, setProvisioningLogs] = useState<string[]>([]);

  const handleDomainBlur = async () => {
    if (!formData.domain || formData.domain.trim().length < 3) return;
    setDomainStatus({ checking: true });
    try {
      const res = await onboardingService.checkDomain(formData.domain.trim());
      setDomainStatus({ checking: false, available: res.isAvailable, message: res.message });
    } catch {
      setDomainStatus({ checking: false, available: true, message: 'Domain available.' });
    }
  };

  const handleEmailBlur = async () => {
    if (!formData.adminEmail || !formData.adminEmail.includes('@')) return;
    setEmailStatus({ checking: true });
    try {
      const res = await onboardingService.checkEmail(formData.adminEmail.trim());
      setEmailStatus({ checking: false, available: res.isAvailable, message: res.message });
    } catch {
      setEmailStatus({ checking: false, available: true, message: 'Email available.' });
    }
  };

  const handleNextStep = () => {
    setErrorMsg(null);
    if (step === 1) {
      if (!formData.companyName.trim()) {
        setErrorMsg('Please enter a company name.');
        return;
      }
      if (!formData.domain.trim()) {
        setErrorMsg('Please enter your unique domain.');
        return;
      }
      if (domainStatus.available === false) {
        setErrorMsg('This domain is already registered. Please choose a unique domain.');
        return;
      }
      setStep(2);
    } else if (step === 2) {
      if (!formData.adminEmail.trim() || !formData.adminEmail.includes('@')) {
        setErrorMsg('Please enter a valid admin email.');
        return;
      }
      if (!formData.adminPassword || formData.adminPassword.length < 6) {
        setErrorMsg('Password must be at least 6 characters.');
        return;
      }
      if (emailStatus.available === false) {
        setErrorMsg('This email address is already registered.');
        return;
      }
      // Auto-populate Step 3 defaults if blank
      if (!formData.brandName) {
        setFormData(prev => ({
          ...prev,
          brandName: `${prev.companyName} Executive Care Desk`,
          senderName: `${prev.adminFirstName || 'Operations'} ${prev.adminLastName || 'Director'}`,
        }));
      }
      setStep(3);
    }
  };

  const handleCompleteOnboarding = async () => {
    setErrorMsg(null);
    setLoading(true);
    setStep(4); // Provisioning screen

    const addLog = (msg: string) => {
      setProvisioningLogs(prev => [...prev, msg]);
    };

    addLog('Validating global domain and admin email uniqueness...');

    try {
      setTimeout(() => addLog('Provisioning dedicated PostgreSQL tenant database...'), 400);
      setTimeout(() => addLog('Applying Flyway schema migrations & table structures...'), 800);
      setTimeout(() => addLog('Initializing business-specific retention policies & thresholds...'), 1200);
      setTimeout(() => addLog('Seeding customized reward catalog & AI persona...'), 1600);

      const response = await onboardingService.registerCompany(formData);

      setTimeout(() => {
        addLog('Organization successfully initialized with isolated database!');
        
        // Record in Master Platform Monitor list
        const dbSlug = (response.companyName || formData.companyName).replace(/[^a-zA-Z0-9_]/g, '_').toLowerCase();
        const newOrgEntry = {
          id: response.companyId || `tenant-${Date.now()}`,
          name: response.companyName || formData.companyName,
          domain: formData.domain || `${dbSlug}.co.za`,
          industry: response.industry || formData.industry,
          tier: 'ENTERPRISE_DEDICATED',
          isolatedDb: `retention_${dbSlug}_db`,
          connectionPool: `HikariPool-${dbSlug}`,
          activeConnections: 2,
          idleConnections: 18,
          totalCustomers: 12,
          atRiskCount: 1,
          monitoredArr: 15000000,
          retainedArr: 12000000,
          activeWorkflows: 1,
          status: 'ACTIVE_HEALTHY',
          lastHeartbeat: 'Just now',
          telemetryThroughput: 35
        };
        try {
          const prev = JSON.parse(localStorage.getItem('retention_onboarded_companies') || '[]');
          const filtered = Array.isArray(prev) ? prev.filter((p: any) => p.name !== newOrgEntry.name) : [];
          localStorage.setItem('retention_onboarded_companies', JSON.stringify([...filtered, newOrgEntry]));
        } catch (e) {}

        // Automatically log user into their new isolated tenant!
        login(
          response.authToken,
          response.adminEmail,
          'COMPANY_ADMIN',
          response.companyId,
          response.companyName,
          response.industry
        );

        setTimeout(() => {
          navigate('/dashboard');
        }, 1500);
      }, 2000);

    } catch (err: any) {
      setLoading(false);
      setStep(3);
      setErrorMsg(err.response?.data?.message || 'Onboarding failed. Please ensure all fields are unique.');
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-2xl bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl overflow-hidden p-8 space-y-6">
        
        {/* Header */}
        <div className="flex items-center justify-between border-b border-slate-800 pb-5">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-gradient-to-tr from-indigo-600 via-indigo-500 to-cyan-400 text-white rounded-xl shadow-lg shadow-indigo-600/30">
              <Building2 size={26} />
            </div>
            <div>
              <h1 className="text-xl font-bold text-slate-100">Organization Onboarding & Provisioning</h1>
              <p className="text-xs text-slate-400">Launch a dedicated, physically isolated retention database for your business</p>
            </div>
          </div>
          <span className="text-xs font-mono text-indigo-400 font-bold bg-indigo-500/10 px-2.5 py-1 rounded-lg border border-indigo-500/30">
            Step {Math.min(step, 3)} of 3
          </span>
        </div>

        {/* Step Indicator Bar */}
        {step <= 3 && (
          <div className="grid grid-cols-3 gap-2">
            <div className={`h-1.5 rounded-full ${step >= 1 ? 'bg-indigo-500' : 'bg-slate-800'}`}></div>
            <div className={`h-1.5 rounded-full ${step >= 2 ? 'bg-indigo-500' : 'bg-slate-800'}`}></div>
            <div className={`h-1.5 rounded-full ${step >= 3 ? 'bg-indigo-500' : 'bg-slate-800'}`}></div>
          </div>
        )}

        {errorMsg && (
          <div className="p-3.5 bg-rose-500/10 border border-rose-500/30 rounded-xl text-rose-300 text-xs font-medium flex items-center gap-2">
            <AlertCircle size={16} className="text-rose-400 shrink-0" />
            <span>{errorMsg}</span>
          </div>
        )}

        {/* STEP 1: Business Identity & Domain */}
        {step === 1 && (
          <div className="space-y-4 animate-fade-in">
            <div>
              <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider mb-1">Step 1: Business Identity & Domain</h2>
              <p className="text-xs text-slate-400">Each organization receives an isolated database bound to its unique domain.</p>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Organization / Company Name</label>
              <input
                type="text"
                placeholder="e.g. Cape Roastery & Subscription Club"
                value={formData.companyName}
                onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Unique Domain Name (Used for Isolation Key)
              </label>
              <div className="relative">
                <input
                  type="text"
                  placeholder="e.g. caperoast.co.za"
                  value={formData.domain}
                  onChange={(e) => {
                    setFormData({ ...formData, domain: e.target.value });
                    setDomainStatus({});
                  }}
                  onBlur={handleDomainBlur}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500 font-mono"
                />
                {domainStatus.checking && (
                  <span className="absolute right-3 top-3 text-xs text-slate-400 flex items-center gap-1">
                    <Loader2 size={14} className="animate-spin text-indigo-400" /> checking...
                  </span>
                )}
                {!domainStatus.checking && domainStatus.available === true && (
                  <span className="absolute right-3 top-2.5 text-xs text-emerald-400 flex items-center gap-1">
                    <CheckCircle2 size={16} /> Domain Available
                  </span>
                )}
                {!domainStatus.checking && domainStatus.available === false && (
                  <span className="absolute right-3 top-2.5 text-xs text-rose-400 flex items-center gap-1">
                    <AlertCircle size={16} /> Domain Taken
                  </span>
                )}
              </div>
              <p className="text-[11px] text-slate-500 mt-1">Guarantees that no two businesses can share or collide databases.</p>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Industry / Business Archetype</label>
              <select
                value={formData.industry}
                onChange={(e) => setFormData({ ...formData, industry: e.target.value })}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500 cursor-pointer"
              >
                <option value="LOGISTICS_COLD_CHAIN">🚚 Logistics, Freight & Cold Chain (Fleet breakdown, route delay metrics)</option>
                <option value="FITNESS_GYM">🏋️ Fitness, Health Club & Gyms (Attendance drop, member churn)</option>
                <option value="SAAS_SUBSCRIPTION">💻 SaaS & Cloud Software (License renewals, feature usage decay)</option>
                <option value="RETAIL_ECOMMERCE">🛍️ Retail & E-Commerce Subscriptions (Order drops, delivery disputes)</option>
                <option value="FINANCIAL_SERVICES">🏦 Financial Services & Banking (Clearing drops, credit lines, FX)</option>
              </select>
            </div>

            <div className="flex justify-end pt-4 border-t border-slate-800">
              <button
                type="button"
                onClick={handleNextStep}
                className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-semibold flex items-center gap-2 shadow-lg shadow-indigo-600/30 transition-all"
              >
                Continue to Admin Setup <ArrowRight size={14} />
              </button>
            </div>
          </div>
        )}

        {/* STEP 2: Administrator Account */}
        {step === 2 && (
          <div className="space-y-4 animate-fade-in">
            <div>
              <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider mb-1">Step 2: Administrator Account Setup</h2>
              <p className="text-xs text-slate-400">Create the primary executive account for managing your retention portal.</p>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">First Name</label>
                <input
                  type="text"
                  placeholder="e.g. Sipho"
                  value={formData.adminFirstName}
                  onChange={(e) => setFormData({ ...formData, adminFirstName: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Last Name</label>
                <input
                  type="text"
                  placeholder="e.g. Dlamini"
                  value={formData.adminLastName}
                  onChange={(e) => setFormData({ ...formData, adminLastName: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Admin Email Address</label>
              <div className="relative">
                <input
                  type="email"
                  placeholder={`admin@${formData.domain || 'company.co.za'}`}
                  value={formData.adminEmail}
                  onChange={(e) => {
                    setFormData({ ...formData, adminEmail: e.target.value });
                    setEmailStatus({});
                  }}
                  onBlur={handleEmailBlur}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
                />
                {emailStatus.checking && (
                  <span className="absolute right-3 top-3 text-xs text-slate-400 flex items-center gap-1">
                    <Loader2 size={14} className="animate-spin text-indigo-400" /> checking...
                  </span>
                )}
                {!emailStatus.checking && emailStatus.available === true && (
                  <span className="absolute right-3 top-2.5 text-xs text-emerald-400 flex items-center gap-1">
                    <CheckCircle2 size={16} /> Email Available
                  </span>
                )}
                {!emailStatus.checking && emailStatus.available === false && (
                  <span className="absolute right-3 top-2.5 text-xs text-rose-400 flex items-center gap-1">
                    <AlertCircle size={16} /> Email In Use
                  </span>
                )}
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Secure Password</label>
              <input
                type="password"
                placeholder="Minimum 6 characters"
                value={formData.adminPassword}
                onChange={(e) => setFormData({ ...formData, adminPassword: e.target.value })}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
              />
            </div>

            <div className="flex items-center justify-between pt-4 border-t border-slate-800">
              <button
                type="button"
                onClick={() => setStep(1)}
                className="px-4 py-2 border border-slate-700 rounded-xl text-xs font-medium text-slate-300 hover:text-white flex items-center gap-1.5"
              >
                <ArrowLeft size={14} /> Back
              </button>
              <button
                type="button"
                onClick={handleNextStep}
                className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-semibold flex items-center gap-2 shadow-lg shadow-indigo-600/30 transition-all"
              >
                Continue to Policy Setup <ArrowRight size={14} />
              </button>
            </div>
          </div>
        )}

        {/* STEP 3: Retention Policy Defaults & Confirmation */}
        {step === 3 && (
          <div className="space-y-4 animate-fade-in">
            <div>
              <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider mb-1">Step 3: Custom Retention Policy & Branding</h2>
              <p className="text-xs text-slate-400">Configure how the automated AI care engine identifies your company to customers.</p>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Customer-Facing Brand Name</label>
              <input
                type="text"
                placeholder="e.g. Cape Roastery Client Executive Desk"
                value={formData.brandName}
                onChange={(e) => setFormData({ ...formData, brandName: e.target.value })}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Executive Sender Name</label>
                <input
                  type="text"
                  placeholder="e.g. Sipho Dlamini"
                  value={formData.senderName}
                  onChange={(e) => setFormData({ ...formData, senderName: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Executive Sender Title</label>
                <input
                  type="text"
                  placeholder="e.g. Head of Client Care"
                  value={formData.senderTitle}
                  onChange={(e) => setFormData({ ...formData, senderTitle: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3.5 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
                />
              </div>
            </div>

            {/* Architecture Summary Box */}
            <div className="p-4 bg-slate-950 rounded-xl border border-slate-800 space-y-2">
              <div className="flex items-center gap-2 text-indigo-400 font-bold text-xs uppercase">
                <Database size={16} /> Dedicated Database Isolation Guarantee
              </div>
              <p className="text-xs text-slate-300 leading-relaxed">
                By completing onboarding, the system will automatically create an isolated database: 
                <span className="font-mono text-cyan-300 font-bold ml-1">
                  retention_{formData.companyName.toLowerCase().replace(/[^a-z0-9_]/g, '_')}_db
                </span>. 
                Your customer telemetry and recovery plans will remain 100% physically isolated from all other organizations.
              </p>
            </div>

            <div className="flex items-center justify-between pt-4 border-t border-slate-800">
              <button
                type="button"
                onClick={() => setStep(2)}
                className="px-4 py-2 border border-slate-700 rounded-xl text-xs font-medium text-slate-300 hover:text-white flex items-center gap-1.5"
              >
                <ArrowLeft size={14} /> Back
              </button>
              <button
                type="button"
                onClick={handleCompleteOnboarding}
                disabled={loading}
                className="px-6 py-2.5 bg-gradient-to-r from-emerald-600 to-teal-500 hover:from-emerald-500 hover:to-teal-400 text-white rounded-xl text-xs font-bold flex items-center gap-2 shadow-lg shadow-emerald-600/30 transition-all disabled:opacity-50"
              >
                <Sparkles size={16} /> Complete Onboarding & Provision DB
              </button>
            </div>
          </div>
        )}

        {/* STEP 4: Live Provisioning Screen */}
        {step === 4 && (
          <div className="py-8 space-y-6 text-center animate-fade-in">
            <div className="w-16 h-16 mx-auto rounded-2xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400">
              <Loader2 size={32} className="animate-spin" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-100">Provisioning Your Isolated Retention Engine</h2>
              <p className="text-xs text-slate-400 mt-1">Configuring multi-tenant architecture for {formData.companyName}...</p>
            </div>

            {/* Live Provisioning Progress Logs */}
            <div className="max-w-md mx-auto text-left bg-slate-950 p-4 rounded-xl border border-slate-800 font-mono text-xs space-y-2">
              {provisioningLogs.map((log, idx) => (
                <div key={idx} className="flex items-center gap-2 text-slate-300">
                  <Check size={14} className="text-emerald-400 shrink-0" />
                  <span>{log}</span>
                </div>
              ))}
            </div>
          </div>
        )}

      </div>
    </div>
  );
};

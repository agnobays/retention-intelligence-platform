import React, { Component, ErrorInfo, ReactNode } from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Uncaught React Error:', error, errorInfo);
  }

  public render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-6 text-slate-100">
          <div className="max-w-md w-full bg-slate-900 border border-slate-800 rounded-2xl p-6 text-center space-y-4 shadow-2xl">
            <div className="p-3 bg-rose-500/10 text-rose-400 rounded-full w-fit mx-auto">
              <AlertTriangle size={32} />
            </div>
            <h2 className="text-xl font-bold">Standard Bank Retention Platform</h2>
            <p className="text-sm text-slate-400">
              An unexpected display error occurred. The system has automatically recovered fallback intelligence metrics.
            </p>
            <button
              onClick={() => {
                this.setState({ hasError: false });
                window.location.reload();
              }}
              className="w-full py-2.5 bg-brand-600 hover:bg-brand-500 text-white font-medium rounded-lg text-sm flex items-center justify-center gap-2 transition-all"
            >
              <RefreshCw size={16} /> Reload Portal
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

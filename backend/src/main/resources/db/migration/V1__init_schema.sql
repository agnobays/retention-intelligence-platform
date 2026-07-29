-- =========================================================
-- V1__init_schema.sql
-- Customer Retention Intelligence Platform Schema
-- =========================================================

-- 1. Companies (Multi-Tenant Organizations)
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255) UNIQUE NOT NULL,
    industry VARCHAR(100),
    subscription_tier VARCHAR(50) DEFAULT 'ENTERPRISE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Users (Platform Accounts with RBAC)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Customers (Imported Client Data)
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    external_customer_id VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    mrr NUMERIC(12, 2) DEFAULT 0.00,
    arr NUMERIC(12, 2) DEFAULT 0.00,
    health_score INT DEFAULT 100 CHECK (health_score BETWEEN 0 AND 100),
    churn_probability NUMERIC(5, 2) DEFAULT 0.00 CHECK (churn_probability BETWEEN 0.00 AND 100.00),
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'AT_RISK', 'RECOVERING', 'CHURNED', 'SAVED')),
    contract_renewal_date DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Customer Value Scores
CREATE TABLE customer_value_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    ltv NUMERIC(14, 2) DEFAULT 0.00,
    usage_frequency_score INT CHECK (usage_frequency_score BETWEEN 0 AND 100),
    support_ticket_volume INT DEFAULT 0,
    sla_tier VARCHAR(50) DEFAULT 'STANDARD',
    strategic_value_tier VARCHAR(50) DEFAULT 'TIER_3',
    calculated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. At Risk Metrics (Detection Triggers)
CREATE TABLE at_risk_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    metric_type VARCHAR(100) NOT NULL, -- e.g., LOGINS_DROP_50_PCT, PAYMENT_FAILED, NPS_DETRACTOR
    severity VARCHAR(20) DEFAULT 'MEDIUM' CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    metric_value VARCHAR(255),
    detected_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. Decision Engine Rules & Recovery Plans
CREATE TABLE recovery_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    recommended_action VARCHAR(255) NOT NULL,
    discount_percentage INT DEFAULT 0,
    assigned_manager_id UUID REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'PENDING_APPROVAL' CHECK (status IN ('PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'EXECUTING', 'COMPLETED', 'FAILED')),
    outcome_notes TEXT,
    workflow_instance_id VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Audit Trail Logs
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255),
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. System Notifications
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_customers_company ON customers(company_id);
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_at_risk_customer ON at_risk_metrics(customer_id);
CREATE INDEX idx_recovery_plans_status ON recovery_plans(status);

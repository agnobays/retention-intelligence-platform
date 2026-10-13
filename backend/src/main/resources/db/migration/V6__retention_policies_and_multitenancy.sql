-- =========================================================
-- V6__retention_policies_and_multitenancy.sql
-- Multi-Tenant Retention Policies & Local Business Benchmark
-- =========================================================

-- 1. Create Retention Policy Table (Tenant-Configurable Churn & Retention Rules)
CREATE TABLE IF NOT EXISTS retention_policies (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    business_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    brand_name VARCHAR(100) NOT NULL,
    sender_name VARCHAR(100) NOT NULL,
    sender_title VARCHAR(150) NOT NULL,
    sender_email VARCHAR(150) NOT NULL,
    volume_decay_weight NUMERIC(3, 2) NOT NULL DEFAULT 0.50,
    friction_weight NUMERIC(3, 2) NOT NULL DEFAULT 0.60,
    billing_risk_weight NUMERIC(3, 2) NOT NULL DEFAULT 0.40,
    auto_approval_threshold INT NOT NULL DEFAULT 65,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT uq_company_retention_policy UNIQUE (company_id)
);

-- 2. Seed Retention Policy for Standard Bank
INSERT INTO retention_policies (
    company_id, business_type, brand_name, sender_name, sender_title, sender_email,
    volume_decay_weight, friction_weight, billing_risk_weight, auto_approval_threshold
)
SELECT
    id, 'FINANCIAL_SERVICES', 'Standard Bank Executive Care Desk', 'Sipho Dlamini',
    'Senior Relationship Director', 'sipho.dlamini@standardbank.co.za',
    0.50, 0.60, 0.40, 70
FROM companies WHERE name ILIKE '%standard bank%' LIMIT 1
ON CONFLICT (company_id) DO NOTHING;

-- 3. Seed Local Business Company: Kasi Express Logistics & Cold Chain
INSERT INTO companies (id, name, domain, industry, subscription_tier)
VALUES (
    '88888888-8888-8888-8888-888888888888',
    'Kasi Express Logistics',
    'kasiexpress.co.za',
    'LOGISTICS_COLD_CHAIN',
    'ENTERPRISE'
)
ON CONFLICT DO NOTHING;

-- 4. Seed Local Business User
INSERT INTO users (id, company_id, email, password_hash, first_name, last_name, role, active)
VALUES (
    '99999999-9999-9999-9999-999999999999',
    '88888888-8888-8888-8888-888888888888',
    'manager@kasiexpress.co.za',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVym50CR6251HnUeyhG1pA6W',
    'Lerato',
    'Khumalo',
    'COMPANY_ADMIN',
    true
)
ON CONFLICT DO NOTHING;

-- 5. Seed Retention Policy for Kasi Express Logistics
INSERT INTO retention_policies (
    company_id, business_type, brand_name, sender_name, sender_title, sender_email,
    volume_decay_weight, friction_weight, billing_risk_weight, auto_approval_threshold
) VALUES (
    '88888888-8888-8888-8888-888888888888',
    'LOGISTICS_COLD_CHAIN',
    'Kasi Express Fleet & Merchant Care',
    'Lerato Khumalo',
    'Chief Operations & Fleet Director',
    'operations@kasiexpress.co.za',
    0.60, 0.70, 0.50, 65
)
ON CONFLICT (company_id) DO NOTHING;

-- 6. Seed Configurable Reward Catalogue for Kasi Express Logistics
INSERT INTO reward_catalog (company_id, reward_type, reward_name, reward_value, target_segment, min_frustration_score, requires_approval, active)
VALUES 
(
    '88888888-8888-8888-8888-888888888888',
    'VOUCHER',
    'Shell Fleet Fuel Card Credit',
    'R5,000 Diesel & Fleet Fuel Rebate',
    'ENTERPRISE_FLEET',
    65,
    TRUE,
    TRUE
),
(
    '88888888-8888-8888-8888-888888888888',
    'FEE_WAIVER',
    'Cold Storage & Cross-Docking Waiver',
    '1 Month Zero-Fee Cold Storage Facility Access',
    'COMMERCIAL_SME',
    60,
    FALSE,
    TRUE
),
(
    '88888888-8888-8888-8888-888888888888',
    'CUSTOM',
    'Priority Express Loading Bay Pass',
    'VIP Priority Fleet Dispatch Bay access for 60 days',
    'COMMERCIAL_SME',
    50,
    FALSE,
    TRUE
),
(
    '88888888-8888-8888-8888-888888888888',
    'CASHBACK',
    'Merchant Dispatch Volume Rebate',
    '10% Shipping Rebate credited to Logistics Ledger',
    'ALL',
    70,
    TRUE,
    TRUE
)
ON CONFLICT DO NOTHING;

-- 7. Seed Initial Local Business Accounts for Kasi Express Logistics
INSERT INTO customers (
    id, company_id, external_customer_id, name, email, customer_segment, tenure, products_held,
    mrr, arr, health_score, frustration_score, satisfaction_score, churn_probability, status,
    issue_category, issue_severity, previous_complaints_count, resolution_time_hours,
    interactions_count, escalations_count, recommended_intervention, reward_category, reward_value,
    reward_redeemed, post_recovery_score, retention_outcome, contract_renewal_date
) VALUES 
(
    'c1111111-1111-1111-1111-111111111111',
    '88888888-8888-8888-8888-888888888888',
    'KEL-FLEET-301',
    'Soweto Fresh Wholesalers & Distributors',
    'procurement@sowetofresh.co.za',
    'ENTERPRISE_FLEET',
    '3 years',
    'Refrigerated Linehaul, Daily FMCG Distribution, Pallet Warehousing',
    180000.00,
    2160000.00,
    38,
    82,
    44,
    81.50,
    'AT_RISK',
    'Refrigerated Truck Breakdown & 6h Delivery Delay',
    'CRITICAL',
    2,
    6,
    4,
    2,
    'Fleet Director Personal Intervention & Fuel Rebate',
    'VOUCHER — Shell Fleet Fuel Card Credit',
    'R5,000 Diesel & Fleet Fuel Rebate',
    FALSE,
    88,
    'PENDING',
    '2027-04-30'
),
(
    'c2222222-2222-2222-2222-222222222222',
    '88888888-8888-8888-8888-888888888888',
    'KEL-SME-302',
    'Mama Zola Artisan Bakeries (12 Branches)',
    'dispatch@mamazola.co.za',
    'COMMERCIAL_SME',
    '2 years',
    'Morning Delivery Route, Packaging Logistics',
    45000.00,
    540000.00,
    42,
    74,
    50,
    72.00,
    'AT_RISK',
    'Late Morning Dispatch to Sandton Retail Outlet',
    'HIGH',
    1,
    4,
    3,
    1,
    'Operations Specialist Outreach & Priority Bay Pass',
    'CUSTOM — Priority Express Loading Bay Pass',
    'VIP Priority Fleet Dispatch Bay access for 60 days',
    FALSE,
    85,
    'PENDING',
    '2027-08-15'
),
(
    'c3333333-3333-3333-3333-333333333333',
    '88888888-8888-8888-8888-888888888888',
    'KEL-CORP-303',
    'Highveld Cold Chain Meats Ltd',
    'logistics@highveldmeats.co.za',
    'ENTERPRISE_FLEET',
    '5 years',
    'Dedicated Fleet, Temperature-Monitored Inter-Provincial Linehaul',
    310000.00,
    3720000.00,
    92,
    15,
    90,
    8.40,
    'ACTIVE',
    'Scheduled Maintenance Notice',
    'LOW',
    0,
    1,
    1,
    0,
    'Routine Account Review',
    'Standard Service',
    'N/A',
    FALSE,
    95,
    'RETAINED',
    '2027-12-31'
)
ON CONFLICT DO NOTHING;

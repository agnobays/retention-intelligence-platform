-- =========================================================
-- V3__sanisa_retention_intelligence_schema.sql
-- Tailors DB Schema & Seeds Data for Sanisa Retention Intelligence Platform™
-- =========================================================

-- 1. Add Sanisa Data Model Columns to customers table
ALTER TABLE customers ADD COLUMN IF NOT EXISTS customer_segment VARCHAR(50);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS tenure VARCHAR(50);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS products_held VARCHAR(255);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS frustration_score INT DEFAULT 50;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS satisfaction_score INT DEFAULT 50;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS issue_category VARCHAR(100);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS issue_severity VARCHAR(50);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS previous_complaints_count INT DEFAULT 0;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS resolution_time_hours INT DEFAULT 24;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS interactions_count INT DEFAULT 1;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS escalations_count INT DEFAULT 0;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS recommended_intervention VARCHAR(255);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS reward_category VARCHAR(100);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS reward_value VARCHAR(255);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS reward_redeemed BOOLEAN DEFAULT FALSE;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS post_recovery_score INT DEFAULT 85;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS retention_outcome VARCHAR(50) DEFAULT 'PENDING';

-- 2. Seed Sanisa Pilot Scenario 01: Private Client
INSERT INTO customers (
    id, company_id, external_customer_id, name, email, customer_segment, tenure, products_held,
    mrr, arr, health_score, frustration_score, satisfaction_score, churn_probability, status,
    issue_category, issue_severity, previous_complaints_count, resolution_time_hours,
    interactions_count, escalations_count, recommended_intervention, reward_category, reward_value,
    reward_redeemed, post_recovery_score, retention_outcome, contract_renewal_date
) VALUES (
    'b7777777-7777-7777-7777-777777777777',
    '11111111-1111-1111-1111-111111111111',
    'SB-PC-001',
    'Dr. Anele Nkosi (Private Client)',
    'anele.nkosi@privateclient.co.za',
    'PRIVATE_CLIENT',
    '8 years',
    'Current Account, Investments, Credit Card, Home Loan',
    45000.00,
    540000.00,
    12,
    88,
    42,
    88.00,
    'AT_RISK',
    'Service Delay (Investment Request)',
    'HIGH',
    1,
    72,
    5,
    2,
    'Senior Relationship Manager Intervention & Personalised Apology',
    'Lifestyle & Executive Experience',
    'R1,500 Lifestyle Experience Voucher & Dedicated Private Banker',
    FALSE,
    85,
    'PENDING',
    '2027-06-30'
) ON CONFLICT DO NOTHING;

-- 3. Seed Sanisa Pilot Scenario 02: Commercial Client
INSERT INTO customers (
    id, company_id, external_customer_id, name, email, customer_segment, tenure, products_held,
    mrr, arr, health_score, frustration_score, satisfaction_score, churn_probability, status,
    issue_category, issue_severity, previous_complaints_count, resolution_time_hours,
    interactions_count, escalations_count, recommended_intervention, reward_category, reward_value,
    reward_redeemed, post_recovery_score, retention_outcome, contract_renewal_date
) VALUES (
    'b8888888-8888-8888-8888-888888888888',
    '11111111-1111-1111-1111-111111111111',
    'SB-CC-002',
    'Apex Logistics Enterprise (SME)',
    'treasury@apexlogistics.co.za',
    'COMMERCIAL_SME',
    '4 years',
    'Business Account, Merchant Services, Business Lending',
    66666.67,
    800000.00,
    24,
    76,
    48,
    76.00,
    'AT_RISK',
    'Merchant Settlement / Payment Disruption',
    'HIGH',
    2,
    24,
    3,
    1,
    'Business Specialist Intervention & Proactive Monitoring',
    'Merchant Fee Credit & Business Benefit',
    'Merchant Fee Waiver & Priority Settlement Desk Access',
    FALSE,
    88,
    'PENDING',
    '2027-09-15'
) ON CONFLICT DO NOTHING;

-- 4. Seed Sanisa Pilot Scenario 03: Everyday Customer
INSERT INTO customers (
    id, company_id, external_customer_id, name, email, customer_segment, tenure, products_held,
    mrr, arr, health_score, frustration_score, satisfaction_score, churn_probability, status,
    issue_category, issue_severity, previous_complaints_count, resolution_time_hours,
    interactions_count, escalations_count, recommended_intervention, reward_category, reward_value,
    reward_redeemed, post_recovery_score, retention_outcome, contract_renewal_date
) VALUES (
    'b9999999-9999-9999-9999-999999999999',
    '11111111-1111-1111-1111-111111111111',
    'SB-EC-003',
    'Thabo Khumalo (Everyday Banking)',
    'thabo.khumalo@gmail.com',
    'EVERYDAY_BANKING',
    '2 years',
    'Current Account + Debit Card',
    708.33,
    8500.00,
    38,
    62,
    55,
    62.00,
    'AT_RISK',
    'Card Transaction Dispute SLA Dissatisfaction',
    'MEDIUM',
    0,
    12,
    1,
    0,
    'Automated Recovery Email & Thank-You Communication',
    'Retail Voucher & Loyalty Points',
    '5,000 Loyalty Points / R250 Retail Voucher',
    TRUE,
    85,
    'RETAINED',
    '2027-11-20'
) ON CONFLICT DO NOTHING;

-- 5. Update existing corporate clients with Sanisa data attributes
UPDATE customers SET
    customer_segment = 'COMMERCIAL_SME',
    tenure = '10 years',
    products_held = 'Merchant Clearing, Corporate Lending, FX Hedging',
    frustration_score = 82,
    satisfaction_score = 42,
    issue_category = 'API Gateway Merchant SLA Disruption',
    issue_severity = 'CRITICAL',
    previous_complaints_count = 3,
    resolution_time_hours = 48,
    interactions_count = 8,
    escalations_count = 3,
    recommended_intervention = 'Executive CIB Outreach & 15% Concession',
    reward_category = 'Corporate Fee Credit',
    reward_value = '15% Transaction Fee Waiver & Dedicated RM',
    retention_outcome = 'PENDING'
WHERE external_customer_id = 'SB-CIB-1001';

UPDATE customers SET
    customer_segment = 'COMMERCIAL_SME',
    tenure = '6 years',
    products_held = 'Corporate Checking, Fleet Cards, Business Lending',
    frustration_score = 64,
    satisfaction_score = 58,
    issue_category = 'Credit Line Drawdown Reduction',
    issue_severity = 'MEDIUM',
    previous_complaints_count = 1,
    resolution_time_hours = 24,
    interactions_count = 3,
    escalations_count = 1,
    recommended_intervention = 'Custom FX Rate Lock & 10% Fee Discount',
    reward_category = 'Treasury FX Concession',
    reward_value = 'Custom FX Spread Lock',
    retention_outcome = 'RETAINED'
WHERE external_customer_id = 'SB-CIB-1002';

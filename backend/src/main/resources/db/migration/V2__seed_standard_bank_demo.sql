-- =========================================================
-- V2__seed_standard_bank_demo.sql
-- Seed Data for Standard Bank South Africa CIB Demo
-- =========================================================

-- 1. Seed Company: Standard Bank Corporate & Investment Banking (CIB)
INSERT INTO companies (id, name, domain, industry, subscription_tier)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Standard Bank CIB',
    'standardbank.co.za',
    'FINANCIAL_SERVICES',
    'ENTERPRISE'
);

-- 2. Seed Users (BCrypt Hash for password: 'Password123!')
-- BCrypt: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVym50CR6251HnUeyhG1pA6W
INSERT INTO users (id, company_id, email, password_hash, first_name, last_name, role, active)
VALUES 
(
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'admin@standardbank.co.za',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVym50CR6251HnUeyhG1pA6W',
    'Sipho',
    'Dlamini',
    'COMPANY_ADMIN',
    true
),
(
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'manager@standardbank.co.za',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVym50CR6251HnUeyhG1pA6W',
    'Thabo',
    'Mokoena',
    'MANAGER',
    true
),
(
    '44444444-4444-4444-4444-444444444444',
    '11111111-1111-1111-1111-111111111111',
    'analyst@standardbank.co.za',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVym50CR6251HnUeyhG1pA6W',
    'Keira',
    'van der Merwe',
    'ANALYST',
    true
);

-- 3. Seed Corporate Customers
INSERT INTO customers (id, company_id, external_customer_id, name, email, mrr, arr, health_score, churn_probability, status, contract_renewal_date)
VALUES
(
    'a1111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    'SB-CIB-1001',
    'Shoprite Holdings Ltd',
    'corporate.treasury@shoprite.co.za',
    250000.00,
    3000000.00,
    42,
    82.40,
    'AT_RISK',
    '2026-11-30'
),
(
    'a2222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'SB-CIB-1002',
    'MTN Group South Africa',
    'finance@mtn.co.za',
    180000.00,
    2160000.00,
    58,
    64.00,
    'RECOVERING',
    '2027-03-15'
),
(
    'a3333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'SB-CIB-1003',
    'Sasol Enterprise Solutions',
    'accounts@sasol.com',
    320000.00,
    3840000.00,
    88,
    12.50,
    'ACTIVE',
    '2027-08-01'
),
(
    'a4444444-4444-4444-4444-444444444444',
    '11111111-1111-1111-1111-111111111111',
    'SB-CIB-1004',
    'Discovery Health Group',
    'payments@discovery.co.za',
    150000.00,
    1800000.00,
    35,
    89.00,
    'AT_RISK',
    '2026-10-15'
),
(
    'a5555555-5555-5555-5555-555555555555',
    '11111111-1111-1111-1111-111111111111',
    'SB-CIB-1005',
    'Pick n Pay Retailers',
    'treasury@pnp.co.za',
    95000.00,
    1140000.00,
    91,
    8.20,
    'ACTIVE',
    '2027-05-20'
),
(
    'a6666666-6666-6666-6666-666666666666',
    '11111111-1111-1111-1111-111111111111',
    'SB-CIB-1006',
    'Anglo American Platinum',
    'finance@angloamerican.com',
    410000.00,
    4920000.00,
    95,
    4.10,
    'SAVED',
    '2027-12-10'
);

-- 4. Seed Customer Value Scores
INSERT INTO customer_value_scores (id, customer_id, ltv, usage_frequency_score, support_ticket_volume, sla_tier, strategic_value_tier)
VALUES
(gen_random_uuid(), 'a1111111-1111-1111-1111-111111111111', 12500000.00, 35, 8, 'ENTERPRISE_PLATINUM', 'TIER_1'),
(gen_random_uuid(), 'a2222222-2222-2222-2222-222222222222', 8500000.00, 52, 5, 'ENTERPRISE_GOLD', 'TIER_1'),
(gen_random_uuid(), 'a3333333-3333-3333-3333-333333333333', 15300000.00, 92, 1, 'ENTERPRISE_PLATINUM', 'TIER_1'),
(gen_random_uuid(), 'a4444444-4444-4444-4444-444444444444', 7200000.00, 28, 12, 'ENTERPRISE_GOLD', 'TIER_2'),
(gen_random_uuid(), 'a5555555-5555-5555-5555-555555555555', 4500000.00, 88, 2, 'STANDARD', 'TIER_2'),
(gen_random_uuid(), 'a6666666-6666-6666-6666-666666666666', 19600000.00, 96, 0, 'ENTERPRISE_PLATINUM', 'TIER_1');

-- 5. Seed At-Risk Metrics
INSERT INTO at_risk_metrics (id, customer_id, metric_type, severity, metric_value)
VALUES
(gen_random_uuid(), 'a1111111-1111-1111-1111-111111111111', 'TRANSACTION_VOLUME_DROP_45_PCT', 'CRITICAL', '45% decline in daily merchant clearing transactions'),
(gen_random_uuid(), 'a1111111-1111-1111-1111-111111111111', 'HIGH_ESCALATED_SUPPORT_TICKETS', 'HIGH', '8 unresolved corporate banking API SLA issues'),
(gen_random_uuid(), 'a2222222-2222-2222-2222-222222222222', 'CREDIT_LINE_DRAWDOWN_REDUCTION', 'MEDIUM', '30% drop in revolving credit facility utilization'),
(gen_random_uuid(), 'a4444444-4444-4444-4444-444444444444', 'NPS_DETRACTOR_SCORE', 'CRITICAL', 'NPS score dropped to 3/10 following gateway maintenance');

-- 6. Seed Recovery Plans
INSERT INTO recovery_plans (id, customer_id, recommended_action, discount_percentage, assigned_manager_id, status, outcome_notes)
VALUES
(
    'b1111111-1111-1111-1111-111111111111',
    'a1111111-1111-1111-1111-111111111111',
    'DEDICATED_CIB_RELATIONSHIP_MANAGER_OUTREACH_AND_15_PERCENT_FEE_DISCOUNT',
    15,
    '33333333-3333-3333-3333-333333333333',
    'PENDING_APPROVAL',
    'Requires Executive Manager approval for corporate fee concession.'
),
(
    'b2222222-2222-2222-2222-222222222222',
    'a2222222-2222-2222-2222-222222222222',
    'CUSTOM_FX_RATE_LOCK_AND_10_PERCENT_DISCOUNT',
    10,
    '33333333-3333-3333-3333-333333333333',
    'EXECUTING',
    'Relationship manager conducting on-site treasury optimization review.'
);

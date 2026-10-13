-- V4: Configurable Reward Catalogue for Loyalty Reward Dispatch
-- Allows any company (e.g. Standard Bank) to define their own reward types per segment

CREATE TABLE IF NOT EXISTS reward_catalog (
    id                      UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    company_id              UUID NOT NULL REFERENCES companies(id),
    reward_type             VARCHAR(50)  NOT NULL, -- LOYALTY_POINTS | CASHBACK | VOUCHER | FEE_WAIVER | LIFESTYLE_EXPERIENCE | CUSTOM
    reward_name             VARCHAR(200) NOT NULL, -- e.g. "UCount Rewards Points"
    reward_value            VARCHAR(300) NOT NULL, -- e.g. "5000 points", "R2,500 cashback"
    target_segment          VARCHAR(50)  NOT NULL DEFAULT 'ALL', -- EVERYDAY_BANKING | PRIVATE_CLIENT | COMMERCIAL_SME | CORPORATE_CIB | ALL
    min_frustration_score   INT          NOT NULL DEFAULT 50,    -- Only trigger when frustration >= this
    requires_approval       BOOLEAN      NOT NULL DEFAULT FALSE,
    active                  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS reward_dispatch_log (
    id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    customer_id         UUID NOT NULL REFERENCES customers(id),
    reward_catalog_id   UUID REFERENCES reward_catalog(id),
    reward_type         VARCHAR(50)  NOT NULL,
    reward_name         VARCHAR(200) NOT NULL,
    reward_value        VARCHAR(300) NOT NULL,
    dispatch_status     VARCHAR(50)  NOT NULL DEFAULT 'DISPATCHED', -- DISPATCHED | REDEEMED | EXPIRED | FAILED
    dispatch_channel    VARCHAR(50)  NOT NULL DEFAULT 'EMAIL',      -- EMAIL | SMS | API | MANUAL
    dispatched_at       TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),
    redeemed_at         TIMESTAMP WITH TIME ZONE,
    notes               TEXT
);

-- Seed Standard Bank default reward catalogue
INSERT INTO reward_catalog (company_id, reward_type, reward_name, reward_value, target_segment, min_frustration_score, requires_approval, active)
SELECT
    c.id,
    'LOYALTY_POINTS',
    'UCount Rewards Points',
    '5,000 UCount loyalty points redeemable at partner stores',
    'EVERYDAY_BANKING',
    50,
    FALSE,
    TRUE
FROM companies c WHERE c.name ILIKE '%standard bank%' LIMIT 1;

INSERT INTO reward_catalog (company_id, reward_type, reward_name, reward_value, target_segment, min_frustration_score, requires_approval, active)
SELECT
    c.id,
    'VOUCHER',
    'Lifestyle Dining Voucher',
    'R500 dining experience voucher at partner restaurants',
    'EVERYDAY_BANKING',
    65,
    FALSE,
    TRUE
FROM companies c WHERE c.name ILIKE '%standard bank%' LIMIT 1;

INSERT INTO reward_catalog (company_id, reward_type, reward_name, reward_value, target_segment, min_frustration_score, requires_approval, active)
SELECT
    c.id,
    'FEE_WAIVER',
    'Merchant Clearing Fee Waiver',
    '1 month merchant clearing fee waiver',
    'COMMERCIAL_SME',
    70,
    TRUE,
    TRUE
FROM companies c WHERE c.name ILIKE '%standard bank%' LIMIT 1;

INSERT INTO reward_catalog (company_id, reward_type, reward_name, reward_value, target_segment, min_frustration_score, requires_approval, active)
SELECT
    c.id,
    'CASHBACK',
    'Business Account Cashback',
    'R1,000 cashback credited to business account',
    'COMMERCIAL_SME',
    75,
    TRUE,
    TRUE
FROM companies c WHERE c.name ILIKE '%standard bank%' LIMIT 1;

INSERT INTO reward_catalog (company_id, reward_type, reward_name, reward_value, target_segment, min_frustration_score, requires_approval, active)
SELECT
    c.id,
    'LIFESTYLE_EXPERIENCE',
    'Executive Lifestyle Experience',
    'R1,500 executive lifestyle experience voucher & dedicated private banker access',
    'PRIVATE_CLIENT',
    70,
    TRUE,
    TRUE
FROM companies c WHERE c.name ILIKE '%standard bank%' LIMIT 1;

INSERT INTO reward_catalog (company_id, reward_type, reward_name, reward_value, target_segment, min_frustration_score, requires_approval, active)
SELECT
    c.id,
    'FEE_WAIVER',
    'Corporate Clearing Fee Waiver',
    '15% corporate clearing fee concession for 3 months',
    'CORPORATE_CIB',
    75,
    TRUE,
    TRUE
FROM companies c WHERE c.name ILIKE '%standard bank%' LIMIT 1;

INSERT INTO reward_catalog (company_id, reward_type, reward_name, reward_value, target_segment, min_frustration_score, requires_approval, active)
SELECT
    c.id,
    'CASHBACK',
    'Executive Account Credit',
    'R2,500 cashback credited to corporate treasury account',
    'CORPORATE_CIB',
    80,
    TRUE,
    TRUE
FROM companies c WHERE c.name ILIKE '%standard bank%' LIMIT 1;

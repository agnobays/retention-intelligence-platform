-- V5: Leads Schema for Acquisition Pipeline
-- Creates the leads table to support the AI Acquisition functionality

CREATE TABLE IF NOT EXISTS leads (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    industry_segment VARCHAR(100) NOT NULL,
    propensity_to_buy INT NOT NULL,
    estimated_arr NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

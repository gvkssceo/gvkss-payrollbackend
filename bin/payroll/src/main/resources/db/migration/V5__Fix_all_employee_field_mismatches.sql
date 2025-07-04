-- Fix ALL Employee entity field mismatches with database schema

-- Create missing enum type for tax_filing_status first (only if it doesn't exist)
DO $$ BEGIN
    CREATE TYPE tax_filing_status AS ENUM ('SINGLE', 'MARRIED', 'HEAD_OF_HOUSEHOLD', 'WIDOWED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Add missing fields that the entity expects but database doesn't have
ALTER TABLE employees 
ADD COLUMN IF NOT EXISTS ssn VARCHAR(255), -- Entity has ssn but DB has ssn_encrypted
ADD COLUMN IF NOT EXISTS standard_hours DECIMAL(5,2) DEFAULT 40.00, -- Entity has standardHours
ADD COLUMN IF NOT EXISTS tax_filing_status tax_filing_status DEFAULT 'SINGLE', -- Entity has taxFilingStatus
ADD COLUMN IF NOT EXISTS is_exempt BOOLEAN DEFAULT FALSE, -- Entity has isExempt
ADD COLUMN IF NOT EXISTS job_title VARCHAR(100), -- Entity missing this field
ADD COLUMN IF NOT EXISTS department VARCHAR(100), -- Entity missing this field
ADD COLUMN IF NOT EXISTS compensation_type compensation_type, -- Entity missing this field
ADD COLUMN IF NOT EXISTS pay_frequency pay_frequency DEFAULT 'BI_WEEKLY', -- Entity missing this field
ADD COLUMN IF NOT EXISTS federal_tax_exemptions INTEGER DEFAULT 0, -- Entity missing this field
ADD COLUMN IF NOT EXISTS state_tax_exemptions INTEGER DEFAULT 0, -- Entity missing this field
ADD COLUMN IF NOT EXISTS additional_federal_withholding DECIMAL(10,2) DEFAULT 0, -- Entity missing this field
ADD COLUMN IF NOT EXISTS additional_state_withholding DECIMAL(10,2) DEFAULT 0, -- Entity missing this field
ADD COLUMN IF NOT EXISTS bank_account_number_encrypted VARCHAR(255), -- Entity missing this field
ADD COLUMN IF NOT EXISTS bank_account_number_encrypted_iv VARCHAR(255), -- Entity missing this field
ADD COLUMN IF NOT EXISTS bank_routing_number_encrypted VARCHAR(255), -- Entity missing this field
ADD COLUMN IF NOT EXISTS bank_routing_number_encrypted_iv VARCHAR(255), -- Entity missing this field
ADD COLUMN IF NOT EXISTS bank_name VARCHAR(100), -- Entity missing this field
ADD COLUMN IF NOT EXISTS account_type account_type DEFAULT 'CHECKING'; -- Entity missing this field 
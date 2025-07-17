-- Create earnings table
CREATE TABLE earnings (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    abbreviation VARCHAR(10),
    company_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    
    -- Unique constraint to prevent duplicate earning types per company
    UNIQUE (company_id, type)
);

-- Create indexes for better performance
CREATE INDEX idx_earnings_company_id ON earnings (company_id);
CREATE INDEX idx_earnings_active ON earnings (is_active);
CREATE INDEX idx_earnings_type ON earnings (type);

-- Insert some default earnings for existing companies
INSERT INTO earnings (type, description, abbreviation, company_id, is_active, created_at, updated_at)
SELECT 
    'Regular Pay' as type,
    'Standard hourly or salary compensation' as description,
    'REG' as abbreviation,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL;

INSERT INTO earnings (type, description, abbreviation, company_id, is_active, created_at, updated_at)
SELECT 
    'Overtime' as type,
    'Overtime compensation at premium rate' as description,
    'OT' as abbreviation,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL;

INSERT INTO earnings (type, description, abbreviation, company_id, is_active, created_at, updated_at)
SELECT 
    'Bonus' as type,
    'Performance or holiday bonuses' as description,
    'BON' as abbreviation,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL;

INSERT INTO earnings (type, description, abbreviation, company_id, is_active, created_at, updated_at)
SELECT 
    'Commission' as type,
    'Sales commission payments' as description,
    'COM' as abbreviation,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL; 
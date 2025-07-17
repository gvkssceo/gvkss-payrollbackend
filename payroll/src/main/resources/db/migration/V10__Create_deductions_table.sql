-- Create deductions table
CREATE TABLE deductions (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    abbreviation VARCHAR(10),
    amount DECIMAL(10,2),
    percentage DECIMAL(5,2),
    is_percentage BOOLEAN NOT NULL DEFAULT FALSE,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    company_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    
    -- Unique constraint to prevent duplicate deduction types per company
    UNIQUE (company_id, type)
);

-- Create indexes for better performance
CREATE INDEX idx_deductions_company_id ON deductions (company_id);
CREATE INDEX idx_deductions_active ON deductions (is_active);
CREATE INDEX idx_deductions_type ON deductions (type);
CREATE INDEX idx_deductions_required ON deductions (is_required);

-- Insert some default deductions for existing companies
INSERT INTO deductions (type, description, abbreviation, amount, is_percentage, is_required, company_id, is_active, created_at, updated_at)
SELECT 
    'Federal Income Tax' as type,
    'Federal income tax withholding' as description,
    'FIT' as abbreviation,
    0.00 as amount,
    FALSE as is_percentage,
    TRUE as is_required,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL;

INSERT INTO deductions (type, description, abbreviation, amount, is_percentage, is_required, company_id, is_active, created_at, updated_at)
SELECT 
    'State Income Tax' as type,
    'State income tax withholding' as description,
    'SIT' as abbreviation,
    0.00 as amount,
    FALSE as is_percentage,
    TRUE as is_required,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL;

INSERT INTO deductions (type, description, abbreviation, percentage, is_percentage, is_required, company_id, is_active, created_at, updated_at)
SELECT 
    'Social Security' as type,
    'Social Security tax (6.2%)' as description,
    'SS' as abbreviation,
    6.20 as percentage,
    TRUE as is_percentage,
    TRUE as is_required,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL;

INSERT INTO deductions (type, description, abbreviation, percentage, is_percentage, is_required, company_id, is_active, created_at, updated_at)
SELECT 
    'Medicare' as type,
    'Medicare tax (1.45%)' as description,
    'MED' as abbreviation,
    1.45 as percentage,
    TRUE as is_percentage,
    TRUE as is_required,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL;

INSERT INTO deductions (type, description, abbreviation, amount, is_percentage, is_required, company_id, is_active, created_at, updated_at)
SELECT 
    'Health Insurance' as type,
    'Employee health insurance contribution' as description,
    'HI' as abbreviation,
    0.00 as amount,
    FALSE as is_percentage,
    FALSE as is_required,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL;

INSERT INTO deductions (type, description, abbreviation, amount, is_percentage, is_required, company_id, is_active, created_at, updated_at)
SELECT 
    '401(k) Contribution' as type,
    'Employee 401(k) retirement contribution' as description,
    '401K' as abbreviation,
    0.00 as amount,
    FALSE as is_percentage,
    FALSE as is_required,
    c.id as company_id,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM companies c
WHERE c.id IS NOT NULL; 
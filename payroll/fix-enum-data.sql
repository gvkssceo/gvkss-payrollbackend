-- Check for problematic enum data
SELECT 'enrollment_data' as table_name, id, enrollment_step, contact_email 
FROM enrollment_data 
WHERE enrollment_step IS NULL OR enrollment_step NOT IN ('INITIAL', 'COMPANY_INFO', 'PLAN_SELECTION', 'ACCOUNT_CREATION', 'COMPLETED');

SELECT 'companies' as table_name, id, status, email 
FROM companies 
WHERE status IS NULL OR status NOT IN ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED');

SELECT 'users' as table_name, id, status, email 
FROM users 
WHERE status IS NULL OR status NOT IN ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED');

-- Fix any null enrollment_step values
UPDATE enrollment_data 
SET enrollment_step = 'INITIAL' 
WHERE enrollment_step IS NULL;

-- Fix any null company status values
UPDATE companies 
SET status = 'PENDING' 
WHERE status IS NULL;

-- Fix any null user status values
UPDATE users 
SET status = 'PENDING' 
WHERE status IS NULL; 
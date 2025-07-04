-- Change PostgreSQL enums to VARCHAR for better Hibernate compatibility

-- Drop existing enum columns and recreate as VARCHAR with constraints
ALTER TABLE enrollment_data ALTER COLUMN enrollment_step TYPE VARCHAR(50);
ALTER TABLE enrollment_data ADD CONSTRAINT chk_enrollment_step CHECK (enrollment_step IN ('INITIAL', 'COMPANY_INFO', 'PLAN_SELECTION', 'ACCOUNT_CREATION', 'COMPLETED'));

ALTER TABLE companies ALTER COLUMN status TYPE VARCHAR(50);
ALTER TABLE companies ADD CONSTRAINT chk_company_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED'));

ALTER TABLE companies ALTER COLUMN subscription_status TYPE VARCHAR(50);
ALTER TABLE companies ADD CONSTRAINT chk_subscription_status CHECK (subscription_status IN ('ACTIVE', 'TRIAL', 'EXPIRED', 'CANCELLED'));

ALTER TABLE users ALTER COLUMN user_type TYPE VARCHAR(50);
ALTER TABLE users ADD CONSTRAINT chk_user_type CHECK (user_type IN ('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'EMPLOYEE'));

ALTER TABLE users ALTER COLUMN status TYPE VARCHAR(50);
ALTER TABLE users ADD CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED'));

ALTER TABLE employees ALTER COLUMN status TYPE VARCHAR(50);
ALTER TABLE employees ADD CONSTRAINT chk_employee_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'TERMINATED', 'INCOMPLETE'));

ALTER TABLE employees ALTER COLUMN employee_type TYPE VARCHAR(50);
ALTER TABLE employees ADD CONSTRAINT chk_employee_type CHECK (employee_type IN ('EMPLOYEE', 'CONTRACTOR', 'INTERN'));

ALTER TABLE employees ALTER COLUMN compensation_type TYPE VARCHAR(50);
ALTER TABLE employees ADD CONSTRAINT chk_compensation_type CHECK (compensation_type IN ('HOURLY', 'SALARY', 'COMMISSION'));

ALTER TABLE employees ALTER COLUMN pay_frequency TYPE VARCHAR(50);
ALTER TABLE employees ADD CONSTRAINT chk_pay_frequency CHECK (pay_frequency IN ('WEEKLY', 'BI_WEEKLY', 'SEMI_MONTHLY', 'MONTHLY'));

ALTER TABLE employees ALTER COLUMN account_type TYPE VARCHAR(50);
ALTER TABLE employees ADD CONSTRAINT chk_account_type CHECK (account_type IN ('CHECKING', 'SAVINGS'));

ALTER TABLE company_subscriptions ALTER COLUMN status TYPE VARCHAR(50);
ALTER TABLE company_subscriptions ADD CONSTRAINT chk_subscription_status_2 CHECK (status IN ('ACTIVE', 'TRIAL', 'EXPIRED', 'CANCELLED'));

ALTER TABLE company_subscriptions ALTER COLUMN billing_cycle TYPE VARCHAR(50);
ALTER TABLE company_subscriptions ADD CONSTRAINT chk_billing_cycle CHECK (billing_cycle IN ('MONTHLY', 'YEARLY'));

ALTER TABLE employees ALTER COLUMN tax_filing_status TYPE VARCHAR(50);
ALTER TABLE employees ADD CONSTRAINT chk_tax_filing_status CHECK (tax_filing_status IN ('SINGLE', 'MARRIED', 'HEAD_OF_HOUSEHOLD', 'WIDOWED'));

-- Update default values
UPDATE enrollment_data SET enrollment_step = 'INITIAL' WHERE enrollment_step IS NULL;
UPDATE companies SET status = 'PENDING' WHERE status IS NULL;
UPDATE companies SET subscription_status = 'TRIAL' WHERE subscription_status IS NULL;
UPDATE users SET status = 'PENDING' WHERE status IS NULL;
UPDATE employees SET status = 'INCOMPLETE' WHERE status IS NULL;
UPDATE employees SET pay_frequency = 'BI_WEEKLY' WHERE pay_frequency IS NULL;
UPDATE employees SET account_type = 'CHECKING' WHERE account_type IS NULL;
UPDATE employees SET tax_filing_status = 'SINGLE' WHERE tax_filing_status IS NULL; 
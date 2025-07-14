-- Fix database schema mismatch
-- This migration ensures all tables exist before running subsequent migrations

-- Create enum types if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_type') THEN
        CREATE TYPE user_type AS ENUM ('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'EMPLOYEE');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_status') THEN
        CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'company_status') THEN
        CREATE TYPE company_status AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'subscription_status') THEN
        CREATE TYPE subscription_status AS ENUM ('ACTIVE', 'TRIAL', 'EXPIRED', 'CANCELLED');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'enrollment_step') THEN
        CREATE TYPE enrollment_step AS ENUM ('INITIAL', 'COMPANY_INFO', 'PLAN_SELECTION', 'ACCOUNT_CREATION', 'COMPLETED');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'employee_type') THEN
        CREATE TYPE employee_type AS ENUM ('EMPLOYEE', 'CONTRACTOR', 'INTERN');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'employee_status') THEN
        CREATE TYPE employee_status AS ENUM ('ACTIVE', 'INACTIVE');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'compensation_type') THEN
        CREATE TYPE compensation_type AS ENUM ('HOURLY', 'SALARY', 'COMMISSION');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'pay_frequency') THEN
        CREATE TYPE pay_frequency AS ENUM ('WEEKLY', 'BI_WEEKLY', 'SEMI_MONTHLY', 'MONTHLY');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'billing_cycle') THEN
        CREATE TYPE billing_cycle AS ENUM ('MONTHLY', 'YEARLY');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'account_type') THEN
        CREATE TYPE account_type AS ENUM ('CHECKING', 'SAVINGS');
    END IF;
END $$;

-- Create plans table if it doesn't exist
CREATE TABLE IF NOT EXISTS plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    monthly_price DECIMAL(10,2) NOT NULL,
    yearly_price DECIMAL(10,2),
    max_employees INTEGER NOT NULL,
    features JSONB NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create companies table if it doesn't exist
CREATE TABLE IF NOT EXISTS companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'USA',
    ein VARCHAR(255),
    ein_encrypted BOOLEAN DEFAULT TRUE,
    status company_status DEFAULT 'PENDING',
    subscription_status subscription_status DEFAULT 'TRIAL',
    trial_ends_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    custom_fields JSONB DEFAULT '{}'
);

-- Create users table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_type user_type NOT NULL,
    status user_status DEFAULT 'PENDING',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    company_id BIGINT REFERENCES companies(id),
    last_login_at TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP,
    remember_me BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    custom_fields JSONB DEFAULT '{}'
);

-- Create company_subscriptions table if it doesn't exist
CREATE TABLE IF NOT EXISTS company_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    status subscription_status DEFAULT 'TRIAL',
    billing_cycle billing_cycle DEFAULT 'MONTHLY',
    start_date DATE NOT NULL,
    end_date DATE,
    monthly_price DECIMAL(10,2) NOT NULL,
    next_billing_date DATE,
    auto_renew BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create enrollment_data table if it doesn't exist
CREATE TABLE IF NOT EXISTS enrollment_data (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT REFERENCES companies(id),
    user_id BIGINT REFERENCES users(id),
    enrollment_step enrollment_step DEFAULT 'INITIAL',
    company_name VARCHAR(255),
    contact_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    selected_plan_id BIGINT REFERENCES plans(id),
    plan_selected_before_login BOOLEAN DEFAULT FALSE,
    enrollment_completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    custom_fields JSONB DEFAULT '{}'
);

-- Create employees table if it doesn't exist
CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    employee_id VARCHAR(50),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    employee_type employee_type NOT NULL,
    status employee_status DEFAULT 'ACTIVE',
    ssn_encrypted VARCHAR(255),
    ssn_encrypted_iv VARCHAR(255),
    date_of_birth DATE,
    job_title VARCHAR(100),
    department VARCHAR(100),
    hire_date DATE,
    termination_date DATE,
    compensation_type compensation_type NOT NULL,
    hourly_rate DECIMAL(10,2),
    salary DECIMAL(12,2),
    pay_frequency pay_frequency DEFAULT 'BI_WEEKLY',
    federal_tax_exemptions INTEGER DEFAULT 0,
    state_tax_exemptions INTEGER DEFAULT 0,
    additional_federal_withholding DECIMAL(10,2) DEFAULT 0,
    additional_state_withholding DECIMAL(10,2) DEFAULT 0,
    bank_account_number_encrypted VARCHAR(255),
    bank_account_number_encrypted_iv VARCHAR(255),
    bank_routing_number_encrypted VARCHAR(255),
    bank_routing_number_encrypted_iv VARCHAR(255),
    bank_name VARCHAR(100),
    account_type account_type DEFAULT 'CHECKING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    custom_fields JSONB DEFAULT '{}'
);

-- Create audit_logs table if it doesn't exist
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    company_id BIGINT REFERENCES companies(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add foreign key constraints if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_company_subscriptions_company') THEN
        ALTER TABLE company_subscriptions ADD CONSTRAINT fk_company_subscriptions_company FOREIGN KEY (company_id) REFERENCES companies(id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_company_subscriptions_plan') THEN
        ALTER TABLE company_subscriptions ADD CONSTRAINT fk_company_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans(id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_employees_company') THEN
        ALTER TABLE employees ADD CONSTRAINT fk_employees_company FOREIGN KEY (company_id) REFERENCES companies(id);
    END IF;
END $$;

-- Create indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_company_id ON users(company_id);
CREATE INDEX IF NOT EXISTS idx_companies_email ON companies(email);
CREATE INDEX IF NOT EXISTS idx_company_subscriptions_company ON company_subscriptions(company_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_data_email ON enrollment_data(contact_email);
CREATE INDEX IF NOT EXISTS idx_employees_company ON employees(company_id);

-- Insert sample plans data if not exists
INSERT INTO plans (name, display_name, description, monthly_price, yearly_price, max_employees, features, is_active, is_featured, sort_order) 
SELECT 'BASIC', 'Basic', 'Full payroll processing for small businesses', 39.00, 390.00, 5, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)"]', true, false, 1
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'BASIC');

INSERT INTO plans (name, display_name, description, monthly_price, yearly_price, max_employees, features, is_active, is_featured, sort_order) 
SELECT 'STANDARD', 'Standard', 'Complete payroll solution for growing businesses', 99.00, 990.00, 50, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)", "Benefits administration", "Employee self-service portal", "Custom reporting tools", "Priority chat support (24hr)", "W-2/1099 preparation"]', true, true, 2
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'STANDARD');

INSERT INTO plans (name, display_name, description, monthly_price, yearly_price, max_employees, features, is_active, is_featured, sort_order) 
SELECT 'PREMIUM', 'Premium', 'Enterprise payroll solution with dedicated support', 199.00, 1990.00, 999999, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)", "Benefits administration", "Employee self-service portal", "Custom reporting tools", "Priority chat support (24hr)", "W-2/1099 preparation", "Dedicated payroll specialist", "HR compliance auditing", "Advanced analytics dashboard", "API & accounting integrations", "24/7 phone support"]', true, false, 3
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'PREMIUM'); 
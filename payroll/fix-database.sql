-- Fix database schema by converting PostgreSQL enums to VARCHAR
-- Run this script in your PostgreSQL database

-- Drop existing tables to start fresh
DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS enrollment_data CASCADE;
DROP TABLE IF EXISTS company_subscriptions CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS companies CASCADE;
DROP TABLE IF EXISTS plans CASCADE;

-- Drop enum types
DROP TYPE IF EXISTS user_type CASCADE;
DROP TYPE IF EXISTS user_status CASCADE;
DROP TYPE IF EXISTS company_status CASCADE;
DROP TYPE IF EXISTS subscription_status CASCADE;
DROP TYPE IF EXISTS enrollment_step CASCADE;
DROP TYPE IF EXISTS employee_type CASCADE;
DROP TYPE IF EXISTS employee_status CASCADE;
DROP TYPE IF EXISTS compensation_type CASCADE;
DROP TYPE IF EXISTS pay_frequency CASCADE;
DROP TYPE IF EXISTS billing_cycle CASCADE;
DROP TYPE IF EXISTS account_type CASCADE;

-- Create tables with VARCHAR columns instead of enums
CREATE TABLE plans (
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

CREATE TABLE companies (
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
    status VARCHAR(50) DEFAULT 'PENDING',
    subscription_status VARCHAR(50) DEFAULT 'TRIAL',
    trial_ends_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    custom_fields JSONB DEFAULT '{}',
    CONSTRAINT uk_companies_email UNIQUE (email),
    CONSTRAINT chk_companies_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_company_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED')),
    CONSTRAINT chk_subscription_status CHECK (subscription_status IN ('ACTIVE', 'TRIAL', 'EXPIRED', 'CANCELLED'))
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
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
    custom_fields JSONB DEFAULT '{}',
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_user_type CHECK (user_type IN ('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'EMPLOYEE')),
    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED'))
);

CREATE TABLE company_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    plan_id BIGINT NOT NULL REFERENCES plans(id),
    status VARCHAR(50) DEFAULT 'TRIAL',
    billing_cycle VARCHAR(50) DEFAULT 'MONTHLY',
    start_date DATE NOT NULL,
    end_date DATE,
    monthly_price DECIMAL(10,2) NOT NULL,
    next_billing_date DATE,
    auto_renew BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_subscriptions_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_company_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans(id),
    CONSTRAINT chk_subscription_status_2 CHECK (status IN ('ACTIVE', 'TRIAL', 'EXPIRED', 'CANCELLED')),
    CONSTRAINT chk_billing_cycle CHECK (billing_cycle IN ('MONTHLY', 'YEARLY'))
);

CREATE TABLE enrollment_data (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT REFERENCES companies(id),
    user_id BIGINT REFERENCES users(id),
    enrollment_step VARCHAR(50) DEFAULT 'INITIAL',
    company_name VARCHAR(255),
    contact_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    selected_plan_id BIGINT REFERENCES plans(id),
    plan_selected_before_login BOOLEAN DEFAULT FALSE,
    enrollment_completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    custom_fields JSONB DEFAULT '{}',
    CONSTRAINT fk_enrollment_data_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_enrollment_data_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_enrollment_data_plan FOREIGN KEY (selected_plan_id) REFERENCES plans(id),
    CONSTRAINT chk_enrollment_step CHECK (enrollment_step IN ('INITIAL', 'COMPANY_INFO', 'PLAN_SELECTION', 'ACCOUNT_CREATION', 'COMPLETED'))
);

CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    employee_id VARCHAR(50),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    employee_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    ssn_encrypted VARCHAR(255),
    ssn_encrypted_iv VARCHAR(255),
    date_of_birth DATE,
    job_title VARCHAR(100),
    department VARCHAR(100),
    hire_date DATE,
    termination_date DATE,
    compensation_type VARCHAR(50) NOT NULL,
    hourly_rate DECIMAL(10,2),
    salary DECIMAL(12,2),
    pay_frequency VARCHAR(50) DEFAULT 'BI_WEEKLY',
    federal_tax_exemptions INTEGER DEFAULT 0,
    state_tax_exemptions INTEGER DEFAULT 0,
    additional_federal_withholding DECIMAL(10,2) DEFAULT 0,
    additional_state_withholding DECIMAL(10,2) DEFAULT 0,
    bank_account_number_encrypted VARCHAR(255),
    bank_account_number_encrypted_iv VARCHAR(255),
    bank_routing_number_encrypted VARCHAR(255),
    bank_routing_number_encrypted_iv VARCHAR(255),
    bank_name VARCHAR(100),
    account_type VARCHAR(50) DEFAULT 'CHECKING',
    hourly_rate_2 DECIMAL(10,2),
    salary_2 DECIMAL(12,2),
    standard_hours DECIMAL(5,2) DEFAULT 40.00,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(20),
    phone_2 VARCHAR(20),
    email_2 VARCHAR(255),
    tax_filing_status VARCHAR(50) DEFAULT 'SINGLE',
    dependents INTEGER DEFAULT 0,
    is_exempt BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    custom_fields JSONB DEFAULT '{}',
    CONSTRAINT fk_employees_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT uk_employees_company_ssn UNIQUE (company_id, ssn_encrypted),
    CONSTRAINT chk_employees_email CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_employee_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_employee_type CHECK (employee_type IN ('EMPLOYEE', 'CONTRACTOR', 'INTERN')),
    CONSTRAINT chk_compensation_type CHECK (compensation_type IN ('HOURLY', 'SALARY', 'COMMISSION')),
    CONSTRAINT chk_pay_frequency CHECK (pay_frequency IN ('WEEKLY', 'BI_WEEKLY', 'SEMI_MONTHLY', 'MONTHLY')),
    CONSTRAINT chk_account_type CHECK (account_type IN ('CHECKING', 'SAVINGS'))
);

CREATE TABLE audit_logs (
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

-- Insert sample plans
INSERT INTO plans (name, display_name, description, monthly_price, yearly_price, max_employees, features, is_active, is_featured, sort_order) VALUES
('BASIC', 'Basic', 'Full payroll processing for small businesses', 39.00, 390.00, 5, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)"]', true, false, 1),
('STANDARD', 'Standard', 'Complete payroll solution for growing businesses', 99.00, 990.00, 50, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)", "Benefits administration", "Employee self-service portal", "Custom reporting tools", "Priority chat support (24hr)", "W-2/1099 preparation"]', true, true, 2),
('PREMIUM', 'Premium', 'Enterprise payroll solution with dedicated support', 199.00, 1990.00, 999999, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)", "Benefits administration", "Employee self-service portal", "Custom reporting tools", "Priority chat support (24hr)", "W-2/1099 preparation", "Dedicated payroll specialist", "HR compliance auditing", "Advanced analytics dashboard", "API & accounting integrations", "24/7 phone support"]', true, false, 3); 
-- Create ENUM types
CREATE TYPE user_type AS ENUM ('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'EMPLOYEE');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED');
CREATE TYPE company_status AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED');
CREATE TYPE subscription_status AS ENUM ('ACTIVE', 'TRIAL', 'EXPIRED', 'CANCELLED');
CREATE TYPE enrollment_step AS ENUM ('INITIAL', 'COMPANY_INFO', 'PLAN_SELECTION', 'ACCOUNT_CREATION', 'COMPLETED');
CREATE TYPE employee_type AS ENUM ('EMPLOYEE', 'CONTRACTOR', 'INTERN');
CREATE TYPE employee_status AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE compensation_type AS ENUM ('HOURLY', 'SALARY', 'COMMISSION');
CREATE TYPE pay_frequency AS ENUM ('WEEKLY', 'BI_WEEKLY', 'SEMI_MONTHLY', 'MONTHLY');
CREATE TYPE billing_cycle AS ENUM ('MONTHLY', 'YEARLY');
CREATE TYPE account_type AS ENUM ('CHECKING', 'SAVINGS');

-- 1. Companies Table (Hybrid: Static Core + Dynamic Custom Fields)
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    -- Static Core Fields (Required for Payroll)
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
    ein VARCHAR(255), -- Encrypted, required for tax filing
    ein_encrypted BOOLEAN DEFAULT TRUE,
    status company_status DEFAULT 'PENDING',
    subscription_status subscription_status DEFAULT 'TRIAL',
    trial_ends_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    -- Dynamic Custom Fields (Flexible Business Settings)
    custom_fields JSONB DEFAULT '{}',
    
    CONSTRAINT uk_companies_email UNIQUE (email),
    CONSTRAINT chk_companies_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- 2. Users Table (Hybrid: Static Core + Dynamic Custom Fields)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    -- Static Core Fields (Required for Authentication & Payroll)
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- BCrypt
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
    
    -- Dynamic Custom Fields (User Preferences & Settings)
    custom_fields JSONB DEFAULT '{}',
    
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- 3. Plans Table (Static - Business Rules Don't Change)
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

-- 4. Company Subscriptions Table (Static - Business Rules)
CREATE TABLE company_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    plan_id BIGINT NOT NULL REFERENCES plans(id),
    status subscription_status DEFAULT 'TRIAL',
    billing_cycle billing_cycle DEFAULT 'MONTHLY',
    start_date DATE NOT NULL,
    end_date DATE,
    monthly_price DECIMAL(10,2) NOT NULL,
    next_billing_date DATE,
    auto_renew BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_company_subscriptions_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_company_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans(id)
);

-- 5. Enrollment Data Table (Hybrid: Static Core + Dynamic Custom Fields)
CREATE TABLE enrollment_data (
    id BIGSERIAL PRIMARY KEY,
    -- Static Core Fields (Required for Enrollment Process)
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
    
    -- Dynamic Custom Fields (Additional Enrollment Data)
    custom_fields JSONB DEFAULT '{}',
    
    CONSTRAINT fk_enrollment_data_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_enrollment_data_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_enrollment_data_plan FOREIGN KEY (selected_plan_id) REFERENCES plans(id)
);

-- 6. Employees Table (Hybrid: Static Core + Dynamic Custom Fields)
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    -- Static Core Fields (Required for Payroll Processing)
    company_id BIGINT NOT NULL REFERENCES companies(id),
    employee_id VARCHAR(50), -- Company's internal employee ID
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    employee_type employee_type NOT NULL,
    status employee_status DEFAULT 'ACTIVE',
    
    -- Encrypted Sensitive Data (Static - Security Requirements)
    ssn_encrypted VARCHAR(255),
    ssn_encrypted_iv VARCHAR(255),
    date_of_birth DATE,
    
    -- Employment Details (Static - Payroll Requirements)
    job_title VARCHAR(100),
    department VARCHAR(100),
    hire_date DATE,
    termination_date DATE,
    
    -- Compensation (Static - Payroll Calculations)
    compensation_type compensation_type NOT NULL,
    hourly_rate DECIMAL(10,2),
    salary DECIMAL(12,2),
    pay_frequency pay_frequency DEFAULT 'BI_WEEKLY',
    
    -- Tax Information (Static - Tax Filing Requirements)
    federal_tax_exemptions INTEGER DEFAULT 0,
    state_tax_exemptions INTEGER DEFAULT 0,
    additional_federal_withholding DECIMAL(10,2) DEFAULT 0,
    additional_state_withholding DECIMAL(10,2) DEFAULT 0,
    
    -- Direct Deposit (Static - Payment Processing)
    bank_account_number_encrypted VARCHAR(255),
    bank_account_number_encrypted_iv VARCHAR(255),
    bank_routing_number_encrypted VARCHAR(255),
    bank_routing_number_encrypted_iv VARCHAR(255),
    bank_name VARCHAR(100),
    account_type account_type DEFAULT 'CHECKING',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    -- Dynamic Custom Fields (Flexible Employee Data)
    custom_fields JSONB DEFAULT '{}',
    
    CONSTRAINT fk_employees_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT uk_employees_company_ssn UNIQUE (company_id, ssn_encrypted),
    CONSTRAINT chk_employees_email CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- 7. Audit Log Table (Static - Compliance Requirements)
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

-- Create Indexes for Performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_company_id ON users(company_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_custom_fields ON users USING GIN (custom_fields);

CREATE INDEX idx_companies_email ON companies(email);
CREATE INDEX idx_companies_status ON companies(status);
CREATE INDEX idx_companies_subscription_status ON companies(subscription_status);
CREATE INDEX idx_companies_custom_fields ON companies USING GIN (custom_fields);

CREATE INDEX idx_plans_active ON plans(is_active);
CREATE INDEX idx_plans_sort_order ON plans(sort_order);
CREATE INDEX idx_plans_name ON plans(name);

CREATE INDEX idx_company_subscriptions_company ON company_subscriptions(company_id);
CREATE INDEX idx_company_subscriptions_status ON company_subscriptions(status);
CREATE INDEX idx_company_subscriptions_plan ON company_subscriptions(plan_id);

CREATE INDEX idx_enrollment_data_email ON enrollment_data(contact_email);
CREATE INDEX idx_enrollment_data_step ON enrollment_data(enrollment_step);
CREATE INDEX idx_enrollment_data_company ON enrollment_data(company_id);
CREATE INDEX idx_enrollment_data_custom_fields ON enrollment_data USING GIN (custom_fields);

CREATE INDEX idx_employees_company ON employees(company_id);
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_employees_employee_type ON employees(employee_type);
CREATE INDEX idx_employees_custom_fields ON employees USING GIN (custom_fields);

CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_company ON audit_logs(company_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

-- Insert Sample Plans Data
INSERT INTO plans (name, display_name, description, monthly_price, yearly_price, max_employees, features, is_active, is_featured, sort_order) VALUES
('BASIC', 'Basic', 'Full payroll processing for small businesses', 39.00, 390.00, 5, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)"]', true, false, 1),
('STANDARD', 'Standard', 'Complete payroll solution for growing businesses', 99.00, 990.00, 50, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)", "Benefits administration", "Employee self-service portal", "Custom reporting tools", "Priority chat support (24hr)", "W-2/1099 preparation"]', true, true, 2),
('PREMIUM', 'Premium', 'Enterprise payroll solution with dedicated support', 199.00, 1990.00, 999999, '["Full payroll processing", "Unlimited direct deposits", "Federal & state tax filings", "Basic compliance reporting", "Email support (72hr response)", "Benefits administration", "Employee self-service portal", "Custom reporting tools", "Priority chat support (24hr)", "W-2/1099 preparation", "Dedicated payroll specialist", "HR compliance auditing", "Advanced analytics dashboard", "API & accounting integrations", "24/7 phone support"]', true, false, 3);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_companies_updated_at BEFORE UPDATE ON companies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_plans_updated_at BEFORE UPDATE ON plans FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_company_subscriptions_updated_at BEFORE UPDATE ON company_subscriptions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_enrollment_data_updated_at BEFORE UPDATE ON enrollment_data FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_employees_updated_at BEFORE UPDATE ON employees FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample custom fields examples
INSERT INTO companies (name, email, custom_fields) VALUES 
('Sample Corp', 'sample@example.com', '{
  "industry": "technology",
  "company_size": "small",
  "payroll_frequency": "bi_weekly",
  "timezone": "America/Chicago",
  "holiday_schedule": {
    "christmas": true,
    "thanksgiving": true
  }
}');

INSERT INTO users (email, password_hash, user_type, first_name, last_name, custom_fields) VALUES 
('admin@sample.com', '$2a$12$dummy.hash.for.example', 'BUSINESS_OWNER', 'John', 'Doe', '{
  "preferred_language": "en",
  "timezone": "America/Chicago",
  "notification_preferences": {
    "email": true,
    "sms": false
  },
  "ui_preferences": {
    "theme": "light",
    "dashboard_layout": "standard"
  }
}'); 
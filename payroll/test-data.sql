-- Test Data for Texas Payroll Backend
-- Run this after the application starts successfully

-- Insert test plans
INSERT INTO plans (name, display_name, description, monthly_price, yearly_price, max_employees, features, is_active, is_featured, sort_order) 
VALUES 
  ('Basic', 'Basic Plan', 'Basic payroll features for small businesses', 39.00, 390.00, 5, '["Full payroll processing","Unlimited direct deposits","Federal & state tax filings","Basic compliance reporting","Email support (72hr response)"]', true, false, 1),
  ('Standard', 'Standard Plan', 'Advanced payroll features for growing businesses', 99.00, 990.00, 50, '["Full payroll processing","Unlimited direct deposits","Federal & state tax filings","Basic compliance reporting","Email support (72hr response)","Benefits administration","Employee self-service portal","Custom reporting tools","Priority chat support (24hr)","W-2/1099 preparation"]', true, true, 2),
  ('Premium', 'Premium Plan', 'Complete payroll solution for large organizations', 199.00, 1990.00, 999999, '["Full payroll processing","Unlimited direct deposits","Federal & state tax filings","Basic compliance reporting","Email support (72hr response)","Benefits administration","Employee self-service portal","Custom reporting tools","Priority chat support (24hr)","W-2/1099 preparation","Dedicated payroll specialist","HR compliance auditing","Advanced analytics dashboard","API & accounting integrations","24/7 phone support"]', true, false, 3);

-- Insert test company
INSERT INTO companies (name, legal_name, email, phone, address_line1, city, state, zip_code, country, status, subscription_status) 
VALUES ('Test Company Inc', 'Test Company Incorporated', 'admin@testcompany.com', '555-123-4567', '123 Main St', 'Austin', 'TX', '78701', 'USA', 'ACTIVE', 'ACTIVE');

-- Insert test user (password: test123 - no encoding since we removed Spring Security)
INSERT INTO users (email, password_hash, user_type, status, first_name, last_name, phone, company_id) 
VALUES ('admin@testcompany.com', 'test123', 'BUSINESS_OWNER', 'ACTIVE', 'John', 'Doe', '555-123-4567', 1);

-- Insert test employee
INSERT INTO employees (company_id, employee_id, first_name, last_name, email, phone, employee_type, status, job_title, department, compensation_type, hourly_rate, hire_date) 
VALUES (1, 'EMP001', 'Jane', 'Smith', 'jane.smith@testcompany.com', '555-987-6543', 'EMPLOYEE', 'ACTIVE', 'Software Developer', 'Engineering', 'HOURLY', 25.00, '2024-01-15');

-- Insert another test employee
INSERT INTO employees (company_id, employee_id, first_name, last_name, email, phone, employee_type, status, job_title, department, compensation_type, salary, hire_date) 
VALUES (1, 'EMP002', 'Mike', 'Johnson', 'mike.johnson@testcompany.com', '555-456-7890', 'EMPLOYEE', 'ACTIVE', 'Manager', 'Management', 'SALARY', 75000.00, '2023-06-01'); 
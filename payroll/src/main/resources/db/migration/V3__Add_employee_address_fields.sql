-- Add missing address fields to employees table
ALTER TABLE employees 
ADD COLUMN address_line1 VARCHAR(255),
ADD COLUMN address_line2 VARCHAR(255),
ADD COLUMN city VARCHAR(100),
ADD COLUMN state VARCHAR(50),
ADD COLUMN zip_code VARCHAR(20); 
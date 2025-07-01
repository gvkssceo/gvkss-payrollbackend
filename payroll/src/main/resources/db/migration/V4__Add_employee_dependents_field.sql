-- Add missing dependents field to employees table
ALTER TABLE employees 
ADD COLUMN dependents INTEGER DEFAULT 0; 
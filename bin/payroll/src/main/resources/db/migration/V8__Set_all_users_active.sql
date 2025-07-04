-- Set all existing users to ACTIVE status
UPDATE users SET status = 'ACTIVE' WHERE status IS NULL OR status != 'ACTIVE';

-- Set all existing companies to ACTIVE status
UPDATE companies SET status = 'ACTIVE' WHERE status IS NULL OR status != 'ACTIVE'; 
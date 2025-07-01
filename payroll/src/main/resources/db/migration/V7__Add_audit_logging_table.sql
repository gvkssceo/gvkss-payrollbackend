-- Drop existing audit_logs table and recreate with enhanced schema
DROP TABLE IF EXISTS audit_logs CASCADE;

-- Create enhanced audit logging table for security and compliance
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(36) NOT NULL, -- UUID for event tracking
    event_type VARCHAR(50) NOT NULL, -- AUTHENTICATION, REGISTRATION, PASSWORD_CHANGE, etc.
    user_id BIGINT REFERENCES users(id),
    company_id BIGINT REFERENCES companies(id),
    email VARCHAR(255),
    ip_address INET,
    user_agent TEXT,
    endpoint VARCHAR(255),
    http_method VARCHAR(10),
    status_code INTEGER,
    execution_time_ms BIGINT,
    success BOOLEAN DEFAULT TRUE,
    failure_reason TEXT,
    additional_data JSONB DEFAULT '{}',
    severity VARCHAR(20) DEFAULT 'INFO', -- INFO, WARN, ERROR, CRITICAL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_audit_logs_event_type CHECK (event_type IN (
        'AUTHENTICATION', 'REGISTRATION', 'PASSWORD_CHANGE', 'LOGOUT', 
        'FAILED_LOGIN', 'ACCOUNT_LOCKOUT', 'SENSITIVE_DATA_ACCESS', 
        'SUSPICIOUS_ACTIVITY', 'API_ACCESS', 'DATA_EXPORT', 
        'CONFIGURATION_CHANGE', 'POLICY_VIOLATION', 'PERFORMANCE_ISSUE'
    )),
    CONSTRAINT chk_audit_logs_severity CHECK (severity IN ('INFO', 'WARN', 'ERROR', 'CRITICAL'))
);

-- Create indexes for efficient querying
CREATE INDEX idx_audit_logs_event_id ON audit_logs(event_id);
CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_company_id ON audit_logs(company_id);
CREATE INDEX idx_audit_logs_email ON audit_logs(email);
CREATE INDEX idx_audit_logs_ip_address ON audit_logs(ip_address);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_severity ON audit_logs(severity);
CREATE INDEX idx_audit_logs_success ON audit_logs(success);

-- Create composite indexes for common query patterns
CREATE INDEX idx_audit_logs_user_created ON audit_logs(user_id, created_at);
CREATE INDEX idx_audit_logs_company_created ON audit_logs(company_id, created_at);
CREATE INDEX idx_audit_logs_type_created ON audit_logs(event_type, created_at);
CREATE INDEX idx_audit_logs_severity_created ON audit_logs(severity, created_at);

-- Create GIN index for JSONB additional_data field
CREATE INDEX idx_audit_logs_additional_data ON audit_logs USING GIN (additional_data);

-- Add comments for documentation
COMMENT ON TABLE audit_logs IS 'Comprehensive audit logging table for security and compliance tracking';
COMMENT ON COLUMN audit_logs.event_id IS 'Unique UUID for event tracking and correlation';
COMMENT ON COLUMN audit_logs.event_type IS 'Type of security event (AUTHENTICATION, REGISTRATION, etc.)';
COMMENT ON COLUMN audit_logs.user_id IS 'Reference to the user who triggered the event';
COMMENT ON COLUMN audit_logs.company_id IS 'Reference to the company associated with the event';
COMMENT ON COLUMN audit_logs.email IS 'Email address for events where user_id is not available';
COMMENT ON COLUMN audit_logs.ip_address IS 'IP address of the request for security tracking';
COMMENT ON COLUMN audit_logs.user_agent IS 'User agent string for request identification';
COMMENT ON COLUMN audit_logs.endpoint IS 'API endpoint accessed (for API_ACCESS events)';
COMMENT ON COLUMN audit_logs.http_method IS 'HTTP method used (for API_ACCESS events)';
COMMENT ON COLUMN audit_logs.status_code IS 'HTTP status code returned (for API_ACCESS events)';
COMMENT ON COLUMN audit_logs.execution_time_ms IS 'Execution time in milliseconds (for performance events)';
COMMENT ON COLUMN audit_logs.success IS 'Whether the event was successful';
COMMENT ON COLUMN audit_logs.failure_reason IS 'Reason for failure if success is false';
COMMENT ON COLUMN audit_logs.additional_data IS 'Additional event-specific data in JSON format';
COMMENT ON COLUMN audit_logs.severity IS 'Severity level of the event (INFO, WARN, ERROR, CRITICAL)';
COMMENT ON COLUMN audit_logs.created_at IS 'Timestamp when the event occurred';

-- Create a function to automatically clean old audit logs (for compliance retention)
CREATE OR REPLACE FUNCTION cleanup_old_audit_logs()
RETURNS void AS $$
BEGIN
    -- Keep audit logs for 7 years for compliance (adjust as needed)
    DELETE FROM audit_logs 
    WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '7 years';
    
    -- Log the cleanup operation
    INSERT INTO audit_logs (
        event_id, event_type, email, ip_address, user_agent,
        endpoint, http_method, status_code, success, 
        additional_data, severity, created_at
    ) VALUES (
        gen_random_uuid()::text, 'CONFIGURATION_CHANGE', 'system@texaspayroll.com',
        '127.0.0.1', 'System Cleanup Job', '/audit/cleanup', 'SYSTEM', 200,
        true, '{"operation": "cleanup_old_audit_logs", "retention_years": 7}'::jsonb,
        'INFO', CURRENT_TIMESTAMP
    );
END;
$$ LANGUAGE plpgsql;

-- Create a scheduled job to run cleanup (requires pg_cron extension)
-- Note: This requires the pg_cron extension to be installed
-- SELECT cron.schedule('cleanup-audit-logs', '0 2 * * 0', 'SELECT cleanup_old_audit_logs();');

-- Create a view for common audit queries
CREATE VIEW audit_summary AS
SELECT 
    event_type,
    severity,
    DATE_TRUNC('day', created_at) as event_date,
    COUNT(*) as event_count,
    COUNT(CASE WHEN success = false THEN 1 END) as failure_count,
    AVG(execution_time_ms) as avg_execution_time_ms,
    MAX(execution_time_ms) as max_execution_time_ms
FROM audit_logs
GROUP BY event_type, severity, DATE_TRUNC('day', created_at)
ORDER BY event_date DESC, event_type, severity;

-- Create a view for security incidents
CREATE VIEW security_incidents AS
SELECT 
    event_id,
    event_type,
    email,
    ip_address,
    user_agent,
    failure_reason,
    severity,
    created_at
FROM audit_logs
WHERE severity IN ('ERROR', 'CRITICAL') 
   OR event_type IN ('FAILED_LOGIN', 'ACCOUNT_LOCKOUT', 'SUSPICIOUS_ACTIVITY', 'POLICY_VIOLATION')
   OR success = false
ORDER BY created_at DESC;

-- Add comments to views
COMMENT ON VIEW audit_summary IS 'Summary view of audit events by type, severity, and date';
COMMENT ON VIEW security_incidents IS 'View of security-related incidents and failures'; 
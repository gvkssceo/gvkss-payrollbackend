package com.payroll.texas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for logging security-related events and audit trails.
 * Provides comprehensive security monitoring and compliance logging.
 */
@Service
public class SecurityAuditService {
    
    private static final Logger logger = LoggerFactory.getLogger("com.payroll.texas.security");
    
    /**
     * Logs a user authentication event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param success whether authentication was successful
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     * @param reason failure reason if authentication failed
     */
    public void logAuthenticationEvent(String email, Long userId, boolean success, 
                                     String ipAddress, String userAgent, String reason) {
        String eventId = UUID.randomUUID().toString();
        String status = success ? "SUCCESS" : "FAILED";
        
        logger.info("Authentication event: eventId={}, email={}, userId={}, status={}, ipAddress={}, userAgent={}, reason={}, timestamp={}", 
                   eventId, email, userId, status, ipAddress, userAgent, reason, LocalDateTime.now());
        
        if (!success) {
            logger.warn("Authentication failure: eventId={}, email={}, ipAddress={}, reason={}", 
                       eventId, email, ipAddress, reason);
        }
    }
    
    /**
     * Logs a user registration event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param companyId the company ID
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     */
    public void logRegistrationEvent(String email, Long userId, Long companyId, 
                                   String ipAddress, String userAgent) {
        String eventId = UUID.randomUUID().toString();
        
        logger.info("Registration event: eventId={}, email={}, userId={}, companyId={}, ipAddress={}, userAgent={}, timestamp={}", 
                   eventId, email, userId, companyId, ipAddress, userAgent, LocalDateTime.now());
    }
    
    /**
     * Logs a password change event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     * @param changeType the type of password change (reset, update, etc.)
     */
    public void logPasswordChangeEvent(String email, Long userId, String ipAddress, 
                                     String userAgent, String changeType) {
        String eventId = UUID.randomUUID().toString();
        
        logger.info("Password change event: eventId={}, email={}, userId={}, changeType={}, ipAddress={}, userAgent={}, timestamp={}", 
                   eventId, email, userId, changeType, ipAddress, userAgent, LocalDateTime.now());
    }
    
    /**
     * Logs a user logout event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     * @param sessionDuration the session duration in seconds
     */
    public void logLogoutEvent(String email, Long userId, String ipAddress, 
                             String userAgent, long sessionDuration) {
        String eventId = UUID.randomUUID().toString();
        
        logger.info("Logout event: eventId={}, email={}, userId={}, ipAddress={}, userAgent={}, sessionDuration={}s, timestamp={}", 
                   eventId, email, userId, ipAddress, userAgent, sessionDuration, LocalDateTime.now());
    }
    
    /**
     * Logs a failed login attempt.
     * 
     * @param email the attempted email
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     * @param reason the reason for failure
     * @param attemptCount the current attempt count
     */
    public void logFailedLoginAttempt(String email, String ipAddress, String userAgent, 
                                    String reason, int attemptCount) {
        String eventId = UUID.randomUUID().toString();
        
        logger.warn("Failed login attempt: eventId={}, email={}, ipAddress={}, userAgent={}, reason={}, attemptCount={}, timestamp={}", 
                   eventId, email, ipAddress, userAgent, reason, attemptCount, LocalDateTime.now());
        
        // Log potential brute force attacks
        if (attemptCount >= 5) {
            logger.error("Potential brute force attack detected: email={}, ipAddress={}, attemptCount={}", 
                        email, ipAddress, attemptCount);
        }
    }
    
    /**
     * Logs an account lockout event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param ipAddress the IP address of the request
     * @param lockoutReason the reason for lockout
     * @param lockoutDuration the lockout duration in minutes
     */
    public void logAccountLockoutEvent(String email, Long userId, String ipAddress, 
                                     String lockoutReason, int lockoutDuration) {
        String eventId = UUID.randomUUID().toString();
        
        logger.error("Account lockout: eventId={}, email={}, userId={}, ipAddress={}, reason={}, duration={}min, timestamp={}", 
                    eventId, email, userId, ipAddress, lockoutReason, lockoutDuration, LocalDateTime.now());
    }
    
    /**
     * Logs a sensitive data access event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param dataType the type of sensitive data accessed (SSN, bank account, etc.)
     * @param action the action performed (view, update, delete)
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     */
    public void logSensitiveDataAccess(String email, Long userId, String dataType, 
                                     String action, String ipAddress, String userAgent) {
        String eventId = UUID.randomUUID().toString();
        
        logger.info("Sensitive data access: eventId={}, email={}, userId={}, dataType={}, action={}, ipAddress={}, userAgent={}, timestamp={}", 
                   eventId, email, userId, dataType, action, ipAddress, userAgent, LocalDateTime.now());
    }
    
    /**
     * Logs a suspicious activity event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param activityType the type of suspicious activity
     * @param description the description of the activity
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     * @param severity the severity level (LOW, MEDIUM, HIGH, CRITICAL)
     */
    public void logSuspiciousActivity(String email, Long userId, String activityType, 
                                    String description, String ipAddress, String userAgent, String severity) {
        String eventId = UUID.randomUUID().toString();
        
        switch (severity.toUpperCase()) {
            case "CRITICAL":
                logger.error("Suspicious activity (CRITICAL): eventId={}, email={}, userId={}, activityType={}, description={}, ipAddress={}, userAgent={}, timestamp={}", 
                           eventId, email, userId, activityType, description, ipAddress, userAgent, LocalDateTime.now());
                break;
            case "HIGH":
                logger.error("Suspicious activity (HIGH): eventId={}, email={}, userId={}, activityType={}, description={}, ipAddress={}, userAgent={}, timestamp={}", 
                           eventId, email, userId, activityType, description, ipAddress, userAgent, LocalDateTime.now());
                break;
            case "MEDIUM":
                logger.warn("Suspicious activity (MEDIUM): eventId={}, email={}, userId={}, activityType={}, description={}, ipAddress={}, userAgent={}, timestamp={}", 
                          eventId, email, userId, activityType, description, ipAddress, userAgent, LocalDateTime.now());
                break;
            default:
                logger.info("Suspicious activity (LOW): eventId={}, email={}, userId={}, activityType={}, description={}, ipAddress={}, userAgent={}, timestamp={}", 
                          eventId, email, userId, activityType, description, ipAddress, userAgent, LocalDateTime.now());
        }
    }
    
    /**
     * Logs an API access event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param endpoint the API endpoint accessed
     * @param method the HTTP method used
     * @param statusCode the HTTP status code returned
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     * @param executionTime the execution time in milliseconds
     */
    public void logApiAccess(String email, Long userId, String endpoint, String method, 
                           int statusCode, String ipAddress, String userAgent, long executionTime) {
        String eventId = UUID.randomUUID().toString();
        
        logger.info("API access: eventId={}, email={}, userId={}, endpoint={}, method={}, statusCode={}, ipAddress={}, userAgent={}, executionTime={}ms, timestamp={}", 
                   eventId, email, userId, endpoint, method, statusCode, ipAddress, userAgent, executionTime, LocalDateTime.now());
        
        // Log failed API calls
        if (statusCode >= 400) {
            logger.warn("Failed API call: eventId={}, email={}, endpoint={}, method={}, statusCode={}, ipAddress={}", 
                       eventId, email, endpoint, method, statusCode, ipAddress);
        }
    }
    
    /**
     * Logs a data export event.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param exportType the type of data exported
     * @param recordCount the number of records exported
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     */
    public void logDataExport(String email, Long userId, String exportType, 
                            int recordCount, String ipAddress, String userAgent) {
        String eventId = UUID.randomUUID().toString();
        
        logger.info("Data export: eventId={}, email={}, userId={}, exportType={}, recordCount={}, ipAddress={}, userAgent={}, timestamp={}", 
                   eventId, email, userId, exportType, recordCount, ipAddress, userAgent, LocalDateTime.now());
    }
    
    /**
     * Logs a system configuration change.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param configType the type of configuration changed
     * @param oldValue the old configuration value
     * @param newValue the new configuration value
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     */
    public void logConfigurationChange(String email, Long userId, String configType, 
                                     String oldValue, String newValue, String ipAddress, String userAgent) {
        String eventId = UUID.randomUUID().toString();
        
        logger.info("Configuration change: eventId={}, email={}, userId={}, configType={}, oldValue={}, newValue={}, ipAddress={}, userAgent={}, timestamp={}", 
                   eventId, email, userId, configType, oldValue, newValue, ipAddress, userAgent, LocalDateTime.now());
    }
    
    /**
     * Logs a security policy violation.
     * 
     * @param email the user's email
     * @param userId the user's ID
     * @param policyType the type of policy violated
     * @param violation the description of the violation
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     * @param severity the severity level
     */
    public void logPolicyViolation(String email, Long userId, String policyType, 
                                 String violation, String ipAddress, String userAgent, String severity) {
        String eventId = UUID.randomUUID().toString();
        
        logger.error("Policy violation: eventId={}, email={}, userId={}, policyType={}, violation={}, ipAddress={}, userAgent={}, severity={}, timestamp={}", 
                    eventId, email, userId, policyType, violation, ipAddress, userAgent, severity, LocalDateTime.now());
    }
} 
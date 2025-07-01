package com.payroll.texas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service for handling password encryption and validation using BCrypt.
 * Provides industry-standard password security with configurable strength.
 */
@Service
public class PasswordService {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public PasswordService() {
        // BCrypt with strength 12 (industry standard for 2024)
        // This provides 2^12 = 4096 iterations, making it computationally expensive to crack
        this.passwordEncoder = new BCryptPasswordEncoder(12, new SecureRandom());
    }
    
    /**
     * Encrypts a plain text password using BCrypt.
     * 
     * @param plainPassword the plain text password to encrypt
     * @return the encrypted password hash
     * @throws IllegalArgumentException if password is null or empty
     */
    public String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            logger.error("Attempted to encrypt null or empty password");
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            String encryptedPassword = passwordEncoder.encode(plainPassword);
            logger.debug("Password encrypted successfully for user");
            return encryptedPassword;
        } catch (Exception e) {
            logger.error("Failed to encrypt password: {}", e.getMessage(), e);
            throw new RuntimeException("Password encryption failed", e);
        }
    }
    
    /**
     * Validates a plain text password against an encrypted hash.
     * 
     * @param plainPassword the plain text password to validate
     * @param encryptedPassword the encrypted password hash to compare against
     * @return true if passwords match, false otherwise
     */
    public boolean validatePassword(String plainPassword, String encryptedPassword) {
        if (plainPassword == null || encryptedPassword == null) {
            logger.warn("Password validation attempted with null values");
            return false;
        }
        
        try {
            boolean isValid = passwordEncoder.matches(plainPassword, encryptedPassword);
            if (isValid) {
                logger.debug("Password validation successful");
            } else {
                logger.warn("Password validation failed - incorrect password");
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Password validation error: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Checks if a password meets minimum security requirements.
     * 
     * @param password the password to validate
     * @return true if password meets requirements, false otherwise
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Check for at least one uppercase letter
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        // Check for at least one lowercase letter
        boolean hasLowerCase = password.matches(".*[a-z].*");
        // Check for at least one digit
        boolean hasDigit = password.matches(".*\\d.*");
        // Check for at least one special character
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
    
    /**
     * Generates a secure random password.
     * 
     * @param length the length of the password to generate
     * @return a secure random password
     */
    public String generateSecurePassword(int length) {
        if (length < 8) {
            length = 12; // Minimum secure length
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(chars.charAt(random.nextInt(26))); // Uppercase
        password.append(chars.charAt(26 + random.nextInt(26))); // Lowercase
        password.append(chars.charAt(52 + random.nextInt(10))); // Digit
        password.append(chars.charAt(62 + random.nextInt(chars.length() - 62))); // Special
        
        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        String generatedPassword = new String(passwordArray);
        logger.debug("Generated secure password of length {}", length);
        return generatedPassword;
    }
    
    /**
     * Gets the BCrypt strength used by this encoder.
     * 
     * @return the BCrypt strength (log rounds)
     */
    public int getBCryptStrength() {
        return 12; // Current strength setting
    }
} 
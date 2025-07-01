package com.payroll.texas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PasswordService to verify BCrypt implementation.
 */
class PasswordServiceTest {
    
    private PasswordService passwordService;
    
    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }
    
    @Test
    @DisplayName("Should encrypt password successfully")
    void testEncryptPassword() {
        String plainPassword = "TestPassword123!";
        
        String encryptedPassword = passwordService.encryptPassword(plainPassword);
        
        assertNotNull(encryptedPassword);
        assertNotEquals(plainPassword, encryptedPassword);
        assertTrue(encryptedPassword.startsWith("$2a$12$")); // BCrypt with strength 12
    }
    
    @Test
    @DisplayName("Should validate correct password")
    void testValidatePasswordCorrect() {
        String plainPassword = "TestPassword123!";
        String encryptedPassword = passwordService.encryptPassword(plainPassword);
        
        boolean isValid = passwordService.validatePassword(plainPassword, encryptedPassword);
        
        assertTrue(isValid);
    }
    
    @Test
    @DisplayName("Should reject incorrect password")
    void testValidatePasswordIncorrect() {
        String plainPassword = "TestPassword123!";
        String wrongPassword = "WrongPassword123!";
        String encryptedPassword = passwordService.encryptPassword(plainPassword);
        
        boolean isValid = passwordService.validatePassword(wrongPassword, encryptedPassword);
        
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("Should handle null password")
    void testEncryptPasswordNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.encryptPassword(null);
        });
    }
    
    @Test
    @DisplayName("Should handle empty password")
    void testEncryptPasswordEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.encryptPassword("");
        });
    }
    
    @Test
    @DisplayName("Should validate password strength correctly")
    void testPasswordStrength() {
        // Strong password
        assertTrue(passwordService.isPasswordStrong("StrongPass123!"));
        
        // Weak passwords
        assertFalse(passwordService.isPasswordStrong("weak")); // Too short
        assertFalse(passwordService.isPasswordStrong("weakpassword")); // No uppercase, digit, special
        assertFalse(passwordService.isPasswordStrong("WeakPassword")); // No digit, special
        assertFalse(passwordService.isPasswordStrong("WeakPassword123")); // No special
        assertFalse(passwordService.isPasswordStrong("weakpassword123!")); // No uppercase
    }
    
    @Test
    @DisplayName("Should generate secure password")
    void testGenerateSecurePassword() {
        String generatedPassword = passwordService.generateSecurePassword(12);
        
        assertNotNull(generatedPassword);
        assertEquals(12, generatedPassword.length());
        assertTrue(passwordService.isPasswordStrong(generatedPassword));
    }
    
    @Test
    @DisplayName("Should generate minimum length password when requested length is too short")
    void testGenerateSecurePasswordMinimumLength() {
        String generatedPassword = passwordService.generateSecurePassword(4);
        
        assertNotNull(generatedPassword);
        assertEquals(12, generatedPassword.length()); // Minimum secure length
        assertTrue(passwordService.isPasswordStrong(generatedPassword));
    }
    
    @Test
    @DisplayName("Should return correct BCrypt strength")
    void testGetBCryptStrength() {
        assertEquals(12, passwordService.getBCryptStrength());
    }
    
    @Test
    @DisplayName("Should handle null values in password validation")
    void testValidatePasswordNullValues() {
        assertFalse(passwordService.validatePassword(null, "someHash"));
        assertFalse(passwordService.validatePassword("somePassword", null));
        assertFalse(passwordService.validatePassword(null, null));
    }
    
    @Test
    @DisplayName("Should produce different hashes for same password")
    void testPasswordHashUniqueness() {
        String plainPassword = "TestPassword123!";
        
        String hash1 = passwordService.encryptPassword(plainPassword);
        String hash2 = passwordService.encryptPassword(plainPassword);
        
        // BCrypt should produce different hashes due to salt
        assertNotEquals(hash1, hash2);
        
        // Both should validate correctly
        assertTrue(passwordService.validatePassword(plainPassword, hash1));
        assertTrue(passwordService.validatePassword(plainPassword, hash2));
    }
} 
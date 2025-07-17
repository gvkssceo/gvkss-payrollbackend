package com.payroll.texas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EncryptionService {
    
    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);
    
    @Value("${app.encryption.key}")
    private String encryptionKey;
    
    @Value("${app.encryption.algorithm}")
    private String algorithm;
    
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
    /**
     * Encrypts plaintext using AES-256-GCM
     * @param plaintext The text to encrypt
     * @return Base64 encoded encrypted data with IV
     */
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return null;
        }
        
        try {
            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            
            // Create secret key
            SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), "AES");
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(algorithm);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
            
            // Encrypt
            byte[] encryptedData = cipher.doFinal(plaintext.getBytes());
            
            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    /**
     * Decrypts encrypted data using AES-256-GCM
     * @param encryptedData Base64 encoded encrypted data with IV
     * @return Decrypted plaintext
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return null;
        }
        
        try {
            // Decode from Base64
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            
            // Extract IV and encrypted data
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);
            
            // Create secret key
            SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), "AES");
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(algorithm);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
            
            // Decrypt
            byte[] decryptedData = cipher.doFinal(encrypted);
            
            return new String(decryptedData);
            
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    /**
     * Encrypts SSN with special handling
     * @param ssn The SSN to encrypt
     * @return Encrypted SSN
     */
    public String encryptSSN(String ssn) {
        if (ssn == null || ssn.isEmpty()) {
            return null;
        }
        
        // Remove any non-digit characters
        String cleanSSN = ssn.replaceAll("[^0-9]", "");
        logger.info("DEBUG: Decrypted SSN received: '{}' Cleaned: '{}'", ssn, cleanSSN);
        
        // Validate SSN format (9 digits)
        if (cleanSSN.length() != 9) {
            throw new IllegalArgumentException("SSN must be 9 digits");
        }
        
        return encrypt(cleanSSN);
    }
    
    /**
     * Decrypts SSN
     * @param encryptedSSN The encrypted SSN
     * @return Decrypted SSN
     */
    public String decryptSSN(String encryptedSSN) {
        return decrypt(encryptedSSN);
    }
    
    /**
     * Encrypts bank account number
     * @param accountNumber The account number to encrypt
     * @return Encrypted account number
     */
    public String encryptBankAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return null;
        }
        
        // Remove any non-digit characters
        String cleanAccount = accountNumber.replaceAll("[^0-9]", "");
        
        return encrypt(cleanAccount);
    }
    
    /**
     * Decrypts bank account number
     * @param encryptedAccount The encrypted account number
     * @return Decrypted account number
     */
    public String decryptBankAccount(String encryptedAccount) {
        return decrypt(encryptedAccount);
    }
    
    /**
     * Encrypts routing number
     * @param routingNumber The routing number to encrypt
     * @return Encrypted routing number
     */
    public String encryptRoutingNumber(String routingNumber) {
        if (routingNumber == null || routingNumber.isEmpty()) {
            return null;
        }
        
        // Remove any non-digit characters
        String cleanRouting = routingNumber.replaceAll("[^0-9]", "");
        
        // Validate routing number format (9 digits)
        if (cleanRouting.length() != 9) {
            throw new IllegalArgumentException("Routing number must be 9 digits");
        }
        
        return encrypt(cleanRouting);
    }
    
    /**
     * Decrypts routing number
     * @param encryptedRouting The encrypted routing number
     * @return Decrypted routing number
     */
    public String decryptRoutingNumber(String encryptedRouting) {
        return decrypt(encryptedRouting);
    }
    
    /**
     * Encrypts EIN (Employer Identification Number)
     * @param ein The EIN to encrypt
     * @return Encrypted EIN
     */
    public String encryptEIN(String ein) {
        if (ein == null || ein.isEmpty()) {
            return null;
        }
        
        // Remove any non-digit characters
        String cleanEIN = ein.replaceAll("[^0-9]", "");
        
        // Validate EIN format (9 digits)
        if (cleanEIN.length() != 9) {
            throw new IllegalArgumentException("EIN must be 9 digits");
        }
        
        return encrypt(cleanEIN);
    }
    
    /**
     * Decrypts EIN
     * @param encryptedEIN The encrypted EIN
     * @return Decrypted EIN
     */
    public String decryptEIN(String encryptedEIN) {
        return decrypt(encryptedEIN);
    }
} 
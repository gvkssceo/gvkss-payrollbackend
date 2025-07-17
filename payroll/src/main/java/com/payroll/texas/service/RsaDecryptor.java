package com.payroll.texas.service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsaDecryptor {
    private static final Logger logger = LoggerFactory.getLogger(RsaDecryptor.class);
    private final PrivateKey privateKey;

    public RsaDecryptor(String privateKeyPem) throws Exception {
        String privateKeyPEM = privateKeyPem
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.privateKey = kf.generatePrivate(keySpec);
    }

    public String decrypt(String encrypted) throws Exception {
        logger.info("Attempting to decrypt. First 20 chars: {} (length: {})", encrypted == null ? "null" : encrypted.substring(0, Math.min(20, encrypted.length())), encrypted == null ? 0 : encrypted.length());
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = null;
        // Try base64
        try {
            encryptedBytes = Base64.getDecoder().decode(encrypted);
            logger.info("Base64 decoding succeeded.");
        } catch (IllegalArgumentException e1) {
            logger.warn("Base64 decoding failed: {}. Trying hex...", e1.getMessage());
            // Try hex
            try {
                encryptedBytes = hexStringToByteArray(encrypted);
                logger.info("Hex decoding succeeded.");
            } catch (Exception e2) {
                logger.warn("Hex decoding failed: {}. Trying raw bytes...", e2.getMessage());
                // Fallback: raw bytes (ISO-8859-1)
                encryptedBytes = encrypted.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
                logger.info("Using raw bytes (ISO-8859-1).");
            }
        }
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
} 
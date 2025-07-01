package com.payroll.texas.service;

import com.payroll.texas.dto.auth.LoginRequest;
import com.payroll.texas.dto.auth.LoginResponse;
import com.payroll.texas.model.User;
import com.payroll.texas.model.UserStatus;
import com.payroll.texas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordService passwordService;
    
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmailAndNotDeleted(loginRequest.getEmail())
                .orElse(null);
        
        if (user == null) {
            logger.warn("Login failed - user not found for email: {}", loginRequest.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
        
        // Check if user is active
        if (!user.isActive()) {
            logger.warn("Login failed - inactive account for email: {}", loginRequest.getEmail());
            throw new RuntimeException("Account is not active");
        }
        
        // Check if account is locked
        if (user.isLocked()) {
            logger.warn("Login failed - account locked for email: {}", loginRequest.getEmail());
            throw new RuntimeException("Account is temporarily locked. Please try again later.");
        }
        
        // Validate password using BCrypt
        if (!passwordService.validatePassword(loginRequest.getPassword(), user.getPasswordHash())) {
            // Increment failed login attempts
            user.incrementFailedLoginAttempts();
            userRepository.save(user);
            
            logger.warn("Login failed - invalid password for email: {}", loginRequest.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
        
        // Reset failed login attempts on successful login
        user.resetFailedLoginAttempts();
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        logger.info("Login successful for user: {} (ID: {})", user.getEmail(), user.getId());
        
        // Generate tokens
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("userType", user.getUserType().name());
        claims.put("companyId", user.getCompany() != null ? user.getCompany().getId() : null);
        
        String accessToken = jwtService.generateToken(claims, user.getEmail(), 86400000L);
        String refreshToken = jwtService.generateToken(new HashMap<>(), user.getEmail(), 604800000L);
        
        // Create user info
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getUserType(),
            user.getCompany() != null ? user.getCompany().getId() : null,
            user.getCompany() != null ? user.getCompany().getName() : null
        );
        
        return new LoginResponse(accessToken, refreshToken, 86400L, userInfo);
    }
    
    public String refreshToken(String refreshToken) {
        logger.debug("Token refresh attempt");
        
        if (jwtService.validateToken(refreshToken)) {
            String email = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmailAndNotDeleted(email)
                    .orElseThrow(() -> {
                        logger.warn("Token refresh failed - user not found for email: {}", email);
                        return new RuntimeException("User not found");
                    });
            
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("userType", user.getUserType().name());
            claims.put("companyId", user.getCompany() != null ? user.getCompany().getId() : null);
            
            logger.debug("Token refreshed successfully for user: {}", email);
            return jwtService.generateToken(claims, user.getEmail(), 86400000L);
        }
        
        logger.warn("Token refresh failed - invalid refresh token");
        throw new RuntimeException("Invalid refresh token");
    }
    
    public void logout(String token) {
        logger.debug("Logout attempt");
        
        // In a real application, you might want to blacklist the token
        // For now, we'll just validate that the token exists
        if (!jwtService.validateToken(token)) {
            logger.warn("Logout failed - invalid token");
            throw new RuntimeException("Invalid token");
        }
        
        logger.info("User logged out successfully");
    }
    
    public boolean validateToken(String token) {
        boolean isValid = jwtService.validateToken(token);
        if (!isValid) {
            logger.debug("Token validation failed");
        }
        return isValid;
    }
} 
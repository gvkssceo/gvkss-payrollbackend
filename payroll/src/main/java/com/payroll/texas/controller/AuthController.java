package com.payroll.texas.controller;

import com.payroll.texas.dto.auth.LoginRequest;
import com.payroll.texas.dto.auth.LoginResponse;
import com.payroll.texas.dto.auth.SignupRequest;
import com.payroll.texas.dto.auth.SignupResponse;
import com.payroll.texas.service.AuthService;
import com.payroll.texas.service.SignupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private SignupService signupService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Create error response
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setMessage("Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            SignupResponse response = signupService.signup(signupRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Create error response
            SignupResponse errorResponse = new SignupResponse();
            errorResponse.setMessage("Signup failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String refreshToken = authHeader.substring(7); // Remove "Bearer "
            String newAccessToken = authService.refreshToken(refreshToken);
            
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token refresh failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            authService.logout(token);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully logged out");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Logout failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> response = authService.validateTokenAndGetUser(token);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/signup/validate-email")
    public ResponseEntity<Map<String, Object>> validateEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();
        
        if (email == null || email.trim().isEmpty()) {
            response.put("valid", false);
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Basic email validation
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            response.put("valid", false);
            response.put("message", "Invalid email format");
            return ResponseEntity.badRequest().body(response);
        }
        
        response.put("valid", true);
        response.put("message", "Email format is valid");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/validate-password")
    public ResponseEntity<Map<String, Object>> validatePassword(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        Map<String, Object> response = new HashMap<>();
        
        if (password == null || password.trim().isEmpty()) {
            response.put("valid", false);
            response.put("message", "Password is required");
            response.put("strength", "None");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Password strength validation
        int score = 0;
        String strength = "Weak";
        
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[^A-Za-z0-9].*")) score++;
        
        if (score >= 4) strength = "Strong";
        else if (score >= 2) strength = "Medium";
        else strength = "Weak";
        
        boolean isValid = password.length() >= 8;
        
        response.put("valid", isValid);
        response.put("strength", strength);
        response.put("score", score);
        response.put("message", isValid ? "Password is valid" : "Password must be at least 8 characters");
        
        return ResponseEntity.ok(response);
    }
} 
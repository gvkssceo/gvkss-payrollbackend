package com.payroll.texas.dto.auth;

import com.payroll.texas.model.UserType;

import java.time.LocalDateTime;

public class SignupResponse {
    
    private String message;
    private UserInfo userInfo;
    private CompanyInfo companyInfo;
    private LocalDateTime createdAt;
    
    // Constructors
    public SignupResponse() {}
    
    public SignupResponse(String message, UserInfo userInfo, CompanyInfo companyInfo) {
        this.message = message;
        this.userInfo = userInfo;
        this.companyInfo = companyInfo;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public UserInfo getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }
    
    public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Inner class for user information
    public static class UserInfo {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private UserType userType;
        private String status;
        
        // Constructors
        public UserInfo() {}
        
        public UserInfo(Long id, String email, String firstName, String lastName, UserType userType, String status) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.userType = userType;
            this.status = status;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public UserType getUserType() {
            return userType;
        }
        
        public void setUserType(UserType userType) {
            this.userType = userType;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
    
    // Inner class for company information
    public static class CompanyInfo {
        private Long id;
        private String name;
        private String email;
        private String status;
        private String subscriptionStatus;
        
        // Constructors
        public CompanyInfo() {}
        
        public CompanyInfo(Long id, String name, String email, String status, String subscriptionStatus) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.status = status;
            this.subscriptionStatus = subscriptionStatus;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getSubscriptionStatus() {
            return subscriptionStatus;
        }
        
        public void setSubscriptionStatus(String subscriptionStatus) {
            this.subscriptionStatus = subscriptionStatus;
        }
    }
} 
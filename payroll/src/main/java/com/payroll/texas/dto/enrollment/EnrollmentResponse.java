package com.payroll.texas.dto.enrollment;

import com.payroll.texas.model.EnrollmentStep;

import java.time.LocalDateTime;

public class EnrollmentResponse {
    
    private String message;
    private Long enrollmentId;
    private EnrollmentStep currentStep;
    private EnrollmentData enrollmentData;
    private LocalDateTime createdAt;
    
    // Constructors
    public EnrollmentResponse() {}
    
    public EnrollmentResponse(String message, Long enrollmentId, EnrollmentStep currentStep, EnrollmentData enrollmentData) {
        this.message = message;
        this.enrollmentId = enrollmentId;
        this.currentStep = currentStep;
        this.enrollmentData = enrollmentData;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Long getEnrollmentId() {
        return enrollmentId;
    }
    
    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    
    public EnrollmentStep getCurrentStep() {
        return currentStep;
    }
    
    public void setCurrentStep(EnrollmentStep currentStep) {
        this.currentStep = currentStep;
    }
    
    public EnrollmentData getEnrollmentData() {
        return enrollmentData;
    }
    
    public void setEnrollmentData(EnrollmentData enrollmentData) {
        this.enrollmentData = enrollmentData;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Inner class for enrollment data
    public static class EnrollmentData {
        private String companyName;
        private String contactName;
        private String contactEmail;
        private String contactPhone;
        private String selectedPlan;
        private Boolean planSelectedBeforeLogin;
        private String customFields;
        
        // Constructors
        public EnrollmentData() {}
        
        public EnrollmentData(String companyName, String contactName, String contactEmail, String contactPhone) {
            this.companyName = companyName;
            this.contactName = contactName;
            this.contactEmail = contactEmail;
            this.contactPhone = contactPhone;
        }
        
        // Getters and Setters
        public String getCompanyName() {
            return companyName;
        }
        
        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }
        
        public String getContactName() {
            return contactName;
        }
        
        public void setContactName(String contactName) {
            this.contactName = contactName;
        }
        
        public String getContactEmail() {
            return contactEmail;
        }
        
        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }
        
        public String getContactPhone() {
            return contactPhone;
        }
        
        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }
        
        public String getSelectedPlan() {
            return selectedPlan;
        }
        
        public void setSelectedPlan(String selectedPlan) {
            this.selectedPlan = selectedPlan;
        }
        
        public Boolean getPlanSelectedBeforeLogin() {
            return planSelectedBeforeLogin;
        }
        
        public void setPlanSelectedBeforeLogin(Boolean planSelectedBeforeLogin) {
            this.planSelectedBeforeLogin = planSelectedBeforeLogin;
        }
        
        public String getCustomFields() {
            return customFields;
        }
        
        public void setCustomFields(String customFields) {
            this.customFields = customFields;
        }
    }
} 
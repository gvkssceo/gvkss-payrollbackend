package com.payroll.texas.dto.enrollment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EnrollmentRequest {
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Contact name is required")
    private String contactName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    @NotBlank(message = "Phone is required")
    private String contactPhone;
    
    // Optional fields
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country = "USA";
    
    // Plan selection (optional during enrollment)
    private String selectedPlan;
    private Boolean planSelectedBeforeLogin = false;
    
    // Custom fields for additional data
    private String customFields;
    
    // Constructors
    public EnrollmentRequest() {}
    
    public EnrollmentRequest(String companyName, String contactName, String contactEmail, String contactPhone) {
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
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
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
    
    @Override
    public String toString() {
        return "EnrollmentRequest{" +
                "companyName='" + companyName + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", selectedPlan='" + selectedPlan + '\'' +
                '}';
    }
} 
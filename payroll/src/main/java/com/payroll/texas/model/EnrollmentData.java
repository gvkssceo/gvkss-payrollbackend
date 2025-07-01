package com.payroll.texas.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment_data")
public class EnrollmentData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_step")
    private EnrollmentStep enrollmentStep = EnrollmentStep.INITIAL;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "contact_name")
    private String contactName;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_plan_id")
    private Plan selectedPlan;
    
    @Column(name = "plan_selected_before_login", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean planSelectedBeforeLogin = false;
    
    @Column(name = "enrollment_completed_at")
    private LocalDateTime enrollmentCompletedAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    // Dynamic custom fields (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb DEFAULT '{}'")
    private String customFields = "{}";
    
    // Constructors
    public EnrollmentData() {}
    
    public EnrollmentData(String companyName, String contactName, String contactEmail, String contactPhone) {
        this.companyName = companyName;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Company getCompany() {
        return company;
    }
    
    public void setCompany(Company company) {
        this.company = company;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public EnrollmentStep getEnrollmentStep() {
        return enrollmentStep;
    }
    
    public void setEnrollmentStep(EnrollmentStep enrollmentStep) {
        this.enrollmentStep = enrollmentStep;
    }
    
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
    
    public Plan getSelectedPlan() {
        return selectedPlan;
    }
    
    public void setSelectedPlan(Plan selectedPlan) {
        this.selectedPlan = selectedPlan;
    }
    
    public Boolean getPlanSelectedBeforeLogin() {
        return planSelectedBeforeLogin;
    }
    
    public void setPlanSelectedBeforeLogin(Boolean planSelectedBeforeLogin) {
        this.planSelectedBeforeLogin = planSelectedBeforeLogin;
    }
    
    public LocalDateTime getEnrollmentCompletedAt() {
        return enrollmentCompletedAt;
    }
    
    public void setEnrollmentCompletedAt(LocalDateTime enrollmentCompletedAt) {
        this.enrollmentCompletedAt = enrollmentCompletedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCustomFields() {
        return customFields;
    }
    
    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }
    
    public boolean isCompleted() {
        return enrollmentStep == EnrollmentStep.COMPLETED;
    }
    
    public boolean hasPlanSelected() {
        return selectedPlan != null;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "EnrollmentData{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", enrollmentStep=" + enrollmentStep +
                '}';
    }
} 
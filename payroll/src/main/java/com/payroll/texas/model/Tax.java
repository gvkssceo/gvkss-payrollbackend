package com.payroll.texas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "taxes")
public class Tax {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", nullable = false)
    private TaxType taxType;
    
    @Column(name = "tax_level", nullable = false)
    private String taxLevel; // FEDERAL, STATE, LOCAL
    
    @Column(name = "state_code", length = 2)
    private String stateCode;
    
    @Column(name = "tax_id", length = 50)
    private String taxId;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "deposit_frequency", length = 50)
    private String depositFrequency;
    
    @Column(name = "rate", precision = 5, scale = 4)
    private BigDecimal rate;
    
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    
    @Column(name = "status", length = 50)
    private String status;
    
    @Column(name = "needs_action")
    private Boolean needsAction = false;
    
    @Column(name = "action_required", length = 200)
    private String actionRequired;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Tax() {}
    
    public Tax(TaxType taxType, String taxLevel, Company company) {
        this.taxType = taxType;
        this.taxLevel = taxLevel;
        this.company = company;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TaxType getTaxType() {
        return taxType;
    }
    
    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }
    
    public String getTaxLevel() {
        return taxLevel;
    }
    
    public void setTaxLevel(String taxLevel) {
        this.taxLevel = taxLevel;
    }
    
    public String getStateCode() {
        return stateCode;
    }
    
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
    
    public String getTaxId() {
        return taxId;
    }
    
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDepositFrequency() {
        return depositFrequency;
    }
    
    public void setDepositFrequency(String depositFrequency) {
        this.depositFrequency = depositFrequency;
    }
    
    public BigDecimal getRate() {
        return rate;
    }
    
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Boolean getNeedsAction() {
        return needsAction;
    }
    
    public void setNeedsAction(Boolean needsAction) {
        this.needsAction = needsAction;
    }
    
    public String getActionRequired() {
        return actionRequired;
    }
    
    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }
    
    public Company getCompany() {
        return company;
    }
    
    public void setCompany(Company company) {
        this.company = company;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    @Override
    public String toString() {
        return "Tax{" +
                "id=" + id +
                ", taxType=" + taxType +
                ", taxLevel='" + taxLevel + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", taxId='" + taxId + '\'' +
                ", description='" + description + '\'' +
                ", companyId=" + (company != null ? company.getId() : null) +
                ", isActive=" + isActive +
                '}';
    }
} 
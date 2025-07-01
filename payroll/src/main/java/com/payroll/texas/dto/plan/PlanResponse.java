package com.payroll.texas.dto.plan;

import java.math.BigDecimal;
import java.util.List;

public class PlanResponse {
    
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private BigDecimal monthlyPrice;
    private BigDecimal yearlyPrice;
    private Integer maxEmployees;
    private List<String> features;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer sortOrder;
    
    // Constructors
    public PlanResponse() {}
    
    public PlanResponse(Long id, String name, String displayName, String description, 
                       BigDecimal monthlyPrice, BigDecimal yearlyPrice, Integer maxEmployees, 
                       List<String> features, Boolean isActive, Boolean isFeatured, Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.maxEmployees = maxEmployees;
        this.features = features;
        this.isActive = isActive;
        this.isFeatured = isFeatured;
        this.sortOrder = sortOrder;
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
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }
    
    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }
    
    public BigDecimal getYearlyPrice() {
        return yearlyPrice;
    }
    
    public void setYearlyPrice(BigDecimal yearlyPrice) {
        this.yearlyPrice = yearlyPrice;
    }
    
    public Integer getMaxEmployees() {
        return maxEmployees;
    }
    
    public void setMaxEmployees(Integer maxEmployees) {
        this.maxEmployees = maxEmployees;
    }
    
    public List<String> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<String> features) {
        this.features = features;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public boolean isUnlimited() {
        return maxEmployees != null && maxEmployees >= 999999;
    }
    
    public BigDecimal getYearlyPriceWithDiscount() {
        if (yearlyPrice != null) {
            return yearlyPrice;
        }
        // Default 10% discount for yearly
        return monthlyPrice.multiply(new BigDecimal("12")).multiply(new BigDecimal("0.9"));
    }
} 
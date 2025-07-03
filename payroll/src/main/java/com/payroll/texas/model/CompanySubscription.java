package com.payroll.texas.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_subscriptions")
public class CompanySubscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;
    
    @Column(name = "monthly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    private LocalDateTime trialEndsAt;
    
    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;
    
    private String stripeSubscriptionId;
    
    private String stripeCustomerId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb DEFAULT '{}'")
    private String billingDetails = "{}";
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    private LocalDateTime cancelledAt;
    
    // Constructors
    public CompanySubscription() {}
    
    public CompanySubscription(Company company, Plan plan, BillingCycle billingCycle, BigDecimal monthlyPrice) {
        this.company = company;
        this.plan = plan;
        this.billingCycle = billingCycle;
        this.monthlyPrice = monthlyPrice;
        this.status = SubscriptionStatus.TRIAL;
        this.startDate = LocalDate.now();
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
    
    public Plan getPlan() {
        return plan;
    }
    
    public void setPlan(Plan plan) {
        this.plan = plan;
    }
    
    public SubscriptionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }
    
    public BillingCycle getBillingCycle() {
        return billingCycle;
    }
    
    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }
    
    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }
    
    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getTrialEndsAt() {
        return trialEndsAt;
    }
    
    public void setTrialEndsAt(LocalDateTime trialEndsAt) {
        this.trialEndsAt = trialEndsAt;
    }
    
    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }
    
    public void setNextBillingDate(LocalDate nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }
    
    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }
    
    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }
    
    public String getStripeCustomerId() {
        return stripeCustomerId;
    }
    
    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }
    
    public String getBillingDetails() {
        return billingDetails;
    }
    
    public void setBillingDetails(String billingDetails) {
        this.billingDetails = billingDetails;
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
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    // Business methods
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }
    
    public boolean isTrialActive() {
        return status == SubscriptionStatus.TRIAL && 
               (trialEndsAt == null || trialEndsAt.isAfter(LocalDateTime.now()));
    }
    
    public boolean isExpired() {
        return status == SubscriptionStatus.EXPIRED || 
               (endDate != null && endDate.isBefore(LocalDate.now()));
    }
    
    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        this.endDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
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
        return "CompanySubscription{" +
                "id=" + id +
                ", company=" + (company != null ? company.getName() : "null") +
                ", plan=" + (plan != null ? plan.getName() : "null") +
                ", status=" + status +
                ", billingCycle=" + billingCycle +
                ", monthlyPrice=" + monthlyPrice +
                '}';
    }
} 
package com.payroll.texas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String legalName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String phone;
    
    private String addressLine1;
    
    private String addressLine2;
    
    private String city;
    
    private String state;
    
    private String zipCode;
    
    @Column(columnDefinition = "VARCHAR(100) DEFAULT 'USA'")
    private String country = "USA";
    
    private String ein; // Encrypted
    
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean einEncrypted = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CompanyStatus status = CompanyStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.TRIAL;
    
    private LocalDateTime trialEndsAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    private LocalDateTime deletedAt;
    
    // Dynamic custom fields (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb DEFAULT '{}'")
    private String customFields = "{}";
    
    // Relationships
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Prevent circular reference during serialization
    private List<User> users = new ArrayList<>();
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Prevent circular reference during serialization
    private List<Employee> employees = new ArrayList<>();
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompanySubscription> subscriptions = new ArrayList<>();
    
    // Constructors
    public Company() {}
    
    public Company(String name, String email) {
        this.name = name;
        this.email = email;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLegalName() {
        return legalName;
    }
    
    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
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
    
    public String getEin() {
        return ein;
    }
    
    public void setEin(String ein) {
        this.ein = ein;
    }
    
    public Boolean getEinEncrypted() {
        return einEncrypted;
    }
    
    public void setEinEncrypted(Boolean einEncrypted) {
        this.einEncrypted = einEncrypted;
    }
    
    public CompanyStatus getStatus() {
        return status;
    }
    
    public void setStatus(CompanyStatus status) {
        this.status = status;
    }
    
    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }
    
    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
    
    public LocalDateTime getTrialEndsAt() {
        return trialEndsAt;
    }
    
    public void setTrialEndsAt(LocalDateTime trialEndsAt) {
        this.trialEndsAt = trialEndsAt;
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
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public String getCustomFields() {
        return customFields;
    }
    
    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }
    
    public List<User> getUsers() {
        return users;
    }
    
    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    public List<Employee> getEmployees() {
        return employees;
    }
    
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
    
    public List<CompanySubscription> getSubscriptions() {
        return subscriptions;
    }
    
    public void setSubscriptions(List<CompanySubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
    
    // Business methods
    public boolean isActive() {
        return status == CompanyStatus.ACTIVE;
    }
    
    public boolean isTrialActive() {
        return subscriptionStatus == SubscriptionStatus.TRIAL && 
               (trialEndsAt == null || trialEndsAt.isAfter(LocalDateTime.now()));
    }
    
    public boolean hasActiveSubscription() {
        return subscriptionStatus == SubscriptionStatus.ACTIVE;
    }
    
    public void addUser(User user) {
        users.add(user);
        user.setCompany(this);
    }
    
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setCompany(this);
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
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", subscriptionStatus=" + subscriptionStatus +
                '}';
    }
} 
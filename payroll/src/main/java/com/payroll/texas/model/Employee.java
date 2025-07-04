package com.payroll.texas.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true)
    private String employeeId;
    
    @Column(unique = true)
    private String ssn; // Encrypted
    
    @Column(name = "ssn_encrypted")
    private String ssnEncrypted; // Encrypted SSN data
    
    @Column(name = "ssn_encrypted_iv")
    private String ssnEncryptedIv; // Initialization vector for SSN encryption
    
    private LocalDate dateOfBirth;
    
    private LocalDate hireDate;
    
    private LocalDate terminationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type", nullable = false)
    private EmployeeType employeeType;
    
    // Employment Details (Static - Payroll Requirements)
    private String jobTitle;
    
    private String department;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "compensation_type", nullable = false)
    private CompensationType compensationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_frequency")
    private PayFrequency payFrequency = PayFrequency.BI_WEEKLY;
    
    // Tax Information (Static - Tax Filing Requirements)
    @Column(name = "federal_tax_exemptions", columnDefinition = "INTEGER DEFAULT 0")
    private Integer federalTaxExemptions = 0;
    
    @Column(name = "state_tax_exemptions", columnDefinition = "INTEGER DEFAULT 0")
    private Integer stateTaxExemptions = 0;
    
    @Column(name = "additional_federal_withholding", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal additionalFederalWithholding = BigDecimal.ZERO;
    
    @Column(name = "additional_state_withholding", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal additionalStateWithholding = BigDecimal.ZERO;
    
    // Direct Deposit (Static - Payment Processing)
    @Column(name = "bank_account_number_encrypted")
    private String bankAccountNumberEncrypted;
    
    @Column(name = "bank_account_number_encrypted_iv")
    private String bankAccountNumberEncryptedIv;
    
    @Column(name = "bank_routing_number_encrypted")
    private String bankRoutingNumberEncrypted;
    
    @Column(name = "bank_routing_number_encrypted_iv")
    private String bankRoutingNumberEncryptedIv;
    
    @Column(name = "bank_name")
    private String bankName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType = AccountType.CHECKING;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyRate;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal salary;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal standardHours = new BigDecimal("40.00");
    
    private String addressLine1;
    
    private String addressLine2;
    
    private String city;
    
    private String state;
    
    private String zipCode;
    
    private String phone;
    
    @Column(unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tax_filing_status")
    private TaxFilingStatus taxFilingStatus = TaxFilingStatus.SINGLE;
    
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer dependents = 0;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isExempt = false;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    private LocalDateTime deletedAt;
    
    // Dynamic custom fields (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb DEFAULT '{}'")
    private String customFields = "{}";
    
    // Constructors
    public Employee() {}
    
    public Employee(String firstName, String lastName, EmployeeType employeeType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeType = employeeType;
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
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getSsn() {
        return ssn;
    }
    
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    
    public String getSsnEncrypted() {
        return ssnEncrypted;
    }
    
    public void setSsnEncrypted(String ssnEncrypted) {
        this.ssnEncrypted = ssnEncrypted;
    }
    
    public String getSsnEncryptedIv() {
        return ssnEncryptedIv;
    }
    
    public void setSsnEncryptedIv(String ssnEncryptedIv) {
        this.ssnEncryptedIv = ssnEncryptedIv;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    public LocalDate getTerminationDate() {
        return terminationDate;
    }
    
    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }
    
    public EmployeeStatus getStatus() {
        return status;
    }
    
    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
    
    public EmployeeType getEmployeeType() {
        return employeeType;
    }
    
    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public CompensationType getCompensationType() {
        return compensationType;
    }
    
    public void setCompensationType(CompensationType compensationType) {
        this.compensationType = compensationType;
    }
    
    public PayFrequency getPayFrequency() {
        return payFrequency;
    }
    
    public void setPayFrequency(PayFrequency payFrequency) {
        this.payFrequency = payFrequency;
    }
    
    public Integer getFederalTaxExemptions() {
        return federalTaxExemptions;
    }
    
    public void setFederalTaxExemptions(Integer federalTaxExemptions) {
        this.federalTaxExemptions = federalTaxExemptions;
    }
    
    public Integer getStateTaxExemptions() {
        return stateTaxExemptions;
    }
    
    public void setStateTaxExemptions(Integer stateTaxExemptions) {
        this.stateTaxExemptions = stateTaxExemptions;
    }
    
    public BigDecimal getAdditionalFederalWithholding() {
        return additionalFederalWithholding;
    }
    
    public void setAdditionalFederalWithholding(BigDecimal additionalFederalWithholding) {
        this.additionalFederalWithholding = additionalFederalWithholding;
    }
    
    public BigDecimal getAdditionalStateWithholding() {
        return additionalStateWithholding;
    }
    
    public void setAdditionalStateWithholding(BigDecimal additionalStateWithholding) {
        this.additionalStateWithholding = additionalStateWithholding;
    }
    
    public String getBankAccountNumberEncrypted() {
        return bankAccountNumberEncrypted;
    }
    
    public void setBankAccountNumberEncrypted(String bankAccountNumberEncrypted) {
        this.bankAccountNumberEncrypted = bankAccountNumberEncrypted;
    }
    
    public String getBankAccountNumberEncryptedIv() {
        return bankAccountNumberEncryptedIv;
    }
    
    public void setBankAccountNumberEncryptedIv(String bankAccountNumberEncryptedIv) {
        this.bankAccountNumberEncryptedIv = bankAccountNumberEncryptedIv;
    }
    
    public String getBankRoutingNumberEncrypted() {
        return bankRoutingNumberEncrypted;
    }
    
    public void setBankRoutingNumberEncrypted(String bankRoutingNumberEncrypted) {
        this.bankRoutingNumberEncrypted = bankRoutingNumberEncrypted;
    }
    
    public String getBankRoutingNumberEncryptedIv() {
        return bankRoutingNumberEncryptedIv;
    }
    
    public void setBankRoutingNumberEncryptedIv(String bankRoutingNumberEncryptedIv) {
        this.bankRoutingNumberEncryptedIv = bankRoutingNumberEncryptedIv;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
    
    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    
    public BigDecimal getStandardHours() {
        return standardHours;
    }
    
    public void setStandardHours(BigDecimal standardHours) {
        this.standardHours = standardHours;
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public TaxFilingStatus getTaxFilingStatus() {
        return taxFilingStatus;
    }
    
    public void setTaxFilingStatus(TaxFilingStatus taxFilingStatus) {
        this.taxFilingStatus = taxFilingStatus;
    }
    
    public Integer getDependents() {
        return dependents;
    }
    
    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }
    
    public Boolean getIsExempt() {
        return isExempt;
    }
    
    public void setIsExempt(Boolean isExempt) {
        this.isExempt = isExempt;
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
    
    // Business methods
    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE;
    }
    
    public boolean isTerminated() {
        return status == EmployeeStatus.TERMINATED || 
               (terminationDate != null && terminationDate.isBefore(LocalDate.now()));
    }
    
    public boolean isHourly() {
        return compensationType == CompensationType.HOURLY;
    }
    
    public boolean isSalaried() {
        return compensationType == CompensationType.SALARY;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
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
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", status=" + status +
                ", employeeType=" + employeeType +
                '}';
    }
} 
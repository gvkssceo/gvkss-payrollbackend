package com.payroll.texas.service;

import com.payroll.texas.model.Company;
import com.payroll.texas.model.Employee;
import com.payroll.texas.model.User;
import com.payroll.texas.repository.CompanyRepository;
import com.payroll.texas.repository.EmployeeRepository;
import com.payroll.texas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Employee saveEmployee(Employee employee) {
        logger.info("Saving employee: {}", employee.getEmail());
        
        // Validate required fields
        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
        if (employee.getEmployeeType() == null) {
            throw new RuntimeException("Employee type is required");
        }
        if (employee.getCompensationType() == null) {
            throw new RuntimeException("Compensation type is required");
        }
        
        // Company should already be set from the controller
        if (employee.getCompany() == null) {
            throw new RuntimeException("Company is required for employee creation");
        }
        
        // Set default values
        if (employee.getStatus() == null) {
            employee.setStatus(com.payroll.texas.model.EmployeeStatus.ACTIVE);
        }
        
        if (employee.getCreatedAt() == null) {
            employee.setCreatedAt(LocalDateTime.now());
        }
        
        employee.setUpdatedAt(LocalDateTime.now());
        
        // TODO: Temporarily disable encryption for testing - re-enable later
        // Handle SSN encryption if provided
        if (employee.getSsn() != null && !employee.getSsn().isEmpty()) {
            // String encryptedSsn = encryptionService.encryptSSN(employee.getSsn());
            // employee.setSsnEncrypted(encryptedSsn);
            logger.info("SSN encryption temporarily disabled for testing");
        }

        // Handle bank account encryption if provided
        if (employee.getBankAccountNumberEncrypted() != null && !employee.getBankAccountNumberEncrypted().isEmpty()) {
            // String encryptedAccountNumber = encryptionService.encryptBankAccount(employee.getBankAccountNumberEncrypted());
            // employee.setBankAccountNumberEncrypted(encryptedAccountNumber);
            logger.info("Bank account encryption temporarily disabled for testing");
        }

        if (employee.getBankRoutingNumberEncrypted() != null && !employee.getBankRoutingNumberEncrypted().isEmpty()) {
            // String encryptedRoutingNumber = encryptionService.encryptRoutingNumber(employee.getBankRoutingNumberEncrypted());
            // employee.setBankRoutingNumberEncrypted(encryptedRoutingNumber);
            logger.info("Bank routing encryption temporarily disabled for testing");
        }

        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("Employee saved successfully with ID: {}", savedEmployee.getId());
        return savedEmployee;
    }

    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        List<Employee> employees = employeeRepository.findAll();
        logger.info("Found {} employees", employees.size());
        return employees;
    }

    public Optional<Employee> getEmployeeById(Long id) {
        logger.info("Fetching employee by ID: {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            logger.info("Found employee: {}", employee.get().getEmail());
        } else {
            logger.warn("Employee not found with ID: {}", id);
        }
        return employee;
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {
        logger.info("Updating employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Update basic fields
        if (employeeDetails.getFirstName() != null) {
            employee.setFirstName(employeeDetails.getFirstName());
        }
        if (employeeDetails.getLastName() != null) {
            employee.setLastName(employeeDetails.getLastName());
        }
        if (employeeDetails.getEmail() != null) {
            employee.setEmail(employeeDetails.getEmail());
        }
        if (employeeDetails.getPhone() != null) {
            employee.setPhone(employeeDetails.getPhone());
        }
        if (employeeDetails.getAddressLine1() != null) {
            employee.setAddressLine1(employeeDetails.getAddressLine1());
        }
        if (employeeDetails.getAddressLine2() != null) {
            employee.setAddressLine2(employeeDetails.getAddressLine2());
        }
        if (employeeDetails.getCity() != null) {
            employee.setCity(employeeDetails.getCity());
        }
        if (employeeDetails.getState() != null) {
            employee.setState(employeeDetails.getState());
        }
        if (employeeDetails.getZipCode() != null) {
            employee.setZipCode(employeeDetails.getZipCode());
        }
        if (employeeDetails.getDateOfBirth() != null) {
            employee.setDateOfBirth(employeeDetails.getDateOfBirth());
        }
        if (employeeDetails.getHireDate() != null) {
            employee.setHireDate(employeeDetails.getHireDate());
        }
        if (employeeDetails.getTerminationDate() != null) {
            employee.setTerminationDate(employeeDetails.getTerminationDate());
        }
        if (employeeDetails.getStatus() != null) {
            employee.setStatus(employeeDetails.getStatus());
        }
        if (employeeDetails.getEmployeeType() != null) {
            employee.setEmployeeType(employeeDetails.getEmployeeType());
        }
        if (employeeDetails.getJobTitle() != null) {
            employee.setJobTitle(employeeDetails.getJobTitle());
        }
        if (employeeDetails.getDepartment() != null) {
            employee.setDepartment(employeeDetails.getDepartment());
        }
        if (employeeDetails.getCompensationType() != null) {
            employee.setCompensationType(employeeDetails.getCompensationType());
        }
        if (employeeDetails.getPayFrequency() != null) {
            employee.setPayFrequency(employeeDetails.getPayFrequency());
        }
        if (employeeDetails.getHourlyRate() != null) {
            employee.setHourlyRate(employeeDetails.getHourlyRate());
        }
        if (employeeDetails.getSalary() != null) {
            employee.setSalary(employeeDetails.getSalary());
        }
        if (employeeDetails.getStandardHours() != null) {
            employee.setStandardHours(employeeDetails.getStandardHours());
        }
        if (employeeDetails.getFederalTaxExemptions() != null) {
            employee.setFederalTaxExemptions(employeeDetails.getFederalTaxExemptions());
        }
        if (employeeDetails.getStateTaxExemptions() != null) {
            employee.setStateTaxExemptions(employeeDetails.getStateTaxExemptions());
        }
        if (employeeDetails.getAdditionalFederalWithholding() != null) {
            employee.setAdditionalFederalWithholding(employeeDetails.getAdditionalFederalWithholding());
        }
        if (employeeDetails.getAdditionalStateWithholding() != null) {
            employee.setAdditionalStateWithholding(employeeDetails.getAdditionalStateWithholding());
        }
        if (employeeDetails.getTaxFilingStatus() != null) {
            employee.setTaxFilingStatus(employeeDetails.getTaxFilingStatus());
        }
        if (employeeDetails.getDependents() != null) {
            employee.setDependents(employeeDetails.getDependents());
        }
        if (employeeDetails.getIsExempt() != null) {
            employee.setIsExempt(employeeDetails.getIsExempt());
        }
        if (employeeDetails.getCustomFields() != null) {
            employee.setCustomFields(employeeDetails.getCustomFields());
        }

        // TODO: Temporarily disable encryption for testing - re-enable later
        // Handle SSN encryption if provided
        if (employeeDetails.getSsn() != null && !employeeDetails.getSsn().isEmpty()) {
            // String encryptedSsn = encryptionService.encryptSSN(employeeDetails.getSsn());
            // employee.setSsnEncrypted(encryptedSsn);
            logger.info("SSN encryption temporarily disabled for testing");
        }

        // Handle bank account encryption if provided
        if (employeeDetails.getBankAccountNumberEncrypted() != null && !employeeDetails.getBankAccountNumberEncrypted().isEmpty()) {
            // String encryptedAccountNumber = encryptionService.encryptBankAccount(employeeDetails.getBankAccountNumberEncrypted());
            // employee.setBankAccountNumberEncrypted(encryptedAccountNumber);
            logger.info("Bank account encryption temporarily disabled for testing");
        }

        if (employeeDetails.getBankRoutingNumberEncrypted() != null && !employeeDetails.getBankRoutingNumberEncrypted().isEmpty()) {
            // String encryptedRoutingNumber = encryptionService.encryptRoutingNumber(employeeDetails.getBankRoutingNumberEncrypted());
            // employee.setBankRoutingNumberEncrypted(encryptedRoutingNumber);
            logger.info("Bank routing encryption temporarily disabled for testing");
        }

        if (employeeDetails.getBankName() != null) {
            employee.setBankName(employeeDetails.getBankName());
        }
        if (employeeDetails.getAccountType() != null) {
            employee.setAccountType(employeeDetails.getAccountType());
        }

        employee.setUpdatedAt(LocalDateTime.now());

        Employee updatedEmployee = employeeRepository.save(employee);
        logger.info("Employee updated successfully: {}", updatedEmployee.getEmail());
        return updatedEmployee;
    }

    public void deleteEmployee(Long id) {
        logger.info("Deleting employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
        logger.info("Employee deleted successfully");
    }

    public Company getCompanyById(Long id) {
        logger.info("Fetching company by ID: {}", id);
        Company company = companyRepository.findById(id).orElse(null);
        if (company != null) {
            logger.info("Found company: {}", company.getName());
        } else {
            logger.warn("Company not found with ID: {}", id);
        }
        return company;
    }
    
    /**
     * Get company from authenticated user context
     * This method should be called from controllers that have access to the JWT token
     */
    public Company getCompanyFromUserContext(Long userId) {
        logger.info("Getting company from user context for user ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("User not found with ID: {}", userId);
            return null;
        }
        
        Company company = user.getCompany();
        if (company == null) {
            logger.warn("User {} has no associated company", userId);
            return null;
        }
        
        logger.info("Found company {} for user {}", company.getName(), userId);
        return company;
    }

    public List<Employee> getEmployeesByCompanyId(Long companyId) {
        logger.info("Fetching employees for company ID: {}", companyId);
        return employeeRepository.findByCompanyId(companyId);
    }

    public List<Employee> getEmployeesByCompanyIdAndStatus(Long companyId, com.payroll.texas.model.EmployeeStatus status) {
        logger.info("Fetching employees for company ID: {} and status: {}", companyId, status);
        return employeeRepository.findByCompanyIdAndStatus(companyId, status);
    }
} 
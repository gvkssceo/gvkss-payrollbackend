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
import java.util.Map;
import java.util.HashMap;

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
    
    @Autowired
    private CustomFieldsService customFieldsService;

    @Autowired
    private EncryptionService encryptionService;

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
        
        // Encrypt and store sensitive fields (no validation here)
        // SSN is handled by controller, so just store encrypted value if present
        if (employee.getSsn() != null) {
            logger.info("[BACKEND] SSN before encryption: {}", employee.getSsn());
            String encryptedSsn = encryptionService.encryptSSN(employee.getSsn());
            logger.info("[BACKEND] SSN after encryption: {}", encryptedSsn);
            employee.setSsnEncrypted(encryptedSsn);
            employee.setSsn(null);
        }
        // Routing number and account number are already encrypted by controller, just store
        if (employee.getBankRoutingNumberEncrypted() != null) {
            logger.info("[BACKEND] Routing number (already encrypted): {}", employee.getBankRoutingNumberEncrypted());
            // Do NOT re-encrypt, just store as is
        }
        if (employee.getBankAccountNumberEncrypted() != null) {
            logger.info("[BACKEND] Account number (already encrypted): {}", employee.getBankAccountNumberEncrypted());
            // Do NOT re-encrypt, just store as is
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

        // Handle SSN encryption if provided
        if (employeeDetails.getSsn() != null && !employeeDetails.getSsn().isEmpty()) {
            String ssn = employeeDetails.getSsn();
            if (ssn.matches("\\d{9}")) {
                String encryptedSsn = encryptionService.encryptSSN(ssn);
                employee.setSsnEncrypted(encryptedSsn);
                employee.setSsn(null); // Clear raw SSN to avoid double encryption
                logger.info("SSN encrypted and set (update).");
            } else {
                employee.setSsnEncrypted(ssn);
                employee.setSsn(null); // Clear to avoid accidental re-encryption
                logger.info("SSN already encrypted, set as is (update).");
            }
        }

        // Handle bank account encryption if provided
        if (employeeDetails.getBankAccountNumberEncrypted() != null && !employeeDetails.getBankAccountNumberEncrypted().isEmpty()) {
            String account = employeeDetails.getBankAccountNumberEncrypted();
            // Only encrypt if it's all digits (plain), otherwise assume already encrypted
            if (account.matches("^\\d+$")) {
                String encryptedAccountNumber = encryptionService.encryptBankAccount(account);
                employee.setBankAccountNumberEncrypted(encryptedAccountNumber);
                logger.info("Bank account encrypted and set (update).");
            } else {
                logger.info("Bank account already encrypted, set as is (update).");
            }
        }

        if (employeeDetails.getBankRoutingNumberEncrypted() != null && !employeeDetails.getBankRoutingNumberEncrypted().isEmpty()) {
            String routing = employeeDetails.getBankRoutingNumberEncrypted();
            // Only encrypt if it's exactly 9 digits (plain), otherwise assume already encrypted
            if (routing.matches("^\\d{9}$")) {
                String encryptedRoutingNumber = encryptionService.encryptRoutingNumber(routing);
                employee.setBankRoutingNumberEncrypted(encryptedRoutingNumber);
                logger.info("Bank routing number encrypted and set (update).");
            } else {
                logger.info("Bank routing number already encrypted, set as is (update).");
            }
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

    /**
     * Get department codes for a company
     */
    public Map<String, Object> getDepartmentCodes(Long companyId) {
        logger.info("Getting department codes for company ID: {}", companyId);
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> departmentCodes = new HashMap<>();
            departmentCodes.put("departmentCodes", customFields.getOrDefault("departmentCodes", new String[0]));
            departmentCodes.put("defaultDepartment", customFields.getOrDefault("defaultDepartment", ""));
            
            return departmentCodes;
            
        } catch (Exception e) {
            logger.error("Error getting department codes for company ID {}: {}", companyId, e.getMessage());
            throw new RuntimeException("Failed to get department codes", e);
        }
    }

    /**
     * Update department codes for a company
     */
    public Map<String, Object> updateDepartmentCodes(Long companyId, Map<String, Object> departmentCodesData) {
        logger.info("Updating department codes for company ID: {}", companyId);
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update department codes in custom fields
            if (departmentCodesData.containsKey("departmentCodes")) {
                customFields.put("departmentCodes", departmentCodesData.get("departmentCodes"));
            }
            if (departmentCodesData.containsKey("defaultDepartment")) {
                customFields.put("defaultDepartment", departmentCodesData.get("defaultDepartment"));
            }

            // Save updated custom fields
            String updatedCustomFields = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getDepartmentCodes(companyId);
            
        } catch (Exception e) {
            logger.error("Error updating department codes for company ID {}: {}", companyId, e.getMessage());
            throw new RuntimeException("Failed to update department codes", e);
        }
    }

    /**
     * Get default salary hours for a company
     */
    public Map<String, Object> getDefaultSalaryHours(Long companyId) {
        logger.info("Getting default salary hours for company ID: {}", companyId);
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> defaultSalaryHours = new HashMap<>();
            defaultSalaryHours.put("standardHours", customFields.getOrDefault("standardHours", 40));
            defaultSalaryHours.put("overtimeThreshold", customFields.getOrDefault("overtimeThreshold", 40));
            defaultSalaryHours.put("doubleTimeThreshold", customFields.getOrDefault("doubleTimeThreshold", 60));
            defaultSalaryHours.put("defaultHourlyRate", customFields.getOrDefault("defaultHourlyRate", 15.0));
            defaultSalaryHours.put("defaultSalary", customFields.getOrDefault("defaultSalary", 50000.0));
            
            return defaultSalaryHours;
            
        } catch (Exception e) {
            logger.error("Error getting default salary hours for company ID {}: {}", companyId, e.getMessage());
            throw new RuntimeException("Failed to get default salary hours", e);
        }
    }

    /**
     * Update default salary hours for a company
     */
    public Map<String, Object> updateDefaultSalaryHours(Long companyId, Map<String, Object> defaultSalaryHoursData) {
        logger.info("Updating default salary hours for company ID: {}", companyId);
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update default salary hours in custom fields
            if (defaultSalaryHoursData.containsKey("standardHours")) {
                customFields.put("standardHours", defaultSalaryHoursData.get("standardHours"));
            }
            if (defaultSalaryHoursData.containsKey("overtimeThreshold")) {
                customFields.put("overtimeThreshold", defaultSalaryHoursData.get("overtimeThreshold"));
            }
            if (defaultSalaryHoursData.containsKey("doubleTimeThreshold")) {
                customFields.put("doubleTimeThreshold", defaultSalaryHoursData.get("doubleTimeThreshold"));
            }
            if (defaultSalaryHoursData.containsKey("defaultHourlyRate")) {
                customFields.put("defaultHourlyRate", defaultSalaryHoursData.get("defaultHourlyRate"));
            }
            if (defaultSalaryHoursData.containsKey("defaultSalary")) {
                customFields.put("defaultSalary", defaultSalaryHoursData.get("defaultSalary"));
            }

            // Save updated custom fields
            String updatedCustomFields = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getDefaultSalaryHours(companyId);
            
        } catch (Exception e) {
            logger.error("Error updating default salary hours for company ID {}: {}", companyId, e.getMessage());
            throw new RuntimeException("Failed to update default salary hours", e);
        }
    }

    /**
     * Add a new department code for a company
     */
    public Map<String, Object> addDepartmentCode(Long companyId, Map<String, Object> departmentCodeData) {
        logger.info("Adding department code for company ID: {}", companyId);
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Get existing department codes
            Object[] existingCodes = (Object[]) customFields.getOrDefault("departmentCodes", new Object[0]);
            
            // Create new department code object
            Map<String, Object> newCode = new HashMap<>();
            newCode.put("id", java.util.UUID.randomUUID().toString());
            newCode.put("code", departmentCodeData.get("code"));
            newCode.put("description", departmentCodeData.get("description"));
            newCode.put("inUse", false);
            newCode.put("inReports", false);
            
            // Add to existing codes
            Object[] updatedCodes = new Object[existingCodes.length + 1];
            System.arraycopy(existingCodes, 0, updatedCodes, 0, existingCodes.length);
            updatedCodes[existingCodes.length] = newCode;
            
            // Update custom fields
            customFields.put("departmentCodes", updatedCodes);
            
            // Save updated custom fields
            String updatedCustomFields = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return newCode;
            
        } catch (Exception e) {
            logger.error("Error adding department code for company ID {}: {}", companyId, e.getMessage());
            throw new RuntimeException("Failed to add department code", e);
        }
    }

    /**
     * Update a specific department code for a company
     */
    public Map<String, Object> updateDepartmentCodeById(Long companyId, String codeId, Map<String, Object> departmentCodeData) {
        logger.info("Updating department code {} for company ID: {}", codeId, companyId);
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Get existing department codes
            Object[] existingCodes = (Object[]) customFields.getOrDefault("departmentCodes", new Object[0]);
            
            // Find and update the specific code
            boolean found = false;
            for (int i = 0; i < existingCodes.length; i++) {
                if (existingCodes[i] instanceof Map) {
                    Map<String, Object> code = (Map<String, Object>) existingCodes[i];
                    if (codeId.equals(code.get("id"))) {
                        code.put("code", departmentCodeData.get("code"));
                        code.put("description", departmentCodeData.get("description"));
                        found = true;
                        break;
                    }
                }
            }
            
            if (!found) {
                throw new RuntimeException("Department code not found with ID: " + codeId);
            }
            
            // Save updated custom fields
            String updatedCustomFields = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            // Return the updated code
            for (Object codeObj : existingCodes) {
                if (codeObj instanceof Map) {
                    Map<String, Object> code = (Map<String, Object>) codeObj;
                    if (codeId.equals(code.get("id"))) {
                        return code;
                    }
                }
            }
            
            throw new RuntimeException("Failed to return updated department code");
            
        } catch (Exception e) {
            logger.error("Error updating department code {} for company ID {}: {}", codeId, companyId, e.getMessage());
            throw new RuntimeException("Failed to update department code", e);
        }
    }

    /**
     * Delete a specific department code for a company
     */
    public void deleteDepartmentCodeById(Long companyId, String codeId) {
        logger.info("Deleting department code {} for company ID: {}", codeId, companyId);
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Get existing department codes
            Object[] existingCodes = (Object[]) customFields.getOrDefault("departmentCodes", new Object[0]);
            
            // Find and remove the specific code
            Object[] updatedCodes = new Object[existingCodes.length - 1];
            int newIndex = 0;
            boolean found = false;
            
            for (Object codeObj : existingCodes) {
                if (codeObj instanceof Map) {
                    Map<String, Object> code = (Map<String, Object>) codeObj;
                    if (!codeId.equals(code.get("id"))) {
                        updatedCodes[newIndex++] = codeObj;
                    } else {
                        found = true;
                    }
                }
            }
            
            if (!found) {
                throw new RuntimeException("Department code not found with ID: " + codeId);
            }
            
            // Update custom fields
            customFields.put("departmentCodes", updatedCodes);
            
            // Save updated custom fields
            String updatedCustomFields = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);
            
        } catch (Exception e) {
            logger.error("Error deleting department code {} for company ID {}: {}", codeId, companyId, e.getMessage());
            throw new RuntimeException("Failed to delete department code", e);
        }
    }
} 
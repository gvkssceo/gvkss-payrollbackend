package com.payroll.texas.controller;

import com.payroll.texas.model.Employee;
import com.payroll.texas.service.EmployeeService;
import com.payroll.texas.service.AuthService;
import com.payroll.texas.service.RsaDecryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

// DTOs for sensitive info
class SensitiveInfoResponse {
    public String ssn;
    public String routingNumber;
    public String accountNumber;
    public SensitiveInfoResponse(String ssn, String routingNumber, String accountNumber) {
        this.ssn = ssn;
        this.routingNumber = routingNumber;
        this.accountNumber = accountNumber;
    }
}
class SensitiveInfoUpdateRequest {
    public String ssn;
    public String routingNumber;
    public String accountNumber;
}

@RestController
@RequestMapping("/employees")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5174"})
public class EmployeeController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private AuthService authService;

    @Autowired
    private com.payroll.texas.service.EncryptionService encryptionService;

    @Value("${app.rsa.private-key}")
    private String privateKeyPem;

    // Constructor to verify controller is being instantiated
    public EmployeeController() {
        logger.info("EmployeeController is being instantiated!");
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.info("Employee health check endpoint called");
        return ResponseEntity.ok("Employee Controller is working!");
    }

    // Add new employee
    @PostMapping("/addemployee")
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        logger.info("Received request to add new employee: {}", employee.getEmail());
        try {
            // Decrypt sensitive fields if they are not null and look encrypted
            RsaDecryptor decryptor = new RsaDecryptor(privateKeyPem);
            if (employee.getSsn() != null && !employee.getSsn().isEmpty()) {
                try { employee.setSsn(decryptor.decrypt(employee.getSsn())); } catch (Exception ignored) {}
            }
            if (employee.getBankAccountNumberEncrypted() != null && !employee.getBankAccountNumberEncrypted().isEmpty()) {
                try { employee.setBankAccountNumberEncrypted(decryptor.decrypt(employee.getBankAccountNumberEncrypted())); } catch (Exception ignored) {}
            }
            if (employee.getBankRoutingNumberEncrypted() != null && !employee.getBankRoutingNumberEncrypted().isEmpty()) {
                try { employee.setBankRoutingNumberEncrypted(decryptor.decrypt(employee.getBankRoutingNumberEncrypted())); } catch (Exception ignored) {}
            }
            Employee savedEmployee = employeeService.saveEmployee(employee);
            logger.info("Successfully added employee with ID: {}", savedEmployee.getId());
            
            // Create a simple response DTO to avoid circular references
            java.util.Map<String, Object> response = java.util.Map.of(
                "id", savedEmployee.getId(),
                "firstName", savedEmployee.getFirstName(),
                "lastName", savedEmployee.getLastName(),
                "email", savedEmployee.getEmail(),
                "status", savedEmployee.getStatus(),
                "message", "Employee created successfully"
            );
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error adding employee: {}", e.getMessage());
            return new ResponseEntity<>(java.util.Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // Get all employees
    @GetMapping("/getallemployees")
    public ResponseEntity<?> getAllEmployees(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "status", required = false) String statusParam
    ) {
        logger.info("Received request to get all employees for logged-in user's company");
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            java.util.Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(java.util.Map.of("error", "Invalid or expired token"));
            }

            java.util.Map<String, Object> userInfo = (java.util.Map<String, Object>) validationResult.get("userInfo");
            Long userId = ((Number) userInfo.get("id")).longValue();

            // Get company from authenticated user context
            com.payroll.texas.model.Company company = employeeService.getCompanyFromUserContext(userId);
            if (company == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "User not associated with any company"));
            }

            java.util.List<Employee> employees;
            if (statusParam != null) {
                com.payroll.texas.model.EmployeeStatus status = com.payroll.texas.model.EmployeeStatus.valueOf(statusParam.toUpperCase());
                employees = employeeService.getEmployeesByCompanyIdAndStatus(company.getId(), status);
            } else {
                employees = employeeService.getEmployeesByCompanyId(company.getId());
            }
            logger.info("Successfully retrieved {} employees for company {}{}", employees.size(), company.getId(), statusParam != null ? (" and status " + statusParam) : "");

            // Create simple response DTOs to avoid circular references
            java.util.List<java.util.Map<String, Object>> employeeDtos = employees.stream()
                    .map(emp -> {
                        java.util.Map<String, Object> dto = new java.util.HashMap<>();
                        dto.put("id", emp.getId());
                        dto.put("firstName", emp.getFirstName());
                        dto.put("lastName", emp.getLastName());
                        dto.put("email", emp.getEmail());
                        dto.put("phone", emp.getPhone());
                        dto.put("jobTitle", emp.getJobTitle());
                        dto.put("status", emp.getStatus());
                        dto.put("employeeType", emp.getEmployeeType());
                        dto.put("hireDate", emp.getHireDate());
                        // Add companyId for debugging
                        dto.put("companyId", emp.getCompany() != null ? emp.getCompany().getId() : null);
                        return dto;
                    })
                    .toList();

            return new ResponseEntity<>(employeeDtos, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving employees: {}", e.getMessage());
            return new ResponseEntity<>(java.util.Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get employee by ID
    @GetMapping("/getemployee/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        logger.info("Received request to get employee with ID: {}", id);
        try {
            Optional<Employee> employee = employeeService.getEmployeeById(id);
            if (employee.isPresent()) {
                logger.info("Successfully retrieved employee with ID: {}", id);
                return new ResponseEntity<>(employee.get(), HttpStatus.OK);
            } else {
                logger.warn("Employee not found with ID: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving employee with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update employee
    @PutMapping("/updateemployee/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        logger.info("Received request to update employee with ID: {}", id);
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employee);
            logger.info("Successfully updated employee with ID: {}", id);
            return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating employee with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Delete employee
    @DeleteMapping("/deleteemployee/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        logger.info("Received request to delete employee with ID: {}", id);
        try {
            employeeService.deleteEmployee(id);
            logger.info("Successfully deleted employee with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting employee with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Add new employee with company
    @PostMapping("/addemployee-with-company")
    public ResponseEntity<?> addEmployeeWithCompany(@RequestBody java.util.Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to add employee with company. Payload keys: {}", payload.keySet());
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            java.util.Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Invalid or expired token"));
            }
            
            java.util.Map<String, Object> userInfo = (java.util.Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            // Get company from authenticated user context
            com.payroll.texas.model.Company company = employeeService.getCompanyFromUserContext(userId);
            if (company == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "User not associated with any company"));
            }
            
            // Extract employee data from payload
            Object employeeObj = payload.get("employee");
            if (employeeObj == null) {
                logger.error("Employee data is missing from payload");
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Employee data is required"));
            }

            logger.info("Converting employee object to Employee entity...");
            // Convert employeeObj to Employee, with JavaTimeModule for LocalDate
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            Employee employee = mapper.convertValue(employeeObj, Employee.class);

            // Set company from authenticated user context
            employee.setCompany(company);

            // Decrypt sensitive fields (non-destructive, backward compatible)
            try {
                String pem = privateKeyPem != null ? privateKeyPem.replace("\n", "\n") : null;
                if (pem != null && !pem.isEmpty()) {
                    com.payroll.texas.service.RsaDecryptor decryptor = new com.payroll.texas.service.RsaDecryptor(pem);
                    if (employee.getSsn() != null && !employee.getSsn().isEmpty()) {
                        try { employee.setSsn(decryptor.decrypt(employee.getSsn())); } catch (Exception e) { logger.warn("SSN decryption failed: {}", e.getMessage()); }
                    }
                    if (employee.getBankAccountNumberEncrypted() != null && !employee.getBankAccountNumberEncrypted().isEmpty()) {
                        try {
                            String decryptedAccount = decryptor.decrypt(employee.getBankAccountNumberEncrypted());
                            if (decryptedAccount == null || decryptedAccount.length() < 4) {
                                throw new IllegalArgumentException("Bank account number is too short.");
                            }
                            // Re-encrypt for storage
                            employee.setBankAccountNumberEncrypted(encryptionService.encryptBankAccount(decryptedAccount));
                        } catch (Exception e) { logger.warn("Bank account decryption failed: {}", e.getMessage()); }
                    }
                    if (employee.getBankRoutingNumberEncrypted() != null && !employee.getBankRoutingNumberEncrypted().isEmpty()) {
                        try {
                            String decryptedRouting = decryptor.decrypt(employee.getBankRoutingNumberEncrypted());
                            if (decryptedRouting == null || !decryptedRouting.matches("^\\d{9}$")) {
                                throw new IllegalArgumentException("Invalid bank routing number. Expected 9 digits.");
                            }
                            // Re-encrypt for storage
                            employee.setBankRoutingNumberEncrypted(encryptionService.encryptRoutingNumber(decryptedRouting));
                        } catch (Exception e) { logger.warn("Bank routing decryption failed: {}", e.getMessage()); }
                    }
                } else {
                    logger.warn("PRIVATE_KEY not set in application.yml; skipping decryption.");
                }
            } catch (Exception e) {
                logger.warn("Decryption block failed: {}", e.getMessage());
            }

            // Validate decrypted sensitive fields (non-destructive)
            String validationError = null;
            if (employee.getSsn() != null && !employee.getSsn().matches("^\\d{3}-?\\d{2}-?\\d{4}$")) {
                validationError = "Invalid SSN format. Expected 9 digits or XXX-XX-XXXX";
            }
            // No need to validate account/routing here, already done above
            if (validationError != null) {
                logger.warn("Validation failed: {}", validationError);
                return ResponseEntity.badRequest().body(java.util.Map.of("error", validationError));
            }

            // AES re-encrypt sensitive fields for storage (hybrid encryption)
            // REMOVE these lines:
            // if (employee.getSsn() != null) {
            //     employee.setSsn(encryptionService.encryptSSN(employee.getSsn()));
            // }
            // if (employee.getBankAccountNumberEncrypted() != null) {
            //     employee.setBankAccountNumberEncrypted(encryptionService.encryptBankAccount(employee.getBankAccountNumberEncrypted()));
            // }
            // if (employee.getBankRoutingNumberEncrypted() != null) {
            //     employee.setBankRoutingNumberEncrypted(encryptionService.encryptRoutingNumber(employee.getBankRoutingNumberEncrypted()));
            // }

            logger.info("Saving employee: {} {}", employee.getFirstName(), employee.getLastName());
            Employee savedEmployee = employeeService.saveEmployee(employee);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", savedEmployee.getId());
            response.put("firstName", savedEmployee.getFirstName());
            response.put("lastName", savedEmployee.getLastName());
            response.put("email", savedEmployee.getEmail());
            response.put("status", savedEmployee.getStatus());
            response.put("message", "Employee created successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error adding employee with company: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of("error", e.getMessage()));
        }
    }


    // Add new department code for a company
    @PostMapping("/company/{companyId}/department-codes")
    public ResponseEntity<?> addDepartmentCode(
            @PathVariable Long companyId,
            @RequestBody Map<String, Object> departmentCodeData,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to add department code for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Add department code to company custom fields
            Map<String, Object> newDepartmentCode = employeeService.addDepartmentCode(companyId, departmentCodeData);
            return ResponseEntity.ok(newDepartmentCode);
            
        } catch (Exception e) {
            logger.error("Error adding department code for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add department code: " + e.getMessage()));
        }
    }

    // Update specific department code for a company
    @PutMapping("/company/{companyId}/department-codes/{codeId}")
    public ResponseEntity<?> updateDepartmentCode(
            @PathVariable Long companyId,
            @PathVariable String codeId,
            @RequestBody Map<String, Object> departmentCodeData,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to update department code {} for company: {}", codeId, companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Update specific department code in company custom fields
            Map<String, Object> updatedDepartmentCode = employeeService.updateDepartmentCodeById(companyId, codeId, departmentCodeData);
            return ResponseEntity.ok(updatedDepartmentCode);
            
        } catch (Exception e) {
            logger.error("Error updating department code {} for company {}: {}", codeId, companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update department code: " + e.getMessage()));
        }
    }

    // Delete specific department code for a company
    @DeleteMapping("/company/{companyId}/department-codes/{codeId}")
    public ResponseEntity<?> deleteDepartmentCode(
            @PathVariable Long companyId,
            @PathVariable String codeId,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to delete department code {} for company: {}", codeId, companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Delete specific department code from company custom fields
            employeeService.deleteDepartmentCodeById(companyId, codeId);
            return ResponseEntity.ok(Map.of("message", "Department code deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Error deleting department code {} for company {}: {}", codeId, companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete department code: " + e.getMessage()));
        }
    }

    // Get department codes for a company
    @GetMapping("/company/{companyId}/department-codes")
    public ResponseEntity<?> getDepartmentCodes(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to get department codes for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Get department codes from company custom fields
            Map<String, Object> departmentCodes = employeeService.getDepartmentCodes(companyId);
            return ResponseEntity.ok(departmentCodes);
            
        } catch (Exception e) {
            logger.error("Error getting department codes for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get department codes: " + e.getMessage()));
        }
    }

    // Update department codes for a company
    @PutMapping("/company/{companyId}/department-codes")
    public ResponseEntity<?> updateDepartmentCodes(
            @PathVariable Long companyId,
            @RequestBody Map<String, Object> departmentCodesData,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to update department codes for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Update department codes in company custom fields
            Map<String, Object> updatedDepartmentCodes = employeeService.updateDepartmentCodes(companyId, departmentCodesData);
            return ResponseEntity.ok(updatedDepartmentCodes);
            
        } catch (Exception e) {
            logger.error("Error updating department codes for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update department codes: " + e.getMessage()));
        }
    }

    // Get default salary hours for a company
    @GetMapping("/company/{companyId}/default-salary-hours")
    public ResponseEntity<?> getDefaultSalaryHours(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to get default salary hours for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Get default salary hours from company custom fields
            Map<String, Object> defaultSalaryHours = employeeService.getDefaultSalaryHours(companyId);
            return ResponseEntity.ok(defaultSalaryHours);
            
        } catch (Exception e) {
            logger.error("Error getting default salary hours for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get default salary hours: " + e.getMessage()));
        }
    }

    // Update default salary hours for a company
    @PutMapping("/company/{companyId}/default-salary-hours")
    public ResponseEntity<?> updateDefaultSalaryHours(
            @PathVariable Long companyId,
            @RequestBody Map<String, Object> defaultSalaryHoursData,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to update default salary hours for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Update default salary hours in company custom fields
            Map<String, Object> updatedDefaultSalaryHours = employeeService.updateDefaultSalaryHours(companyId, defaultSalaryHoursData);
            return ResponseEntity.ok(updatedDefaultSalaryHours);
            
        } catch (Exception e) {
            logger.error("Error updating default salary hours for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update default salary hours: " + e.getMessage()));
        }
    }
            

    // Secure endpoint to fetch decrypted sensitive info
    @GetMapping("/{id}/sensitive-info")
    public ResponseEntity<?> getSensitiveInfo(@PathVariable Long id) {
        logger.info("Sensitive info requested for employee {} at {}", id, LocalDateTime.now());
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }
        Employee employee = employeeOpt.get();
        String ssn = null, routing = null, account = null;
        try {
            ssn = encryptionService.decryptSSN(employee.getSsnEncrypted());
        } catch (Exception e) { logger.warn("Failed to decrypt SSN for employee {}", id); }
        try {
            routing = encryptionService.decryptRoutingNumber(employee.getBankRoutingNumberEncrypted());
        } catch (Exception e) { logger.warn("Failed to decrypt routing number for employee {}", id); }
        try {
            account = encryptionService.decryptBankAccount(employee.getBankAccountNumberEncrypted());
        } catch (Exception e) { logger.warn("Failed to decrypt account number for employee {}", id); }
        // Audit log
        logger.info("Sensitive info accessed for employee {} by user at {}", id, LocalDateTime.now());
        return ResponseEntity.ok(new SensitiveInfoResponse(ssn, routing, account));
    }

    // Secure endpoint to update sensitive info
    @PatchMapping("/{id}/sensitive-info")
    public ResponseEntity<?> updateSensitiveInfo(@PathVariable Long id, @RequestBody SensitiveInfoUpdateRequest req) {
        logger.info("Sensitive info update requested for employee {} at {}", id, LocalDateTime.now());
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }
        Employee employee = employeeOpt.get();
        try {
            if (req.ssn != null && !req.ssn.isEmpty()) {
                employee.setSsnEncrypted(encryptionService.encryptSSN(req.ssn));
            }
            if (req.routingNumber != null && !req.routingNumber.isEmpty()) {
                employee.setBankRoutingNumberEncrypted(encryptionService.encryptRoutingNumber(req.routingNumber));
            }
            if (req.accountNumber != null && !req.accountNumber.isEmpty()) {
                employee.setBankAccountNumberEncrypted(encryptionService.encryptBankAccount(req.accountNumber));
            }
            employeeService.saveEmployee(employee);
            // Audit log
            logger.info("Sensitive info updated for employee {} by user at {}", id, LocalDateTime.now());
            return ResponseEntity.ok("Sensitive info updated");
        } catch (Exception e) {
            logger.error("Failed to update sensitive info for employee {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update sensitive info: " + e.getMessage());

        }
    }
} 
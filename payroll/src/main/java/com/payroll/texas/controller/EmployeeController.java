package com.payroll.texas.controller;

import com.payroll.texas.model.Employee;
import com.payroll.texas.service.EmployeeService;
import com.payroll.texas.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5174"})
public class EmployeeController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private AuthService authService;

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
    public ResponseEntity<?> getAllEmployees() {
        logger.info("Received request to get all employees");
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            logger.info("Successfully retrieved {} employees", employees.size());
            
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
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Employee data is required"));
            }

            // Convert employeeObj to Employee, with JavaTimeModule for LocalDate
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            Employee employee = mapper.convertValue(employeeObj, Employee.class);

            // Set company from authenticated user context
            employee.setCompany(company);

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
            logger.error("Error adding employee with company: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of("error", e.getMessage()));
        }
    }
} 
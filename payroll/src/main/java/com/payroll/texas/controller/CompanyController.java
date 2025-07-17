package com.payroll.texas.controller;

import com.payroll.texas.model.Deduction;
import com.payroll.texas.model.Earning;
import com.payroll.texas.service.AuthService;
import com.payroll.texas.service.DeductionService;
import com.payroll.texas.service.EarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/company")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5174"})
public class CompanyController {
    
    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private EarningService earningService;
    
    @Autowired
    private DeductionService deductionService;

    // Constructor to verify controller is being instantiated
    public CompanyController() {
        logger.info("CompanyController is being instantiated!");
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.info("Company health check endpoint called");
        return ResponseEntity.ok("Company Controller is working!");
    }

    // Get default salary hours for a company
    @GetMapping("/{companyId}/default-salary-hours")
    public ResponseEntity<?> getDefaultSalaryHours(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get default salary hours for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = ((Number) userInfo.get("id")).longValue();

            // For now, return default values
            // In a real implementation, you would fetch these from a database table
            Map<String, Object> defaultSalaryHours = new HashMap<>();
            defaultSalaryHours.put("fullTime", new BigDecimal("86.67"));
            defaultSalaryHours.put("partTime", new BigDecimal("43.34"));
            defaultSalaryHours.put("temporary", new BigDecimal("86.67"));
            defaultSalaryHours.put("contractor", new BigDecimal("86.67"));

            logger.info("Successfully retrieved default salary hours for company: {}", companyId);
            return ResponseEntity.ok(defaultSalaryHours);
        } catch (Exception e) {
            logger.error("Error retrieving default salary hours: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update default salary hours for a company
    @PutMapping("/{companyId}/default-salary-hours")
    public ResponseEntity<?> updateDefaultSalaryHours(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> salaryHoursData
    ) {
        logger.info("Received request to update default salary hours for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = ((Number) userInfo.get("id")).longValue();

            // Extract the salary hours data
            Map<String, Object> defaultSalaryHours = new HashMap<>();
            defaultSalaryHours.put("fullTime", new BigDecimal(salaryHoursData.get("fullTime").toString()));
            defaultSalaryHours.put("partTime", new BigDecimal(salaryHoursData.get("partTime").toString()));
            defaultSalaryHours.put("temporary", new BigDecimal(salaryHoursData.get("temporary").toString()));
            defaultSalaryHours.put("contractor", new BigDecimal(salaryHoursData.get("contractor").toString()));

            // In a real implementation, you would save these to a database table
            // For now, just return the updated data
            logger.info("Successfully updated default salary hours for company: {}", companyId);
            return ResponseEntity.ok(defaultSalaryHours);
        } catch (Exception e) {
            logger.error("Error updating default salary hours: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get company earnings
    @GetMapping("/{companyId}/earnings")
    public ResponseEntity<?> getCompanyEarnings(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get company earnings for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            List<Earning> earnings = earningService.getEarningsByCompanyId(companyId);
            logger.info("Successfully retrieved {} earnings for company: {}", earnings.size(), companyId);
            return ResponseEntity.ok(earnings);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for company earnings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving company earnings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Add new earning
    @PostMapping("/{companyId}/earnings")
    public ResponseEntity<?> addCompanyEarning(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> earningData
    ) {
        logger.info("Received request to add earning for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Earning newEarning = earningService.createEarning(companyId, earningData);
            logger.info("Successfully added earning: {} for company: {}", newEarning.getId(), companyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newEarning);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for adding earning: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding earning: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update earning
    @PutMapping("/{companyId}/earnings/{earningId}")
    public ResponseEntity<?> updateCompanyEarning(
            @PathVariable Long companyId,
            @PathVariable Long earningId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> earningData
    ) {
        logger.info("Received request to update earning: {} for company: {}", earningId, companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Earning updatedEarning = earningService.updateEarning(companyId, earningId, earningData);
            logger.info("Successfully updated earning: {} for company: {}", earningId, companyId);
            return ResponseEntity.ok(updatedEarning);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for updating earning: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating earning: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Delete earning
    @DeleteMapping("/{companyId}/earnings/{earningId}")
    public ResponseEntity<?> deleteCompanyEarning(
            @PathVariable Long companyId,
            @PathVariable Long earningId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to delete earning: {} for company: {}", earningId, companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            earningService.deleteEarning(companyId, earningId);
            logger.info("Successfully deleted earning: {} for company: {}", earningId, companyId);
            return ResponseEntity.ok(Map.of("message", "Earning deleted successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for deleting earning: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting earning: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get company deductions
    @GetMapping("/{companyId}/deductions")
    public ResponseEntity<?> getCompanyDeductions(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get company deductions for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            List<Deduction> deductions = deductionService.getDeductionsByCompanyId(companyId);
            logger.info("Successfully retrieved {} deductions for company: {}", deductions.size(), companyId);
            return ResponseEntity.ok(deductions);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for company deductions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving company deductions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Add new deduction
    @PostMapping("/{companyId}/deductions")
    public ResponseEntity<?> addCompanyDeduction(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> deductionData
    ) {
        logger.info("Received request to add deduction for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Deduction newDeduction = deductionService.createDeduction(companyId, deductionData);
            logger.info("Successfully added deduction: {} for company: {}", newDeduction.getId(), companyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newDeduction);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for adding deduction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding deduction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update deduction
    @PutMapping("/{companyId}/deductions/{deductionId}")
    public ResponseEntity<?> updateCompanyDeduction(
            @PathVariable Long companyId,
            @PathVariable Long deductionId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> deductionData
    ) {
        logger.info("Received request to update deduction: {} for company: {}", deductionId, companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Deduction updatedDeduction = deductionService.updateDeduction(companyId, deductionId, deductionData);
            logger.info("Successfully updated deduction: {} for company: {}", deductionId, companyId);
            return ResponseEntity.ok(updatedDeduction);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for updating deduction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating deduction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Delete deduction
    @DeleteMapping("/{companyId}/deductions/{deductionId}")
    public ResponseEntity<?> deleteCompanyDeduction(
            @PathVariable Long companyId,
            @PathVariable Long deductionId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to delete deduction: {} for company: {}", deductionId, companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            deductionService.deleteDeduction(companyId, deductionId);
            logger.info("Successfully deleted deduction: {} for company: {}", deductionId, companyId);
            return ResponseEntity.ok(Map.of("message", "Deduction deleted successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for deleting deduction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting deduction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get other compensation
    @GetMapping("/{companyId}/other-compensation")
    public ResponseEntity<?> getOtherCompensation(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get other compensation for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // For now, return empty array
            // In a real implementation, you would fetch these from a database table
            logger.info("Successfully retrieved other compensation for company: {}", companyId);
            return ResponseEntity.ok(new Object[0]);
        } catch (Exception e) {
            logger.error("Error retrieving other compensation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get department codes
    @GetMapping("/{companyId}/department-codes")
    public ResponseEntity<?> getDepartmentCodes(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get department codes for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // For now, return empty array
            // In a real implementation, you would fetch these from a database table
            logger.info("Successfully retrieved department codes for company: {}", companyId);
            return ResponseEntity.ok(new Object[0]);
        } catch (Exception e) {
            logger.error("Error retrieving department codes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get workers compensation codes
    @GetMapping("/{companyId}/workers-compensation")
    public ResponseEntity<?> getWorkersCompCodes(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get workers compensation codes for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // For now, return empty array
            // In a real implementation, you would fetch these from a database table
            logger.info("Successfully retrieved workers compensation codes for company: {}", companyId);
            return ResponseEntity.ok(new Object[0]);
        } catch (Exception e) {
            logger.error("Error retrieving workers compensation codes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get payroll schedule
    @GetMapping("/{companyId}/payroll-schedule")
    public ResponseEntity<?> getPayrollSchedule(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get payroll schedule for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // For now, return default schedule
            Map<String, Object> schedule = new HashMap<>();
            schedule.put("frequency", "BI_WEEKLY");
            schedule.put("nextPayDate", "2024-01-15");
            schedule.put("payDays", new String[]{"Monday", "Friday"});

            logger.info("Successfully retrieved payroll schedule for company: {}", companyId);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            logger.error("Error retrieving payroll schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get rebuild payroll schedule settings
    @GetMapping("/{companyId}/rebuild-payroll-schedule")
    public ResponseEntity<?> getRebuildPayrollSchedule(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get rebuild payroll schedule for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // For now, return default settings
            Map<String, Object> settings = new HashMap<>();
            settings.put("autoRebuild", false);
            settings.put("rebuildFrequency", "MONTHLY");

            logger.info("Successfully retrieved rebuild payroll schedule for company: {}", companyId);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            logger.error("Error retrieving rebuild payroll schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get pay frequency
    @GetMapping("/{companyId}/pay-frequency")
    public ResponseEntity<?> getPayFrequency(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get pay frequency for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // For now, return default frequency
            Map<String, Object> frequency = new HashMap<>();
            frequency.put("frequency", "BI_WEEKLY");

            logger.info("Successfully retrieved pay frequency for company: {}", companyId);
            return ResponseEntity.ok(frequency);
        } catch (Exception e) {
            logger.error("Error retrieving pay frequency: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
} 
package com.payroll.texas.controller;

import com.payroll.texas.model.Company;
import com.payroll.texas.service.AuthService;
import com.payroll.texas.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/companies")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class CompanyController {
    
    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private AuthService authService;

    // Get company profile
    @GetMapping("/profile")
    public ResponseEntity<?> getCompanyProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Company company = companyService.getCompanyByUserId(userId);
            if (company == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Company not found"));
            }
            
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("id", company.getId());
            companyInfo.put("name", company.getName());
            companyInfo.put("legalName", company.getLegalName());
            companyInfo.put("email", company.getEmail());
            companyInfo.put("phone", company.getPhone());
            companyInfo.put("addressLine1", company.getAddressLine1());
            companyInfo.put("addressLine2", company.getAddressLine2());
            companyInfo.put("city", company.getCity());
            companyInfo.put("state", company.getState());
            companyInfo.put("zipCode", company.getZipCode());
            companyInfo.put("country", company.getCountry());
            companyInfo.put("ein", company.getEin());
            companyInfo.put("status", company.getStatus());
            companyInfo.put("subscriptionStatus", company.getSubscriptionStatus());
            companyInfo.put("customFields", company.getCustomFields());
            
            return ResponseEntity.ok(companyInfo);
            
        } catch (Exception e) {
            logger.error("Error getting company profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company profile: " + e.getMessage()));
        }
    }

    // Update company profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateCompanyProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> updateData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Company updatedCompany = companyService.updateCompanyProfile(userId, updateData);
            
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("id", updatedCompany.getId());
            companyInfo.put("name", updatedCompany.getName());
            companyInfo.put("legalName", updatedCompany.getLegalName());
            companyInfo.put("email", updatedCompany.getEmail());
            companyInfo.put("phone", updatedCompany.getPhone());
            companyInfo.put("addressLine1", updatedCompany.getAddressLine1());
            companyInfo.put("addressLine2", updatedCompany.getAddressLine2());
            companyInfo.put("city", updatedCompany.getCity());
            companyInfo.put("state", updatedCompany.getState());
            companyInfo.put("zipCode", updatedCompany.getZipCode());
            companyInfo.put("country", updatedCompany.getCountry());
            companyInfo.put("ein", updatedCompany.getEin());
            companyInfo.put("status", updatedCompany.getStatus());
            companyInfo.put("subscriptionStatus", updatedCompany.getSubscriptionStatus());
            companyInfo.put("customFields", updatedCompany.getCustomFields());
            
            return ResponseEntity.ok(companyInfo);
            
        } catch (Exception e) {
            logger.error("Error updating company profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company profile: " + e.getMessage()));
        }
    }

    // Get company bank information
    @GetMapping("/bank")
    public ResponseEntity<?> getCompanyBankInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> bankInfo = companyService.getCompanyBankInfo(userId);
            return ResponseEntity.ok(bankInfo);
            
        } catch (Exception e) {
            logger.error("Error getting company bank info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company bank info: " + e.getMessage()));
        }
    }

    // Update company bank information
    @PutMapping("/bank")
    public ResponseEntity<?> updateCompanyBankInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> bankData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedBankInfo = companyService.updateCompanyBankInfo(userId, bankData);
            return ResponseEntity.ok(updatedBankInfo);
            
        } catch (Exception e) {
            logger.error("Error updating company bank info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company bank info: " + e.getMessage()));
        }
    }

    // Get company settings
    @GetMapping("/settings")
    public ResponseEntity<?> getCompanySettings(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> settings = companyService.getCompanySettings(userId);
            return ResponseEntity.ok(settings);
            
        } catch (Exception e) {
            logger.error("Error getting company settings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company settings: " + e.getMessage()));
        }
    }

    // Update company settings
    @PutMapping("/settings")
    public ResponseEntity<?> updateCompanySettings(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> settingsData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedSettings = companyService.updateCompanySettings(userId, settingsData);
            return ResponseEntity.ok(updatedSettings);
            
        } catch (Exception e) {
            logger.error("Error updating company settings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company settings: " + e.getMessage()));
        }
    }

    // Get company users
    @GetMapping("/users")
    public ResponseEntity<?> getCompanyUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            var users = companyService.getCompanyUsers(userId);
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            logger.error("Error getting company users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company users: " + e.getMessage()));
        }
    }

    // Get company workers compensation
    @GetMapping("/{companyId}/workers-compensation")
    public ResponseEntity<?> getCompanyWorkersCompensation(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> workersCompInfo = companyService.getCompanyWorkersCompensation(userId);
            return ResponseEntity.ok(workersCompInfo);
            
        } catch (Exception e) {
            logger.error("Error getting company workers compensation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company workers compensation: " + e.getMessage()));
        }
    }

    // Update company workers compensation
    @PutMapping("/{companyId}/workers-compensation")
    public ResponseEntity<?> updateCompanyWorkersCompensation(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> workersCompData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedWorkersCompInfo = companyService.updateCompanyWorkersCompensation(userId, workersCompData);
            return ResponseEntity.ok(updatedWorkersCompInfo);
            
        } catch (Exception e) {
            logger.error("Error updating company workers compensation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company workers compensation: " + e.getMessage()));
        }
    }

    // Get company earnings
    @GetMapping("/{companyId}/earnings")
    public ResponseEntity<?> getCompanyEarnings(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> earningsInfo = companyService.getCompanyEarnings(userId);
            return ResponseEntity.ok(earningsInfo);
            
        } catch (Exception e) {
            logger.error("Error getting company earnings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company earnings: " + e.getMessage()));
        }
    }

    // Update company earnings
    @PutMapping("/{companyId}/earnings")
    public ResponseEntity<?> updateCompanyEarnings(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> earningsData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedEarningsInfo = companyService.updateCompanyEarnings(userId, earningsData);
            return ResponseEntity.ok(updatedEarningsInfo);
            
        } catch (Exception e) {
            logger.error("Error updating company earnings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company earnings: " + e.getMessage()));
        }
    }

    // Get company deductions
    @GetMapping("/{companyId}/deductions")
    public ResponseEntity<?> getCompanyDeductions(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> deductionsInfo = companyService.getCompanyDeductions(userId);
            return ResponseEntity.ok(deductionsInfo);
            
        } catch (Exception e) {
            logger.error("Error getting company deductions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company deductions: " + e.getMessage()));
        }
    }

    // Update company deductions
    @PutMapping("/{companyId}/deductions")
    public ResponseEntity<?> updateCompanyDeductions(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> deductionsData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedDeductionsInfo = companyService.updateCompanyDeductions(userId, deductionsData);
            return ResponseEntity.ok(updatedDeductionsInfo);
            
        } catch (Exception e) {
            logger.error("Error updating company deductions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company deductions: " + e.getMessage()));
        }
    }

    // Get company payroll schedule
    @GetMapping("/{companyId}/payroll-schedule")
    public ResponseEntity<?> getCompanyPayrollSchedule(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> payrollSchedule = companyService.getCompanyPayrollSchedule(userId);
            return ResponseEntity.ok(payrollSchedule);
            
        } catch (Exception e) {
            logger.error("Error getting company payroll schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company payroll schedule: " + e.getMessage()));
        }
    }

    // Update company payroll schedule
    @PutMapping("/{companyId}/payroll-schedule")
    public ResponseEntity<?> updateCompanyPayrollSchedule(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> scheduleData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedSchedule = companyService.updateCompanyPayrollSchedule(userId, scheduleData);
            return ResponseEntity.ok(updatedSchedule);
            
        } catch (Exception e) {
            logger.error("Error updating company payroll schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company payroll schedule: " + e.getMessage()));
        }
    }

    // Get company rebuild payroll schedule
    @GetMapping("/{companyId}/rebuild-payroll-schedule")
    public ResponseEntity<?> getCompanyRebuildPayrollSchedule(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> rebuildSchedule = companyService.getCompanyRebuildPayrollSchedule(userId);
            return ResponseEntity.ok(rebuildSchedule);
            
        } catch (Exception e) {
            logger.error("Error getting company rebuild payroll schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company rebuild payroll schedule: " + e.getMessage()));
        }
    }

    // Update company rebuild payroll schedule
    @PutMapping("/{companyId}/rebuild-payroll-schedule")
    public ResponseEntity<?> updateCompanyRebuildPayrollSchedule(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> scheduleData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedRebuildSchedule = companyService.updateCompanyRebuildPayrollSchedule(userId, scheduleData);
            return ResponseEntity.ok(updatedRebuildSchedule);
            
        } catch (Exception e) {
            logger.error("Error updating company rebuild payroll schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company rebuild payroll schedule: " + e.getMessage()));
        }
    }

    // Get company pay frequency
    @GetMapping("/{companyId}/pay-frequency")
    public ResponseEntity<?> getCompanyPayFrequency(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> payFrequency = companyService.getCompanyPayFrequency(userId);
            return ResponseEntity.ok(payFrequency);
            
        } catch (Exception e) {
            logger.error("Error getting company pay frequency: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company pay frequency: " + e.getMessage()));
        }
    }

    // Update company pay frequency
    @PutMapping("/{companyId}/pay-frequency")
    public ResponseEntity<?> updateCompanyPayFrequency(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> frequencyData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedPayFrequency = companyService.updateCompanyPayFrequency(userId, frequencyData);
            return ResponseEntity.ok(updatedPayFrequency);
            
        } catch (Exception e) {
            logger.error("Error updating company pay frequency: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company pay frequency: " + e.getMessage()));
        }
    }

    // Get company taxes
    @GetMapping("/{companyId}/taxes")
    public ResponseEntity<?> getCompanyTaxes(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> taxes = companyService.getCompanyTaxes(userId);
            return ResponseEntity.ok(taxes);
            
        } catch (Exception e) {
            logger.error("Error getting company taxes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get company taxes: " + e.getMessage()));
        }
    }

    // Update company taxes
    @PutMapping("/{companyId}/taxes")
    public ResponseEntity<?> updateCompanyTaxes(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> taxesData) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);
            
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            Map<String, Object> userInfo = (Map<String, Object>) validationResult.get("userInfo");
            Long userId = (Long) userInfo.get("id");
            
            Map<String, Object> updatedTaxes = companyService.updateCompanyTaxes(userId, taxesData);
            return ResponseEntity.ok(updatedTaxes);
            
        } catch (Exception e) {
            logger.error("Error updating company taxes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update company taxes: " + e.getMessage()));
        }
    }
} 
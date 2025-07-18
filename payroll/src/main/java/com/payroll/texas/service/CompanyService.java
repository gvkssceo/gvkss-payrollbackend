package com.payroll.texas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.texas.model.Company;
import com.payroll.texas.model.User;
import com.payroll.texas.repository.CompanyRepository;
import com.payroll.texas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompanyService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CustomFieldsService customFieldsService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get company by user ID
     */
    public Company getCompanyByUserId(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return user.getCompany();
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting company by user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company by user ID", e);
        }
    }

    /**
     * Update company profile
     */
    public Company updateCompanyProfile(Long userId, Map<String, Object> updateData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            // Update basic fields
            if (updateData.containsKey("name")) {
                company.setName((String) updateData.get("name"));
            }
            if (updateData.containsKey("legalName")) {
                company.setLegalName((String) updateData.get("legalName"));
            }
            if (updateData.containsKey("email")) {
                company.setEmail((String) updateData.get("email"));
            }
            if (updateData.containsKey("phone")) {
                company.setPhone((String) updateData.get("phone"));
            }
            if (updateData.containsKey("addressLine1")) {
                company.setAddressLine1((String) updateData.get("addressLine1"));
            }
            if (updateData.containsKey("addressLine2")) {
                company.setAddressLine2((String) updateData.get("addressLine2"));
            }
            if (updateData.containsKey("city")) {
                company.setCity((String) updateData.get("city"));
            }
            if (updateData.containsKey("state")) {
                company.setState((String) updateData.get("state"));
            }
            if (updateData.containsKey("zipCode")) {
                company.setZipCode((String) updateData.get("zipCode"));
            }
            if (updateData.containsKey("country")) {
                company.setCountry((String) updateData.get("country"));
            }
            if (updateData.containsKey("ein")) {
                company.setEin((String) updateData.get("ein"));
            }

            company.setUpdatedAt(LocalDateTime.now());
            return companyRepository.save(company);
            
        } catch (Exception e) {
            logger.error("Error updating company profile for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company profile", e);
        }
    }

    /**
     * Get company bank information
     */
    public Map<String, Object> getCompanyBankInfo(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> bankInfo = new HashMap<>();
            bankInfo.put("bankName", customFields.getOrDefault("bankName", ""));
            bankInfo.put("accountNumber", customFields.getOrDefault("accountNumber", ""));
            bankInfo.put("routingNumber", customFields.getOrDefault("routingNumber", ""));
            bankInfo.put("accountType", customFields.getOrDefault("accountType", ""));
            bankInfo.put("bankAddress", customFields.getOrDefault("bankAddress", ""));
            bankInfo.put("isVerified", customFields.getOrDefault("isVerified", false));
            
            return bankInfo;
            
        } catch (Exception e) {
            logger.error("Error getting company bank info for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company bank info", e);
        }
    }

    /**
     * Update company bank information
     */
    public Map<String, Object> updateCompanyBankInfo(Long userId, Map<String, Object> bankData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update bank information in custom fields
            if (bankData.containsKey("bankName")) {
                customFields.put("bankName", bankData.get("bankName"));
            }
            if (bankData.containsKey("accountNumber")) {
                customFields.put("accountNumber", bankData.get("accountNumber"));
            }
            if (bankData.containsKey("routingNumber")) {
                customFields.put("routingNumber", bankData.get("routingNumber"));
            }
            if (bankData.containsKey("accountType")) {
                customFields.put("accountType", bankData.get("accountType"));
            }
            if (bankData.containsKey("bankAddress")) {
                customFields.put("bankAddress", bankData.get("bankAddress"));
            }
            if (bankData.containsKey("isVerified")) {
                customFields.put("isVerified", bankData.get("isVerified"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanyBankInfo(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company bank info for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company bank info", e);
        }
    }

    /**
     * Get company settings
     */
    public Map<String, Object> getCompanySettings(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("thirdPartyAccess", customFields.getOrDefault("thirdPartyAccess", false));
            settings.put("salariedHoursReporting", customFields.getOrDefault("salariedHoursReporting", false));
            settings.put("autoPayroll", customFields.getOrDefault("autoPayroll", false));
            settings.put("emailNotifications", customFields.getOrDefault("emailNotifications", true));
            settings.put("smsNotifications", customFields.getOrDefault("smsNotifications", false));
            settings.put("twoFactorAuth", customFields.getOrDefault("twoFactorAuth", false));
            settings.put("sessionTimeout", customFields.getOrDefault("sessionTimeout", 30));
            settings.put("timezone", customFields.getOrDefault("timezone", "America/Chicago"));
            settings.put("dateFormat", customFields.getOrDefault("dateFormat", "MM/dd/yyyy"));
            settings.put("currency", customFields.getOrDefault("currency", "USD"));
            
            return settings;
            
        } catch (Exception e) {
            logger.error("Error getting company settings for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company settings", e);
        }
    }

    /**
     * Update company settings
     */
    public Map<String, Object> updateCompanySettings(Long userId, Map<String, Object> settingsData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update settings in custom fields
            if (settingsData.containsKey("thirdPartyAccess")) {
                customFields.put("thirdPartyAccess", settingsData.get("thirdPartyAccess"));
            }
            if (settingsData.containsKey("salariedHoursReporting")) {
                customFields.put("salariedHoursReporting", settingsData.get("salariedHoursReporting"));
            }
            if (settingsData.containsKey("autoPayroll")) {
                customFields.put("autoPayroll", settingsData.get("autoPayroll"));
            }
            if (settingsData.containsKey("emailNotifications")) {
                customFields.put("emailNotifications", settingsData.get("emailNotifications"));
            }
            if (settingsData.containsKey("smsNotifications")) {
                customFields.put("smsNotifications", settingsData.get("smsNotifications"));
            }
            if (settingsData.containsKey("twoFactorAuth")) {
                customFields.put("twoFactorAuth", settingsData.get("twoFactorAuth"));
            }
            if (settingsData.containsKey("sessionTimeout")) {
                customFields.put("sessionTimeout", settingsData.get("sessionTimeout"));
            }
            if (settingsData.containsKey("timezone")) {
                customFields.put("timezone", settingsData.get("timezone"));
            }
            if (settingsData.containsKey("dateFormat")) {
                customFields.put("dateFormat", settingsData.get("dateFormat"));
            }
            if (settingsData.containsKey("currency")) {
                customFields.put("currency", settingsData.get("currency"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanySettings(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company settings for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company settings", e);
        }
    }

    /**
     * Get company users
     */
    public List<Map<String, Object>> getCompanyUsers(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            List<User> users = company.getUsers();
            return users.stream()
                .map(user -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("firstName", user.getFirstName());
                    userInfo.put("lastName", user.getLastName());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("phone", user.getPhone());
                    userInfo.put("userType", user.getUserType());
                    userInfo.put("status", user.getStatus());
                    userInfo.put("role", user.getUserType()); // Using userType as role for now
                    return userInfo;
                })
                .toList();
            
        } catch (Exception e) {
            logger.error("Error getting company users for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company users", e);
        }
    }

    /**
     * Get company by ID
     */
    public Company getCompanyById(Long companyId) {
        try {
            Optional<Company> companyOpt = companyRepository.findById(companyId);
            return companyOpt.orElse(null);
        } catch (Exception e) {
            logger.error("Error getting company by ID {}: {}", companyId, e.getMessage());
            throw new RuntimeException("Failed to get company by ID", e);
        }
    }

    /**
     * Get company workers compensation information
     */
    public Map<String, Object> getCompanyWorkersCompensation(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> workersCompInfo = new HashMap<>();
            workersCompInfo.put("carrierName", customFields.getOrDefault("workersCompCarrierName", ""));
            workersCompInfo.put("policyNumber", customFields.getOrDefault("workersCompPolicyNumber", ""));
            workersCompInfo.put("effectiveDate", customFields.getOrDefault("workersCompEffectiveDate", ""));
            workersCompInfo.put("expirationDate", customFields.getOrDefault("workersCompExpirationDate", ""));
            workersCompInfo.put("rate", customFields.getOrDefault("workersCompRate", 0.0));
            workersCompInfo.put("classification", customFields.getOrDefault("workersCompClassification", ""));
            workersCompInfo.put("isActive", customFields.getOrDefault("workersCompIsActive", false));
            
            return workersCompInfo;
            
        } catch (Exception e) {
            logger.error("Error getting company workers compensation for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company workers compensation", e);
        }
    }

    /**
     * Update company workers compensation information
     */
    public Map<String, Object> updateCompanyWorkersCompensation(Long userId, Map<String, Object> workersCompData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update workers compensation information in custom fields
            if (workersCompData.containsKey("carrierName")) {
                customFields.put("workersCompCarrierName", workersCompData.get("carrierName"));
            }
            if (workersCompData.containsKey("policyNumber")) {
                customFields.put("workersCompPolicyNumber", workersCompData.get("policyNumber"));
            }
            if (workersCompData.containsKey("effectiveDate")) {
                customFields.put("workersCompEffectiveDate", workersCompData.get("effectiveDate"));
            }
            if (workersCompData.containsKey("expirationDate")) {
                customFields.put("workersCompExpirationDate", workersCompData.get("expirationDate"));
            }
            if (workersCompData.containsKey("rate")) {
                customFields.put("workersCompRate", workersCompData.get("rate"));
            }
            if (workersCompData.containsKey("classification")) {
                customFields.put("workersCompClassification", workersCompData.get("classification"));
            }
            if (workersCompData.containsKey("isActive")) {
                customFields.put("workersCompIsActive", workersCompData.get("isActive"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanyWorkersCompensation(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company workers compensation for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company workers compensation", e);
        }
    }

    /**
     * Get company earnings information
     */
    public Map<String, Object> getCompanyEarnings(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> earningsInfo = new HashMap<>();
            earningsInfo.put("regularHours", customFields.getOrDefault("earningsRegularHours", 0.0));
            earningsInfo.put("overtimeRate", customFields.getOrDefault("earningsOvertimeRate", 1.5));
            earningsInfo.put("holidayRate", customFields.getOrDefault("earningsHolidayRate", 1.5));
            earningsInfo.put("weekendRate", customFields.getOrDefault("earningsWeekendRate", 1.0));
            earningsInfo.put("nightShiftRate", customFields.getOrDefault("earningsNightShiftRate", 1.0));
            earningsInfo.put("bonusRate", customFields.getOrDefault("earningsBonusRate", 1.0));
            earningsInfo.put("commissionRate", customFields.getOrDefault("earningsCommissionRate", 0.0));
            
            return earningsInfo;
            
        } catch (Exception e) {
            logger.error("Error getting company earnings for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company earnings", e);
        }
    }

    /**
     * Update company earnings information
     */
    public Map<String, Object> updateCompanyEarnings(Long userId, Map<String, Object> earningsData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update earnings information in custom fields
            if (earningsData.containsKey("regularHours")) {
                customFields.put("earningsRegularHours", earningsData.get("regularHours"));
            }
            if (earningsData.containsKey("overtimeRate")) {
                customFields.put("earningsOvertimeRate", earningsData.get("overtimeRate"));
            }
            if (earningsData.containsKey("holidayRate")) {
                customFields.put("earningsHolidayRate", earningsData.get("holidayRate"));
            }
            if (earningsData.containsKey("weekendRate")) {
                customFields.put("earningsWeekendRate", earningsData.get("weekendRate"));
            }
            if (earningsData.containsKey("nightShiftRate")) {
                customFields.put("earningsNightShiftRate", earningsData.get("nightShiftRate"));
            }
            if (earningsData.containsKey("bonusRate")) {
                customFields.put("earningsBonusRate", earningsData.get("bonusRate"));
            }
            if (earningsData.containsKey("commissionRate")) {
                customFields.put("earningsCommissionRate", earningsData.get("commissionRate"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanyEarnings(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company earnings for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company earnings", e);
        }
    }

    /**
     * Get company deductions information
     */
    public Map<String, Object> getCompanyDeductions(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> deductionsInfo = new HashMap<>();
            deductionsInfo.put("federalTaxRate", customFields.getOrDefault("deductionsFederalTaxRate", 0.0));
            deductionsInfo.put("stateTaxRate", customFields.getOrDefault("deductionsStateTaxRate", 0.0));
            deductionsInfo.put("localTaxRate", customFields.getOrDefault("deductionsLocalTaxRate", 0.0));
            deductionsInfo.put("socialSecurityRate", customFields.getOrDefault("deductionsSocialSecurityRate", 6.2));
            deductionsInfo.put("medicareRate", customFields.getOrDefault("deductionsMedicareRate", 1.45));
            deductionsInfo.put("retirementRate", customFields.getOrDefault("deductionsRetirementRate", 0.0));
            deductionsInfo.put("healthInsuranceRate", customFields.getOrDefault("deductionsHealthInsuranceRate", 0.0));
            deductionsInfo.put("otherDeductions", customFields.getOrDefault("deductionsOther", 0.0));
            
            return deductionsInfo;
            
        } catch (Exception e) {
            logger.error("Error getting company deductions for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company deductions", e);
        }
    }

    /**
     * Update company deductions information
     */
    public Map<String, Object> updateCompanyDeductions(Long userId, Map<String, Object> deductionsData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update deductions information in custom fields
            if (deductionsData.containsKey("federalTaxRate")) {
                customFields.put("deductionsFederalTaxRate", deductionsData.get("federalTaxRate"));
            }
            if (deductionsData.containsKey("stateTaxRate")) {
                customFields.put("deductionsStateTaxRate", deductionsData.get("stateTaxRate"));
            }
            if (deductionsData.containsKey("localTaxRate")) {
                customFields.put("deductionsLocalTaxRate", deductionsData.get("localTaxRate"));
            }
            if (deductionsData.containsKey("socialSecurityRate")) {
                customFields.put("deductionsSocialSecurityRate", deductionsData.get("socialSecurityRate"));
            }
            if (deductionsData.containsKey("medicareRate")) {
                customFields.put("deductionsMedicareRate", deductionsData.get("medicareRate"));
            }
            if (deductionsData.containsKey("retirementRate")) {
                customFields.put("deductionsRetirementRate", deductionsData.get("retirementRate"));
            }
            if (deductionsData.containsKey("healthInsuranceRate")) {
                customFields.put("deductionsHealthInsuranceRate", deductionsData.get("healthInsuranceRate"));
            }
            if (deductionsData.containsKey("otherDeductions")) {
                customFields.put("deductionsOther", deductionsData.get("otherDeductions"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanyDeductions(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company deductions for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company deductions", e);
        }
    }

    /**
     * Get company payroll schedule
     */
    public Map<String, Object> getCompanyPayrollSchedule(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> payrollSchedule = new HashMap<>();
            payrollSchedule.put("frequency", customFields.getOrDefault("payrollScheduleFrequency", "BI_WEEKLY"));
            payrollSchedule.put("startDate", customFields.getOrDefault("payrollScheduleStartDate", ""));
            payrollSchedule.put("endDate", customFields.getOrDefault("payrollScheduleEndDate", ""));
            payrollSchedule.put("payDay", customFields.getOrDefault("payrollSchedulePayDay", "FRIDAY"));
            payrollSchedule.put("isActive", customFields.getOrDefault("payrollScheduleIsActive", true));
            
            return payrollSchedule;
            
        } catch (Exception e) {
            logger.error("Error getting company payroll schedule for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company payroll schedule", e);
        }
    }

    /**
     * Update company payroll schedule
     */
    public Map<String, Object> updateCompanyPayrollSchedule(Long userId, Map<String, Object> scheduleData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update payroll schedule in custom fields
            if (scheduleData.containsKey("frequency")) {
                customFields.put("payrollScheduleFrequency", scheduleData.get("frequency"));
            }
            if (scheduleData.containsKey("startDate")) {
                customFields.put("payrollScheduleStartDate", scheduleData.get("startDate"));
            }
            if (scheduleData.containsKey("endDate")) {
                customFields.put("payrollScheduleEndDate", scheduleData.get("endDate"));
            }
            if (scheduleData.containsKey("payDay")) {
                customFields.put("payrollSchedulePayDay", scheduleData.get("payDay"));
            }
            if (scheduleData.containsKey("isActive")) {
                customFields.put("payrollScheduleIsActive", scheduleData.get("isActive"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanyPayrollSchedule(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company payroll schedule for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company payroll schedule", e);
        }
    }

    /**
     * Get company rebuild payroll schedule
     */
    public Map<String, Object> getCompanyRebuildPayrollSchedule(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> rebuildSchedule = new HashMap<>();
            rebuildSchedule.put("rebuildFrequency", customFields.getOrDefault("rebuildScheduleFrequency", "MONTHLY"));
            rebuildSchedule.put("rebuildStartDate", customFields.getOrDefault("rebuildScheduleStartDate", ""));
            rebuildSchedule.put("rebuildEndDate", customFields.getOrDefault("rebuildScheduleEndDate", ""));
            rebuildSchedule.put("isRebuildActive", customFields.getOrDefault("rebuildScheduleIsActive", false));
            
            return rebuildSchedule;
            
        } catch (Exception e) {
            logger.error("Error getting company rebuild payroll schedule for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company rebuild payroll schedule", e);
        }
    }

    /**
     * Update company rebuild payroll schedule
     */
    public Map<String, Object> updateCompanyRebuildPayrollSchedule(Long userId, Map<String, Object> scheduleData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update rebuild payroll schedule in custom fields
            if (scheduleData.containsKey("rebuildFrequency")) {
                customFields.put("rebuildScheduleFrequency", scheduleData.get("rebuildFrequency"));
            }
            if (scheduleData.containsKey("rebuildStartDate")) {
                customFields.put("rebuildScheduleStartDate", scheduleData.get("rebuildStartDate"));
            }
            if (scheduleData.containsKey("rebuildEndDate")) {
                customFields.put("rebuildScheduleEndDate", scheduleData.get("rebuildEndDate"));
            }
            if (scheduleData.containsKey("isRebuildActive")) {
                customFields.put("rebuildScheduleIsActive", scheduleData.get("isRebuildActive"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanyRebuildPayrollSchedule(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company rebuild payroll schedule for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company rebuild payroll schedule", e);
        }
    }

    /**
     * Get company pay frequency
     */
    public Map<String, Object> getCompanyPayFrequency(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> payFrequency = new HashMap<>();
            payFrequency.put("frequency", customFields.getOrDefault("payFrequency", "BI_WEEKLY"));
            payFrequency.put("payDay", customFields.getOrDefault("payFrequencyPayDay", "FRIDAY"));
            payFrequency.put("isActive", customFields.getOrDefault("payFrequencyIsActive", true));
            
            return payFrequency;
            
        } catch (Exception e) {
            logger.error("Error getting company pay frequency for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company pay frequency", e);
        }
    }

    /**
     * Update company pay frequency
     */
    public Map<String, Object> updateCompanyPayFrequency(Long userId, Map<String, Object> frequencyData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update pay frequency in custom fields
            if (frequencyData.containsKey("frequency")) {
                customFields.put("payFrequency", frequencyData.get("frequency"));
            }
            if (frequencyData.containsKey("payDay")) {
                customFields.put("payFrequencyPayDay", frequencyData.get("payDay"));
            }
            if (frequencyData.containsKey("isActive")) {
                customFields.put("payFrequencyIsActive", frequencyData.get("isActive"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanyPayFrequency(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company pay frequency for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company pay frequency", e);
        }
    }

    /**
     * Get company taxes
     */
    public Map<String, Object> getCompanyTaxes(Long userId) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            Map<String, Object> taxes = new HashMap<>();
            taxes.put("federalTaxId", customFields.getOrDefault("taxesFederalTaxId", ""));
            taxes.put("stateTaxId", customFields.getOrDefault("taxesStateTaxId", ""));
            taxes.put("localTaxId", customFields.getOrDefault("taxesLocalTaxId", ""));
            taxes.put("federalTaxRate", customFields.getOrDefault("taxesFederalTaxRate", 0.0));
            taxes.put("stateTaxRate", customFields.getOrDefault("taxesStateTaxRate", 0.0));
            taxes.put("localTaxRate", customFields.getOrDefault("taxesLocalTaxRate", 0.0));
            taxes.put("isActive", customFields.getOrDefault("taxesIsActive", true));
            
            return taxes;
            
        } catch (Exception e) {
            logger.error("Error getting company taxes for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get company taxes", e);
        }
    }

    /**
     * Update company taxes
     */
    public Map<String, Object> updateCompanyTaxes(Long userId, Map<String, Object> taxesData) {
        try {
            Company company = getCompanyByUserId(userId);
            if (company == null) {
                throw new RuntimeException("Company not found for user ID: " + userId);
            }

            Map<String, Object> customFields = customFieldsService.getAllCustomFields(company.getCustomFields());
            
            // Update taxes in custom fields
            if (taxesData.containsKey("federalTaxId")) {
                customFields.put("taxesFederalTaxId", taxesData.get("federalTaxId"));
            }
            if (taxesData.containsKey("stateTaxId")) {
                customFields.put("taxesStateTaxId", taxesData.get("stateTaxId"));
            }
            if (taxesData.containsKey("localTaxId")) {
                customFields.put("taxesLocalTaxId", taxesData.get("localTaxId"));
            }
            if (taxesData.containsKey("federalTaxRate")) {
                customFields.put("taxesFederalTaxRate", taxesData.get("federalTaxRate"));
            }
            if (taxesData.containsKey("stateTaxRate")) {
                customFields.put("taxesStateTaxRate", taxesData.get("stateTaxRate"));
            }
            if (taxesData.containsKey("localTaxRate")) {
                customFields.put("taxesLocalTaxRate", taxesData.get("localTaxRate"));
            }
            if (taxesData.containsKey("isActive")) {
                customFields.put("taxesIsActive", taxesData.get("isActive"));
            }

            // Save updated custom fields
            String updatedCustomFields = objectMapper.writeValueAsString(customFields);
            company.setCustomFields(updatedCustomFields);
            company.setUpdatedAt(LocalDateTime.now());
            companyRepository.save(company);

            return getCompanyTaxes(userId);
            
        } catch (Exception e) {
            logger.error("Error updating company taxes for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update company taxes", e);
        }
    }
} 
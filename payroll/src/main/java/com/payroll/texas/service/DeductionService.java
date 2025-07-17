package com.payroll.texas.service;

import com.payroll.texas.model.Company;
import com.payroll.texas.model.Deduction;
import com.payroll.texas.repository.CompanyRepository;
import com.payroll.texas.repository.DeductionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class DeductionService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeductionService.class);
    
    @Autowired
    private DeductionRepository deductionRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    /**
     * Get all active deductions for a company
     */
    public List<Deduction> getDeductionsByCompanyId(Long companyId) {
        logger.info("Fetching deductions for company: {}", companyId);
        
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        
        // Verify company exists
        if (!companyRepository.existsById(companyId)) {
            throw new IllegalArgumentException("Company not found with ID: " + companyId);
        }
        
        List<Deduction> deductions = deductionRepository.findByCompanyIdAndActive(companyId);
        logger.info("Found {} deductions for company: {}", deductions.size(), companyId);
        return deductions;
    }
    
    /**
     * Get a specific deduction by ID and company ID
     */
    public Optional<Deduction> getDeductionByIdAndCompanyId(Long deductionId, Long companyId) {
        logger.info("Fetching deduction {} for company: {}", deductionId, companyId);
        
        if (deductionId == null || companyId == null) {
            throw new IllegalArgumentException("Deduction ID and Company ID cannot be null");
        }
        
        return deductionRepository.findByIdAndCompanyId(deductionId, companyId);
    }
    
    /**
     * Create a new deduction for a company
     */
    public Deduction createDeduction(Long companyId, Map<String, Object> deductionData) {
        logger.info("Creating new deduction for company: {}", companyId);
        
        // Validate input
        validateDeductionData(deductionData);
        
        // Get company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
        
        String type = (String) deductionData.get("type");
        String description = (String) deductionData.get("description");
        String abbreviation = (String) deductionData.get("abbreviation");
        Boolean isPercentage = (Boolean) deductionData.get("isPercentage");
        Boolean isRequired = (Boolean) deductionData.get("isRequired");
        
        // Check if deduction type already exists
        if (deductionRepository.existsByCompanyIdAndTypeIgnoreCase(companyId, type)) {
            throw new IllegalArgumentException("Deduction type '" + type + "' already exists for this company");
        }
        
        // Create new deduction
        Deduction deduction = new Deduction(type, description, abbreviation, company);
        deduction.setIsPercentage(isPercentage != null ? isPercentage : false);
        deduction.setIsRequired(isRequired != null ? isRequired : false);
        
        // Set amount or percentage based on isPercentage flag
        if (isPercentage != null && isPercentage) {
            Object percentageObj = deductionData.get("percentage");
            if (percentageObj != null) {
                BigDecimal percentage = new BigDecimal(percentageObj.toString());
                deduction.setPercentage(percentage);
            }
        } else {
            Object amountObj = deductionData.get("amount");
            if (amountObj != null) {
                BigDecimal amount = new BigDecimal(amountObj.toString());
                deduction.setAmount(amount);
            }
        }
        
        Deduction savedDeduction = deductionRepository.save(deduction);
        
        logger.info("Successfully created deduction: {} for company: {}", savedDeduction.getId(), companyId);
        return savedDeduction;
    }
    
    /**
     * Update an existing deduction
     */
    public Deduction updateDeduction(Long companyId, Long deductionId, Map<String, Object> deductionData) {
        logger.info("Updating deduction {} for company: {}", deductionId, companyId);
        
        // Validate input
        validateDeductionData(deductionData);
        
        // Get existing deduction
        Deduction existingDeduction = deductionRepository.findByIdAndCompanyId(deductionId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Deduction not found with ID: " + deductionId + " for company: " + companyId));
        
        String type = (String) deductionData.get("type");
        String description = (String) deductionData.get("description");
        String abbreviation = (String) deductionData.get("abbreviation");
        Boolean isPercentage = (Boolean) deductionData.get("isPercentage");
        Boolean isRequired = (Boolean) deductionData.get("isRequired");
        
        // Check if deduction type already exists (excluding current deduction)
        if (deductionRepository.existsByCompanyIdAndTypeIgnoreCase(companyId, type, deductionId)) {
            throw new IllegalArgumentException("Deduction type '" + type + "' already exists for this company");
        }
        
        // Update fields
        existingDeduction.setType(type);
        existingDeduction.setDescription(description);
        existingDeduction.setAbbreviation(abbreviation);
        existingDeduction.setIsPercentage(isPercentage != null ? isPercentage : false);
        existingDeduction.setIsRequired(isRequired != null ? isRequired : false);
        
        // Update amount or percentage based on isPercentage flag
        if (isPercentage != null && isPercentage) {
            Object percentageObj = deductionData.get("percentage");
            if (percentageObj != null) {
                BigDecimal percentage = new BigDecimal(percentageObj.toString());
                existingDeduction.setPercentage(percentage);
                existingDeduction.setAmount(null); // Clear amount when using percentage
            }
        } else {
            Object amountObj = deductionData.get("amount");
            if (amountObj != null) {
                BigDecimal amount = new BigDecimal(amountObj.toString());
                existingDeduction.setAmount(amount);
                existingDeduction.setPercentage(null); // Clear percentage when using amount
            }
        }
        
        Deduction updatedDeduction = deductionRepository.save(existingDeduction);
        
        logger.info("Successfully updated deduction: {} for company: {}", deductionId, companyId);
        return updatedDeduction;
    }
    
    /**
     * Delete a deduction (soft delete by setting isActive to false)
     */
    public void deleteDeduction(Long companyId, Long deductionId) {
        logger.info("Deleting deduction {} for company: {}", deductionId, companyId);
        
        // Get existing deduction
        Deduction existingDeduction = deductionRepository.findByIdAndCompanyId(deductionId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Deduction not found with ID: " + deductionId + " for company: " + companyId));
        
        // Soft delete
        existingDeduction.setIsActive(false);
        deductionRepository.save(existingDeduction);
        
        logger.info("Successfully deleted deduction: {} for company: {}", deductionId, companyId);
    }
    
    /**
     * Search deductions by type
     */
    public List<Deduction> searchDeductionsByType(Long companyId, String type) {
        logger.info("Searching deductions by type '{}' for company: {}", type, companyId);
        
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        
        if (type == null || type.trim().isEmpty()) {
            return getDeductionsByCompanyId(companyId);
        }
        
        List<Deduction> deductions = deductionRepository.findByCompanyIdAndTypeContainingIgnoreCase(companyId, type.trim());
        logger.info("Found {} deductions matching '{}' for company: {}", deductions.size(), type, companyId);
        return deductions;
    }
    
    /**
     * Get required deductions for a company
     */
    public List<Deduction> getRequiredDeductionsByCompanyId(Long companyId) {
        logger.info("Fetching required deductions for company: {}", companyId);
        
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        
        List<Deduction> deductions = deductionRepository.findRequiredDeductionsByCompanyId(companyId);
        logger.info("Found {} required deductions for company: {}", deductions.size(), companyId);
        return deductions;
    }
    
    /**
     * Validate deduction data
     */
    private void validateDeductionData(Map<String, Object> deductionData) {
        if (deductionData == null) {
            throw new IllegalArgumentException("Deduction data cannot be null");
        }
        
        String type = (String) deductionData.get("type");
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Deduction type is required");
        }
        
        if (type.trim().length() > 100) {
            throw new IllegalArgumentException("Deduction type cannot exceed 100 characters");
        }
        
        String description = (String) deductionData.get("description");
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Deduction description cannot exceed 500 characters");
        }
        
        String abbreviation = (String) deductionData.get("abbreviation");
        if (abbreviation != null && abbreviation.length() > 10) {
            throw new IllegalArgumentException("Deduction abbreviation cannot exceed 10 characters");
        }
        
        Boolean isPercentage = (Boolean) deductionData.get("isPercentage");
        if (isPercentage != null && isPercentage) {
            Object percentageObj = deductionData.get("percentage");
            if (percentageObj != null) {
                try {
                    BigDecimal percentage = new BigDecimal(percentageObj.toString());
                    if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
                        throw new IllegalArgumentException("Percentage must be between 0 and 100");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid percentage value");
                }
            }
        } else {
            Object amountObj = deductionData.get("amount");
            if (amountObj != null) {
                try {
                    BigDecimal amount = new BigDecimal(amountObj.toString());
                    if (amount.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("Amount cannot be negative");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid amount value");
                }
            }
        }
    }
} 
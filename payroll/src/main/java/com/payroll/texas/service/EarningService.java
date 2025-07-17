package com.payroll.texas.service;

import com.payroll.texas.model.Company;
import com.payroll.texas.model.Earning;
import com.payroll.texas.repository.CompanyRepository;
import com.payroll.texas.repository.EarningRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class EarningService {
    
    private static final Logger logger = LoggerFactory.getLogger(EarningService.class);
    
    @Autowired
    private EarningRepository earningRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    /**
     * Get all active earnings for a company
     */
    public List<Earning> getEarningsByCompanyId(Long companyId) {
        logger.info("Fetching earnings for company: {}", companyId);
        
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        
        // Verify company exists
        if (!companyRepository.existsById(companyId)) {
            throw new IllegalArgumentException("Company not found with ID: " + companyId);
        }
        
        List<Earning> earnings = earningRepository.findByCompanyIdAndActive(companyId);
        logger.info("Found {} earnings for company: {}", earnings.size(), companyId);
        return earnings;
    }
    
    /**
     * Get a specific earning by ID and company ID
     */
    public Optional<Earning> getEarningByIdAndCompanyId(Long earningId, Long companyId) {
        logger.info("Fetching earning {} for company: {}", earningId, companyId);
        
        if (earningId == null || companyId == null) {
            throw new IllegalArgumentException("Earning ID and Company ID cannot be null");
        }
        
        return earningRepository.findByIdAndCompanyId(earningId, companyId);
    }
    
    /**
     * Create a new earning for a company
     */
    public Earning createEarning(Long companyId, Map<String, Object> earningData) {
        logger.info("Creating new earning for company: {}", companyId);
        
        // Validate input
        validateEarningData(earningData);
        
        // Get company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
        
        String type = (String) earningData.get("type");
        String description = (String) earningData.get("description");
        String abbreviation = (String) earningData.get("abbreviation");
        
        // Check if earning type already exists
        if (earningRepository.existsByCompanyIdAndTypeIgnoreCase(companyId, type)) {
            throw new IllegalArgumentException("Earning type '" + type + "' already exists for this company");
        }
        
        // Create new earning
        Earning earning = new Earning(type, description, abbreviation, company);
        Earning savedEarning = earningRepository.save(earning);
        
        logger.info("Successfully created earning: {} for company: {}", savedEarning.getId(), companyId);
        return savedEarning;
    }
    
    /**
     * Update an existing earning
     */
    public Earning updateEarning(Long companyId, Long earningId, Map<String, Object> earningData) {
        logger.info("Updating earning {} for company: {}", earningId, companyId);
        
        // Validate input
        validateEarningData(earningData);
        
        // Get existing earning
        Earning existingEarning = earningRepository.findByIdAndCompanyId(earningId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Earning not found with ID: " + earningId + " for company: " + companyId));
        
        String type = (String) earningData.get("type");
        String description = (String) earningData.get("description");
        String abbreviation = (String) earningData.get("abbreviation");
        
        // Check if earning type already exists (excluding current earning)
        if (earningRepository.existsByCompanyIdAndTypeIgnoreCase(companyId, type, earningId)) {
            throw new IllegalArgumentException("Earning type '" + type + "' already exists for this company");
        }
        
        // Update fields
        existingEarning.setType(type);
        existingEarning.setDescription(description);
        existingEarning.setAbbreviation(abbreviation);
        
        Earning updatedEarning = earningRepository.save(existingEarning);
        
        logger.info("Successfully updated earning: {} for company: {}", earningId, companyId);
        return updatedEarning;
    }
    
    /**
     * Delete an earning (soft delete by setting isActive to false)
     */
    public void deleteEarning(Long companyId, Long earningId) {
        logger.info("Deleting earning {} for company: {}", earningId, companyId);
        
        // Get existing earning
        Earning existingEarning = earningRepository.findByIdAndCompanyId(earningId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Earning not found with ID: " + earningId + " for company: " + companyId));
        
        // Soft delete
        existingEarning.setIsActive(false);
        earningRepository.save(existingEarning);
        
        logger.info("Successfully deleted earning: {} for company: {}", earningId, companyId);
    }
    
    /**
     * Search earnings by type
     */
    public List<Earning> searchEarningsByType(Long companyId, String type) {
        logger.info("Searching earnings by type '{}' for company: {}", type, companyId);
        
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        
        if (type == null || type.trim().isEmpty()) {
            return getEarningsByCompanyId(companyId);
        }
        
        List<Earning> earnings = earningRepository.findByCompanyIdAndTypeContainingIgnoreCase(companyId, type.trim());
        logger.info("Found {} earnings matching '{}' for company: {}", earnings.size(), type, companyId);
        return earnings;
    }
    
    /**
     * Validate earning data
     */
    private void validateEarningData(Map<String, Object> earningData) {
        if (earningData == null) {
            throw new IllegalArgumentException("Earning data cannot be null");
        }
        
        String type = (String) earningData.get("type");
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Earning type is required");
        }
        
        if (type.trim().length() > 100) {
            throw new IllegalArgumentException("Earning type cannot exceed 100 characters");
        }
        
        String description = (String) earningData.get("description");
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Earning description cannot exceed 500 characters");
        }
        
        String abbreviation = (String) earningData.get("abbreviation");
        if (abbreviation != null && abbreviation.length() > 10) {
            throw new IllegalArgumentException("Earning abbreviation cannot exceed 10 characters");
        }
    }
} 
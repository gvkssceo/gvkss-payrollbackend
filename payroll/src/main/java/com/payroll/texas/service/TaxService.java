package com.payroll.texas.service;

import com.payroll.texas.model.Company;
import com.payroll.texas.model.Tax;
import com.payroll.texas.model.TaxType;
import com.payroll.texas.repository.CompanyRepository;
import com.payroll.texas.repository.TaxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TaxService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaxService.class);
    
    @Autowired
    private TaxRepository taxRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    /**
     * Get all taxes for a company
     */
    public List<Tax> getTaxesByCompanyId(Long companyId) {
        logger.info("Fetching taxes for company: {}", companyId);
        
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        
        // Verify company exists
        if (!companyRepository.existsById(companyId)) {
            throw new IllegalArgumentException("Company not found with ID: " + companyId);
        }
        
        List<Tax> taxes = taxRepository.findByCompanyIdAndActive(companyId);
        logger.info("Found {} taxes for company: {}", taxes.size(), companyId);
        return taxes;
    }
    
    /**
     * Get taxes by level (FEDERAL, STATE, LOCAL) for a company
     */
    public List<Tax> getTaxesByCompanyIdAndLevel(Long companyId, String taxLevel) {
        logger.info("Fetching {} taxes for company: {}", taxLevel, companyId);
        
        if (companyId == null || taxLevel == null) {
            throw new IllegalArgumentException("Company ID and tax level cannot be null");
        }
        
        // Verify company exists
        if (!companyRepository.existsById(companyId)) {
            throw new IllegalArgumentException("Company not found with ID: " + companyId);
        }
        
        List<Tax> taxes = taxRepository.findByCompanyIdAndTaxLevel(companyId, taxLevel);
        logger.info("Found {} {} taxes for company: {}", taxes.size(), taxLevel, companyId);
        return taxes;
    }
    
    /**
     * Get taxes that need action for a company
     */
    public List<Tax> getTaxesNeedingAction(Long companyId) {
        logger.info("Fetching taxes needing action for company: {}", companyId);
        
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        
        List<Tax> taxes = taxRepository.findTaxesNeedingAction(companyId);
        logger.info("Found {} taxes needing action for company: {}", taxes.size(), companyId);
        return taxes;
    }
    
    /**
     * Get federal tax information for a company
     */
    public Map<String, Object> getFederalTaxInfo(Long companyId) {
        logger.info("Fetching federal tax info for company: {}", companyId);
        
        List<Tax> federalTaxes = getTaxesByCompanyIdAndLevel(companyId, "FEDERAL");
        
        Map<String, Object> federalInfo = new HashMap<>();
        if (!federalTaxes.isEmpty()) {
            Tax federalTax = federalTaxes.get(0); // Assuming one federal tax record
            federalInfo.put("ein", federalTax.getTaxId());
            federalInfo.put("description", federalTax.getDescription());
            federalInfo.put("status", federalTax.getStatus());
        } else {
            // Return placeholder data if no federal taxes found
            federalInfo.put("ein", "[Federal EIN]");
            federalInfo.put("description", "FEDERAL UNEMPLOYMENT TAX - FUTA");
            federalInfo.put("status", "Subject");
        }
        
        return federalInfo;
    }
    
    /**
     * Get state tax information for a company
     */
    public List<Tax> getStateTaxes(Long companyId) {
        logger.info("Fetching state taxes for company: {}", companyId);
        return getTaxesByCompanyIdAndLevel(companyId, "STATE");
    }
    
    /**
     * Get local tax information for a company
     */
    public List<Tax> getLocalTaxes(Long companyId) {
        logger.info("Fetching local taxes for company: {}", companyId);
        return getTaxesByCompanyIdAndLevel(companyId, "LOCAL");
    }
    
    /**
     * Add a new tax for a company
     */
    public Tax addTax(Long companyId, Tax tax) {
        logger.info("Adding new tax for company: {}", companyId);
        
        if (companyId == null || tax == null) {
            throw new IllegalArgumentException("Company ID and tax cannot be null");
        }
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
        
        tax.setCompany(company);
        tax.setIsActive(true);
        
        Tax savedTax = taxRepository.save(tax);
        logger.info("Successfully added tax with ID: {} for company: {}", savedTax.getId(), companyId);
        return savedTax;
    }
    
    /**
     * Update an existing tax
     */
    public Tax updateTax(Long companyId, Long taxId, Tax updatedTax) {
        logger.info("Updating tax with ID: {} for company: {}", taxId, companyId);
        
        if (companyId == null || taxId == null || updatedTax == null) {
            throw new IllegalArgumentException("Company ID, tax ID, and updated tax cannot be null");
        }
        
        Tax existingTax = taxRepository.findById(taxId)
                .orElseThrow(() -> new IllegalArgumentException("Tax not found with ID: " + taxId));
        
        // Verify the tax belongs to the company
        if (!existingTax.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("Tax does not belong to the specified company");
        }
        
        // Update fields
        existingTax.setTaxType(updatedTax.getTaxType());
        existingTax.setTaxLevel(updatedTax.getTaxLevel());
        existingTax.setStateCode(updatedTax.getStateCode());
        existingTax.setTaxId(updatedTax.getTaxId());
        existingTax.setDescription(updatedTax.getDescription());
        existingTax.setDepositFrequency(updatedTax.getDepositFrequency());
        existingTax.setRate(updatedTax.getRate());
        existingTax.setEffectiveDate(updatedTax.getEffectiveDate());
        existingTax.setStatus(updatedTax.getStatus());
        existingTax.setNeedsAction(updatedTax.getNeedsAction());
        existingTax.setActionRequired(updatedTax.getActionRequired());
        
        Tax savedTax = taxRepository.save(existingTax);
        logger.info("Successfully updated tax with ID: {} for company: {}", savedTax.getId(), companyId);
        return savedTax;
    }
    
    /**
     * Delete a tax (soft delete)
     */
    public void deleteTax(Long companyId, Long taxId) {
        logger.info("Deleting tax with ID: {} for company: {}", taxId, companyId);
        
        if (companyId == null || taxId == null) {
            throw new IllegalArgumentException("Company ID and tax ID cannot be null");
        }
        
        Tax existingTax = taxRepository.findById(taxId)
                .orElseThrow(() -> new IllegalArgumentException("Tax not found with ID: " + taxId));
        
        // Verify the tax belongs to the company
        if (!existingTax.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("Tax does not belong to the specified company");
        }
        
        existingTax.setIsActive(false);
        taxRepository.save(existingTax);
        logger.info("Successfully deleted tax with ID: {} for company: {}", taxId, companyId);
    }
    
    /**
     * Initialize default federal tax for a company
     */
    public Tax initializeDefaultFederalTax(Long companyId) {
        logger.info("Initializing default federal tax for company: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
        
        Tax federalTax = new Tax(TaxType.FUTA, "FEDERAL", company);
        federalTax.setTaxId("[Federal EIN]");
        federalTax.setDescription("FEDERAL UNEMPLOYMENT TAX - FUTA");
        federalTax.setStatus("Subject");
        federalTax.setIsActive(true);
        
        Tax savedTax = taxRepository.save(federalTax);
        logger.info("Successfully initialized default federal tax for company: {}", companyId);
        return savedTax;
    }
} 
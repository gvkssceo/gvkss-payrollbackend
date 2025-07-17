package com.payroll.texas.repository;

import com.payroll.texas.model.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    
    /**
     * Find all active taxes for a company
     */
    @Query("SELECT t FROM Tax t WHERE t.company.id = :companyId AND t.isActive = true ORDER BY t.taxLevel, t.stateCode, t.taxType")
    List<Tax> findByCompanyIdAndActive(@Param("companyId") Long companyId);
    
    /**
     * Find taxes by company ID and tax level
     */
    @Query("SELECT t FROM Tax t WHERE t.company.id = :companyId AND t.taxLevel = :taxLevel AND t.isActive = true ORDER BY t.stateCode, t.taxType")
    List<Tax> findByCompanyIdAndTaxLevel(@Param("companyId") Long companyId, @Param("taxLevel") String taxLevel);
    
    /**
     * Find taxes that need action for a company
     */
    @Query("SELECT t FROM Tax t WHERE t.company.id = :companyId AND t.needsAction = true AND t.isActive = true ORDER BY t.taxLevel, t.stateCode")
    List<Tax> findTaxesNeedingAction(@Param("companyId") Long companyId);
    
    /**
     * Find taxes by company ID and state code
     */
    @Query("SELECT t FROM Tax t WHERE t.company.id = :companyId AND t.stateCode = :stateCode AND t.isActive = true ORDER BY t.taxType")
    List<Tax> findByCompanyIdAndStateCode(@Param("companyId") Long companyId, @Param("stateCode") String stateCode);
    
    /**
     * Check if company has any taxes
     */
    @Query("SELECT COUNT(t) > 0 FROM Tax t WHERE t.company.id = :companyId AND t.isActive = true")
    boolean existsByCompanyId(@Param("companyId") Long companyId);
} 
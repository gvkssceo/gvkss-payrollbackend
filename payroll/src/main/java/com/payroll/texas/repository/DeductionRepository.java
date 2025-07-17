package com.payroll.texas.repository;

import com.payroll.texas.model.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {
    
    /**
     * Find all active deductions for a specific company
     */
    @Query("SELECT d FROM Deduction d WHERE d.company.id = :companyId AND d.isActive = true ORDER BY d.type")
    List<Deduction> findByCompanyIdAndActive(@Param("companyId") Long companyId);
    
    /**
     * Find all deductions for a specific company (including inactive)
     */
    @Query("SELECT d FROM Deduction d WHERE d.company.id = :companyId ORDER BY d.type")
    List<Deduction> findByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Find a deduction by ID and company ID
     */
    @Query("SELECT d FROM Deduction d WHERE d.id = :deductionId AND d.company.id = :companyId")
    Optional<Deduction> findByIdAndCompanyId(@Param("deductionId") Long deductionId, @Param("companyId") Long companyId);
    
    /**
     * Check if a deduction type already exists for a company
     */
    @Query("SELECT COUNT(d) > 0 FROM Deduction d WHERE d.company.id = :companyId AND LOWER(d.type) = LOWER(:type) AND d.id != :excludeId")
    boolean existsByCompanyIdAndTypeIgnoreCase(@Param("companyId") Long companyId, @Param("type") String type, @Param("excludeId") Long excludeId);
    
    /**
     * Check if a deduction type already exists for a company (for new deductions)
     */
    @Query("SELECT COUNT(d) > 0 FROM Deduction d WHERE d.company.id = :companyId AND LOWER(d.type) = LOWER(:type)")
    boolean existsByCompanyIdAndTypeIgnoreCase(@Param("companyId") Long companyId, @Param("type") String type);
    
    /**
     * Find deductions by type (case-insensitive)
     */
    @Query("SELECT d FROM Deduction d WHERE d.company.id = :companyId AND LOWER(d.type) LIKE LOWER(CONCAT('%', :type, '%')) AND d.isActive = true")
    List<Deduction> findByCompanyIdAndTypeContainingIgnoreCase(@Param("companyId") Long companyId, @Param("type") String type);
    
    /**
     * Find required deductions for a company
     */
    @Query("SELECT d FROM Deduction d WHERE d.company.id = :companyId AND d.isRequired = true AND d.isActive = true ORDER BY d.type")
    List<Deduction> findRequiredDeductionsByCompanyId(@Param("companyId") Long companyId);
} 
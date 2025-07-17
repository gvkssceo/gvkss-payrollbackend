package com.payroll.texas.repository;

import com.payroll.texas.model.Earning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EarningRepository extends JpaRepository<Earning, Long> {
    
    /**
     * Find all active earnings for a specific company
     */
    @Query("SELECT e FROM Earning e WHERE e.company.id = :companyId AND e.isActive = true ORDER BY e.type")
    List<Earning> findByCompanyIdAndActive(@Param("companyId") Long companyId);
    
    /**
     * Find all earnings for a specific company (including inactive)
     */
    @Query("SELECT e FROM Earning e WHERE e.company.id = :companyId ORDER BY e.type")
    List<Earning> findByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Find an earning by ID and company ID
     */
    @Query("SELECT e FROM Earning e WHERE e.id = :earningId AND e.company.id = :companyId")
    Optional<Earning> findByIdAndCompanyId(@Param("earningId") Long earningId, @Param("companyId") Long companyId);
    
    /**
     * Check if an earning type already exists for a company
     */
    @Query("SELECT COUNT(e) > 0 FROM Earning e WHERE e.company.id = :companyId AND LOWER(e.type) = LOWER(:type) AND e.id != :excludeId")
    boolean existsByCompanyIdAndTypeIgnoreCase(@Param("companyId") Long companyId, @Param("type") String type, @Param("excludeId") Long excludeId);
    
    /**
     * Check if an earning type already exists for a company (for new earnings)
     */
    @Query("SELECT COUNT(e) > 0 FROM Earning e WHERE e.company.id = :companyId AND LOWER(e.type) = LOWER(:type)")
    boolean existsByCompanyIdAndTypeIgnoreCase(@Param("companyId") Long companyId, @Param("type") String type);
    
    /**
     * Find earnings by type (case-insensitive)
     */
    @Query("SELECT e FROM Earning e WHERE e.company.id = :companyId AND LOWER(e.type) LIKE LOWER(CONCAT('%', :type, '%')) AND e.isActive = true")
    List<Earning> findByCompanyIdAndTypeContainingIgnoreCase(@Param("companyId") Long companyId, @Param("type") String type);
} 
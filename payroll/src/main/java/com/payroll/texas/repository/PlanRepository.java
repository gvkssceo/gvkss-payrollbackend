package com.payroll.texas.repository;

import com.payroll.texas.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    Optional<Plan> findByName(String name);
    
    List<Plan> findByIsActiveTrue();
    
    List<Plan> findByIsActiveTrueOrderBySortOrderAsc();
    
    List<Plan> findByIsFeaturedTrue();
    
    List<Plan> findByIsActiveTrueAndIsFeaturedTrue();
    
    List<Plan> findByIsActiveTrueAndIsFeaturedTrueOrderBySortOrderAsc();
    
    List<Plan> findByIsActiveTrueAndMaxEmployeesGreaterThanEqualOrderBySortOrderAsc(Integer maxEmployees);
    
    List<Plan> findByIsActiveTrueAndMonthlyPriceBetweenOrderByMonthlyPriceAsc(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("SELECT p FROM Plan p WHERE p.isActive = true AND p.monthlyPrice BETWEEN :minPrice AND :maxPrice ORDER BY p.monthlyPrice ASC")
    List<Plan> findByIsActiveTrueAndMonthlyPriceBetweenOrderByMonthlyPriceAsc(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
    
    @Query("SELECT COUNT(p) FROM Plan p WHERE p.isActive = true")
    Long countActivePlans();
} 
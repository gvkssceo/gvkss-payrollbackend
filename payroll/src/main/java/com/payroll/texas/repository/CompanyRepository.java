package com.payroll.texas.repository;

import com.payroll.texas.model.Company;
import com.payroll.texas.model.CompanyStatus;
import com.payroll.texas.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    Optional<Company> findByEmail(String email);
    
    Optional<Company> findByEmailAndStatus(String email, CompanyStatus status);
    
    List<Company> findByStatus(CompanyStatus status);
    
    List<Company> findBySubscriptionStatus(SubscriptionStatus subscriptionStatus);
    
    @Query("SELECT c FROM Company c WHERE c.email = :email AND c.deletedAt IS NULL")
    Optional<Company> findByEmailAndNotDeleted(@Param("email") String email);
    
    @Query("SELECT c FROM Company c WHERE c.status = :status AND c.deletedAt IS NULL")
    List<Company> findByStatusAndNotDeleted(@Param("status") CompanyStatus status);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
} 
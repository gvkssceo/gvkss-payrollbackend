package com.payroll.texas.repository;

import com.payroll.texas.model.CompanySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, Long> {
    
    Optional<CompanySubscription> findByCompanyId(Long companyId);
    
    List<CompanySubscription> findByCompanyIdAndStatus(Long companyId, com.payroll.texas.model.SubscriptionStatus status);
    
    List<CompanySubscription> findByStatus(com.payroll.texas.model.SubscriptionStatus status);
    
    boolean existsByCompanyIdAndStatus(Long companyId, com.payroll.texas.model.SubscriptionStatus status);
} 
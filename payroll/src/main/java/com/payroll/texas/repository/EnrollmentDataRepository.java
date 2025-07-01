package com.payroll.texas.repository;

import com.payroll.texas.model.EnrollmentData;
import com.payroll.texas.model.EnrollmentStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentDataRepository extends JpaRepository<EnrollmentData, Long> {
    
    Optional<EnrollmentData> findByContactEmail(String contactEmail);
    
    Optional<EnrollmentData> findByContactEmailAndEnrollmentStep(String contactEmail, EnrollmentStep enrollmentStep);
    
    List<EnrollmentData> findByEnrollmentStep(EnrollmentStep enrollmentStep);
    
    List<EnrollmentData> findByCompanyId(Long companyId);
    
    List<EnrollmentData> findByUserId(Long userId);
    
    @Query("SELECT e FROM EnrollmentData e WHERE e.contactEmail = :email AND e.enrollmentStep != 'COMPLETED'")
    Optional<EnrollmentData> findActiveEnrollmentByEmail(@Param("email") String email);
    
    @Query("SELECT e FROM EnrollmentData e WHERE e.company.id = :companyId AND e.enrollmentStep = 'COMPLETED'")
    List<EnrollmentData> findCompletedEnrollmentsByCompany(@Param("companyId") Long companyId);
    
    boolean existsByContactEmail(String contactEmail);
    
    @Query("SELECT COUNT(e) FROM EnrollmentData e WHERE e.enrollmentStep = :step")
    Long countByEnrollmentStep(@Param("step") EnrollmentStep step);
} 
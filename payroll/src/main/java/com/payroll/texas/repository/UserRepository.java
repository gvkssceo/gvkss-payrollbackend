package com.payroll.texas.repository;

import com.payroll.texas.model.User;
import com.payroll.texas.model.UserStatus;
import com.payroll.texas.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    
    List<User> findByCompanyId(Long companyId);
    
    List<User> findByCompanyIdAndStatus(Long companyId, UserStatus status);
    
    List<User> findByUserType(UserType userType);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmailAndNotDeleted(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.deletedAt IS NULL")
    List<User> findByCompanyIdAndNotDeleted(@Param("companyId") Long companyId);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
} 
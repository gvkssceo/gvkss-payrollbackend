package com.payroll.texas.repository;

import com.payroll.texas.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Basic CRUD operations are inherited from JpaRepository
    // No custom company-scoped methods needed for this implementation
} 
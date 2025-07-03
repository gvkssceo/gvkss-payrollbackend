package com.payroll.texas.service;

import com.payroll.texas.dto.enrollment.EnrollmentRequest;
import com.payroll.texas.dto.enrollment.EnrollmentResponse;
import com.payroll.texas.model.EnrollmentData;
import com.payroll.texas.model.EnrollmentStep;
import com.payroll.texas.model.Plan;
import com.payroll.texas.repository.EnrollmentDataRepository;
import com.payroll.texas.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    
    @Autowired
    private EnrollmentDataRepository enrollmentDataRepository;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private CustomFieldsService customFieldsService;
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    @Transactional
    public EnrollmentResponse beginEnrollment(EnrollmentRequest request) {
        // Check if enrollment already exists for this email
        Optional<EnrollmentData> existingEnrollment = enrollmentDataRepository.findByContactEmail(request.getContactEmail());
        
        EnrollmentData enrollmentData;
        if (existingEnrollment.isPresent()) {
            // Update existing enrollment
            enrollmentData = existingEnrollment.get();
            updateEnrollmentData(enrollmentData, request);
        } else {
            // Create new enrollment
            enrollmentData = createEnrollmentData(request);
        }
        
        enrollmentData = enrollmentDataRepository.save(enrollmentData);
        
        // Create response
        EnrollmentResponse.EnrollmentData responseData = new EnrollmentResponse.EnrollmentData(
            enrollmentData.getCompanyName(),
            enrollmentData.getContactName(),
            enrollmentData.getContactEmail(),
            enrollmentData.getContactPhone()
        );
        responseData.setSelectedPlan(enrollmentData.getSelectedPlan() != null ? enrollmentData.getSelectedPlan().getName() : null);
        responseData.setPlanSelectedBeforeLogin(enrollmentData.getPlanSelectedBeforeLogin());
        responseData.setCustomFields(enrollmentData.getCustomFields());
        
        return new EnrollmentResponse(
            "Enrollment started successfully",
            enrollmentData.getId(),
            enrollmentData.getEnrollmentStep(),
            responseData
        );
    }
    
    @Transactional
    public EnrollmentResponse updateEnrollmentStep(Long enrollmentId, EnrollmentStep step) {
        Optional<EnrollmentData> enrollmentOpt = enrollmentDataRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found");
        }
        
        EnrollmentData enrollment = enrollmentOpt.get();
        enrollment.setEnrollmentStep(step);
        
        if (step == EnrollmentStep.COMPLETED) {
            enrollment.setEnrollmentCompletedAt(LocalDateTime.now());
        }
        
        enrollment = enrollmentDataRepository.save(enrollment);
        
        // Create response
        EnrollmentResponse.EnrollmentData responseData = new EnrollmentResponse.EnrollmentData(
            enrollment.getCompanyName(),
            enrollment.getContactName(),
            enrollment.getContactEmail(),
            enrollment.getContactPhone()
        );
        responseData.setSelectedPlan(enrollment.getSelectedPlan() != null ? enrollment.getSelectedPlan().getName() : null);
        responseData.setPlanSelectedBeforeLogin(enrollment.getPlanSelectedBeforeLogin());
        responseData.setCustomFields(enrollment.getCustomFields());
        
        return new EnrollmentResponse(
            "Enrollment step updated successfully",
            enrollment.getId(),
            enrollment.getEnrollmentStep(),
            responseData
        );
    }
    
    @Transactional
    public EnrollmentResponse selectPlan(Long enrollmentId, String planName) {
        Optional<EnrollmentData> enrollmentOpt = enrollmentDataRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found");
        }
        
        Optional<Plan> planOpt = planRepository.findByName(planName);
        if (planOpt.isEmpty()) {
            throw new RuntimeException("Plan not found");
        }
        
        EnrollmentData enrollment = enrollmentOpt.get();
        enrollment.setSelectedPlan(planOpt.get());
        enrollment.setPlanSelectedBeforeLogin(true);
        enrollment.setEnrollmentStep(EnrollmentStep.PLAN_SELECTION);
        
        enrollment = enrollmentDataRepository.save(enrollment);
        
        // Update subscription status based on plan selection
        try {
            subscriptionService.updateCompanySubscriptionStatusByEmail(
                enrollment.getContactEmail(), 
                planName
            );
        } catch (Exception e) {
            // Log the error but don't fail the enrollment
            System.err.println("Failed to update subscription status: " + e.getMessage());
        }
        
        // Create response
        EnrollmentResponse.EnrollmentData responseData = new EnrollmentResponse.EnrollmentData(
            enrollment.getCompanyName(),
            enrollment.getContactName(),
            enrollment.getContactEmail(),
            enrollment.getContactPhone()
        );
        responseData.setSelectedPlan(enrollment.getSelectedPlan().getName());
        responseData.setPlanSelectedBeforeLogin(enrollment.getPlanSelectedBeforeLogin());
        responseData.setCustomFields(enrollment.getCustomFields());
        
        return new EnrollmentResponse(
            "Plan selected successfully",
            enrollment.getId(),
            enrollment.getEnrollmentStep(),
            responseData
        );
    }
    
    public EnrollmentResponse getEnrollmentStatus(String email) {
        Optional<EnrollmentData> enrollmentOpt = enrollmentDataRepository.findByContactEmail(email);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found");
        }
        
        EnrollmentData enrollment = enrollmentOpt.get();
        
        // Create response
        EnrollmentResponse.EnrollmentData responseData = new EnrollmentResponse.EnrollmentData(
            enrollment.getCompanyName(),
            enrollment.getContactName(),
            enrollment.getContactEmail(),
            enrollment.getContactPhone()
        );
        responseData.setSelectedPlan(enrollment.getSelectedPlan() != null ? enrollment.getSelectedPlan().getName() : null);
        responseData.setPlanSelectedBeforeLogin(enrollment.getPlanSelectedBeforeLogin());
        responseData.setCustomFields(enrollment.getCustomFields());
        
        return new EnrollmentResponse(
            "Enrollment status retrieved",
            enrollment.getId(),
            enrollment.getEnrollmentStep(),
            responseData
        );
    }
    
    public List<EnrollmentData> getAllEnrollments() {
        return enrollmentDataRepository.findAll();
    }
    
    private EnrollmentData createEnrollmentData(EnrollmentRequest request) {
        EnrollmentData enrollment = new EnrollmentData(
            request.getCompanyName(),
            request.getContactName(),
            request.getContactEmail(),
            request.getContactPhone()
        );
        
        enrollment.setEnrollmentStep(EnrollmentStep.INITIAL);
        enrollment.setPlanSelectedBeforeLogin(request.getPlanSelectedBeforeLogin());
        
        // Set custom fields
        if (request.getCustomFields() != null) {
            enrollment.setCustomFields(request.getCustomFields());
        }
        
        // Handle plan selection if provided
        if (request.getSelectedPlan() != null) {
            Optional<Plan> planOpt = planRepository.findByName(request.getSelectedPlan());
            if (planOpt.isPresent()) {
                enrollment.setSelectedPlan(planOpt.get());
                enrollment.setEnrollmentStep(EnrollmentStep.PLAN_SELECTION);
                
                // Update subscription status based on plan selection
                try {
                    subscriptionService.updateCompanySubscriptionStatusByEmail(
                        request.getContactEmail(), 
                        request.getSelectedPlan()
                    );
                } catch (Exception e) {
                    // Log the error but don't fail the enrollment
                    System.err.println("Failed to update subscription status: " + e.getMessage());
                }
            }
        }
        
        return enrollment;
    }
    
    private void updateEnrollmentData(EnrollmentData enrollment, EnrollmentRequest request) {
        enrollment.setCompanyName(request.getCompanyName());
        enrollment.setContactName(request.getContactName());
        enrollment.setContactEmail(request.getContactEmail());
        enrollment.setContactPhone(request.getContactPhone());
        
        // Update custom fields
        if (request.getCustomFields() != null) {
            enrollment.setCustomFields(request.getCustomFields());
        }
        
        // Handle plan selection if provided
        if (request.getSelectedPlan() != null) {
            Optional<Plan> planOpt = planRepository.findByName(request.getSelectedPlan());
            if (planOpt.isPresent()) {
                enrollment.setSelectedPlan(planOpt.get());
                enrollment.setPlanSelectedBeforeLogin(request.getPlanSelectedBeforeLogin());
                enrollment.setEnrollmentStep(EnrollmentStep.PLAN_SELECTION);
            }
        }
    }
} 
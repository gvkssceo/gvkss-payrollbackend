package com.payroll.texas.controller;

import com.payroll.texas.dto.enrollment.EnrollmentRequest;
import com.payroll.texas.dto.enrollment.EnrollmentResponse;
import com.payroll.texas.model.EnrollmentData;
import com.payroll.texas.model.EnrollmentStep;
import com.payroll.texas.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enrollment")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/begin")
    public ResponseEntity<EnrollmentResponse> beginEnrollment(@Valid @RequestBody EnrollmentRequest enrollmentRequest) {
        try {
            EnrollmentResponse response = enrollmentService.beginEnrollment(enrollmentRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Log the full error for debugging
            System.err.println("Enrollment error: " + e.getMessage());
            e.printStackTrace();
            
            EnrollmentResponse errorResponse = new EnrollmentResponse();
            errorResponse.setMessage("Enrollment failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{enrollmentId}/step")
    public ResponseEntity<EnrollmentResponse> updateEnrollmentStep(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, String> request) {
        try {
            String stepName = request.get("step");
            EnrollmentStep step = EnrollmentStep.valueOf(stepName.toUpperCase());
            
            EnrollmentResponse response = enrollmentService.updateEnrollmentStep(enrollmentId, step);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            EnrollmentResponse errorResponse = new EnrollmentResponse();
            errorResponse.setMessage("Invalid enrollment step: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            EnrollmentResponse errorResponse = new EnrollmentResponse();
            errorResponse.setMessage("Failed to update enrollment step: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{enrollmentId}/select-plan")
    public ResponseEntity<EnrollmentResponse> selectPlan(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, String> request) {
        try {
            String planName = request.get("planName");
            if (planName == null || planName.trim().isEmpty()) {
                throw new RuntimeException("Plan name is required");
            }
            
            EnrollmentResponse response = enrollmentService.selectPlan(enrollmentId, planName);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            EnrollmentResponse errorResponse = new EnrollmentResponse();
            errorResponse.setMessage("Failed to select plan: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/status/{email}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentStatus(@PathVariable String email) {
        try {
            EnrollmentResponse response = enrollmentService.getEnrollmentStatus(email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            EnrollmentResponse errorResponse = new EnrollmentResponse();
            errorResponse.setMessage("Failed to get enrollment status: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/steps")
    public ResponseEntity<Map<String, Object>> getEnrollmentSteps() {
        Map<String, Object> response = new HashMap<>();
        response.put("steps", EnrollmentStep.values());
        response.put("message", "Available enrollment steps retrieved");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-company")
    public ResponseEntity<Map<String, Object>> validateCompanyInfo(@RequestBody Map<String, String> request) {
        String companyName = request.get("companyName");
        String contactName = request.get("contactName");
        String contactEmail = request.get("contactEmail");
        String contactPhone = request.get("contactPhone");
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate required fields
        if (companyName == null || companyName.trim().isEmpty()) {
            response.put("valid", false);
            response.put("message", "Company name is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (contactName == null || contactName.trim().isEmpty()) {
            response.put("valid", false);
            response.put("message", "Contact name is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (contactEmail == null || contactEmail.trim().isEmpty()) {
            response.put("valid", false);
            response.put("message", "Contact email is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!contactEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            response.put("valid", false);
            response.put("message", "Invalid email format");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (contactPhone == null || contactPhone.trim().isEmpty()) {
            response.put("valid", false);
            response.put("message", "Contact phone is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        response.put("valid", true);
        response.put("message", "Company information is valid");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/diagnostic")
    public ResponseEntity<Map<String, Object>> diagnostic() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Check if there are any existing enrollments with invalid data
            List<EnrollmentData> allEnrollments = enrollmentService.getAllEnrollments();
            response.put("totalEnrollments", allEnrollments.size());
            
            // Check for any enrollments with null or invalid enum values
            List<Map<String, Object>> problematicEnrollments = new ArrayList<>();
            for (EnrollmentData enrollment : allEnrollments) {
                if (enrollment.getEnrollmentStep() == null) {
                    Map<String, Object> problem = new HashMap<>();
                    problem.put("id", enrollment.getId());
                    problem.put("email", enrollment.getContactEmail());
                    problem.put("issue", "null enrollment_step");
                    problematicEnrollments.add(problem);
                }
            }
            
            response.put("problematicEnrollments", problematicEnrollments);
            response.put("message", "Diagnostic completed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("message", "Diagnostic failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 
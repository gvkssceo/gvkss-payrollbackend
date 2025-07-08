package com.payroll.texas.controller;

import com.payroll.texas.model.Company;
import com.payroll.texas.model.CompanySubscription;
import com.payroll.texas.repository.CompanyRepository;
import com.payroll.texas.repository.CompanySubscriptionRepository;
import com.payroll.texas.repository.PlanRepository;
import com.payroll.texas.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanySubscriptionRepository subscriptionRepository;

    @GetMapping
    public String test() {
        System.out.println("TestController.test() called!");
        return "Test endpoint working!";
    }
    
    @GetMapping("/simple")
    public Map<String, Object> simpleTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Simple test endpoint working!");
        response.put("status", "success");
        response.put("timestamp", java.time.LocalDateTime.now());
        return response;
    }
    
    @GetMapping("/plans")
    public Map<String, Object> testPlans() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<com.payroll.texas.model.Plan> plans = planRepository.findAll();
            response.put("message", "Plans retrieved successfully");
            response.put("count", plans.size());
            response.put("plans", plans.stream().map(p -> Map.of(
                "id", p.getId(),
                "name", p.getName(),
                "displayName", p.getDisplayName(),
                "isActive", p.getIsActive()
            )).toList());
            response.put("status", "success");
        } catch (Exception e) {
            response.put("message", "Error retrieving plans: " + e.getMessage());
            response.put("status", "error");
            response.put("error", e.toString());
        }
        return response;
    }
    
    @PostMapping("/subscription")
    public Map<String, Object> testSubscription(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String companyEmail = request.get("companyEmail");
            String planName = request.get("planName");
            
            if (companyEmail == null || planName == null) {
                response.put("message", "Company email and plan name are required");
                response.put("status", "error");
                return response;
            }
            
            subscriptionService.updateCompanySubscriptionStatusByEmail(companyEmail, planName);
            
            response.put("message", "Subscription status updated successfully");
            response.put("companyEmail", companyEmail);
            response.put("planName", planName);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("message", "Error updating subscription: " + e.getMessage());
            response.put("status", "error");
            response.put("error", e.toString());
        }
        return response;
    }
    
    @GetMapping("/enrollment-test")
    public Map<String, Object> testEnrollment() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Test basic enrollment without plan
            Map<String, Object> testData = new HashMap<>();
            testData.put("companyName", "Test Company");
            testData.put("contactName", "Test Contact");
            testData.put("contactEmail", "test@example.com");
            testData.put("contactPhone", "555-123-4567");
            testData.put("selectedPlan", null); // No plan
            testData.put("planSelectedBeforeLogin", false);
            testData.put("customFields", "{}");
            
            response.put("message", "Enrollment test data created");
            response.put("testData", testData);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("message", "Error in enrollment test: " + e.getMessage());
            response.put("status", "error");
            response.put("error", e.toString());
        }
        return response;
    }

    @PostMapping("/test-plan-selection")
    public ResponseEntity<Map<String, Object>> testPlanSelection(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = request.get("email");
            String planName = request.get("planName");
            
            if (email == null || planName == null) {
                response.put("error", "Email and planName are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("TestController: Testing plan selection for email: " + email + ", plan: " + planName);
            
            // Test plan selection
            subscriptionService.updateCompanySubscriptionStatusByEmail(email, planName);
            
            // Get debug info
            Optional<Company> companyOpt = companyRepository.findByEmail(email);
            if (companyOpt.isPresent()) {
                Company company = companyOpt.get();
                response.put("companyId", company.getId());
                response.put("companyName", company.getName());
                response.put("subscriptionStatus", company.getSubscriptionStatus());
                response.put("customFields", company.getCustomFields());
                
                Optional<CompanySubscription> subscriptionOpt = subscriptionRepository.findByCompanyId(company.getId());
                if (subscriptionOpt.isPresent()) {
                    CompanySubscription subscription = subscriptionOpt.get();
                    response.put("subscriptionId", subscription.getId());
                    response.put("subscriptionStatus", subscription.getStatus());
                    response.put("planName", subscription.getPlan() != null ? subscription.getPlan().getName() : "null");
                }
            }
            
            response.put("success", true);
            response.put("message", "Plan selection test completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 
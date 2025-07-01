package com.payroll.texas.controller;

import com.payroll.texas.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private PlanRepository planRepository;

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
} 
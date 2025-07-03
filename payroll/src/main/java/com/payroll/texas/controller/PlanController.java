package com.payroll.texas.controller;

import com.payroll.texas.dto.plan.PlanResponse;
import com.payroll.texas.service.PlanService;
import com.payroll.texas.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plans")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class PlanController {

    @Autowired
    private PlanService planService;
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    @GetMapping("/test-subscription")
    public ResponseEntity<Map<String, Object>> testSubscription() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("message", "SubscriptionService is properly injected");
            response.put("subscriptionService", subscriptionService != null ? "Available" : "Not available");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error testing SubscriptionService");
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugPlans() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PlanResponse> allPlans = planService.getAllActivePlans();
            response.put("availablePlans", allPlans);
            response.put("planCount", allPlans.size());
            response.put("planNames", allPlans.stream().map(PlanResponse::getName).collect(java.util.stream.Collectors.toList()));
            
            // Also check raw database state
            List<com.payroll.texas.model.Plan> rawPlans = planService.getAllPlansRaw();
            response.put("rawPlans", rawPlans.stream().map(p -> Map.of(
                "id", p.getId(),
                "name", p.getName(),
                "displayName", p.getDisplayName(),
                "isActive", p.getIsActive()
            )).collect(java.util.stream.Collectors.toList()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPlans() {
        try {
            List<PlanResponse> plans = planService.getAllActivePlans();
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedPlans() {
        try {
            List<PlanResponse> plans = planService.getFeaturedPlans();
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlanById(@PathVariable Long id) {
        try {
            PlanResponse plan = planService.getPlanById(id);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getPlanByName(@PathVariable String name) {
        try {
            PlanResponse plan = planService.getPlanByName(name);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/select")
    public ResponseEntity<?> selectPlan(@RequestBody Map<String, String> request) {
        try {
            String companyEmail = request.get("companyEmail");
            String planName = request.get("planName");
            
            if (companyEmail == null || planName == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Company email and plan name are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Update subscription status based on plan selection
            subscriptionService.updateCompanySubscriptionStatusByEmail(companyEmail, planName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Plan selected and subscription status updated successfully");
            response.put("planName", planName);
            response.put("companyEmail", companyEmail);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/user-subscription/{userEmail}")
    public ResponseEntity<?> getUserSubscription(@PathVariable String userEmail) {
        try {
            boolean hasSubscription = subscriptionService.hasActiveSubscription(userEmail);
            boolean hasSelectedPlan = subscriptionService.hasSelectedPlan(userEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasSubscription", hasSubscription);
            response.put("hasSelectedPlan", hasSelectedPlan);
            response.put("userEmail", userEmail);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPlans(
            @RequestParam(required = false) Integer maxEmployees,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        try {
            List<PlanResponse> plans;
            
            if (maxEmployees != null) {
                plans = planService.getPlansByMaxEmployees(maxEmployees);
            } else if (minPrice != null && maxPrice != null) {
                plans = planService.getPlansByPriceRange(minPrice, maxPrice);
            } else {
                plans = planService.getAllActivePlans();
            }
            
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/compare")
    public ResponseEntity<?> comparePlans(@RequestParam List<String> planNames) {
        try {
            Map<String, Object> comparison = new HashMap<>();
            
            for (String planName : planNames) {
                try {
                    PlanResponse plan = planService.getPlanByName(planName);
                    comparison.put(planName, plan);
                } catch (RuntimeException e) {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("error", "Plan not found");
                    comparison.put(planName, errorMap);
                }
            }
            
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(
            @RequestParam(required = false) Integer employeeCount,
            @RequestParam(required = false) Double budget) {
        try {
            List<PlanResponse> recommendations;
            
            if (employeeCount != null) {
                recommendations = planService.getPlansByMaxEmployees(employeeCount);
            } else if (budget != null) {
                recommendations = planService.getPlansByPriceRange(0.0, budget);
            } else {
                recommendations = planService.getFeaturedPlans();
            }
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializePlans() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Check if plans already exist
            List<com.payroll.texas.model.Plan> existingPlans = planService.getAllPlansRaw();
            if (!existingPlans.isEmpty()) {
                response.put("message", "Plans already exist in database");
                response.put("existingPlans", existingPlans.stream().map(com.payroll.texas.model.Plan::getName).collect(java.util.stream.Collectors.toList()));
                return ResponseEntity.ok(response);
            }
            
            // Create the plans
            planService.initializeDefaultPlans();
            
            List<com.payroll.texas.model.Plan> newPlans = planService.getAllPlansRaw();
            response.put("message", "Plans initialized successfully");
            response.put("createdPlans", newPlans.stream().map(com.payroll.texas.model.Plan::getName).collect(java.util.stream.Collectors.toList()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 
package com.payroll.texas.controller;

import com.payroll.texas.dto.plan.PlanResponse;
import com.payroll.texas.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plans")
// @CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        try {
            List<PlanResponse> plans = planService.getAllActivePlans();
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<List<PlanResponse>> getFeaturedPlans() {
        try {
            List<PlanResponse> plans = planService.getFeaturedPlans();
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long id) {
        try {
            PlanResponse plan = planService.getPlanById(id);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PlanResponse> getPlanByName(@PathVariable String name) {
        try {
            PlanResponse plan = planService.getPlanByName(name);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlanResponse>> searchPlans(
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
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> comparePlans(@RequestParam List<String> planNames) {
        try {
            Map<String, Object> comparison = new HashMap<>();
            
            for (String planName : planNames) {
                try {
                    PlanResponse plan = planService.getPlanByName(planName);
                    comparison.put(planName, plan);
                } catch (RuntimeException e) {
                    comparison.put(planName, Map.of("error", "Plan not found"));
                }
            }
            
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<PlanResponse>> getRecommendations(
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
            return ResponseEntity.internalServerError().build();
        }
    }
} 
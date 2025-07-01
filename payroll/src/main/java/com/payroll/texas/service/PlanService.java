package com.payroll.texas.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.texas.dto.plan.PlanResponse;
import com.payroll.texas.model.Plan;
import com.payroll.texas.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanService {
    
    @Autowired
    private PlanRepository planRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public List<PlanResponse> getAllActivePlans() {
        List<Plan> plans = planRepository.findByIsActiveTrueOrderBySortOrderAsc();
        return plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanResponse> getFeaturedPlans() {
        List<Plan> plans = planRepository.findByIsActiveTrueAndIsFeaturedTrueOrderBySortOrderAsc();
        return plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public PlanResponse getPlanById(Long id) {
        Optional<Plan> planOpt = planRepository.findById(id);
        if (planOpt.isEmpty()) {
            throw new RuntimeException("Plan not found");
        }
        return convertToResponse(planOpt.get());
    }
    
    public PlanResponse getPlanByName(String name) {
        Optional<Plan> planOpt = planRepository.findByName(name);
        if (planOpt.isEmpty()) {
            throw new RuntimeException("Plan not found");
        }
        return convertToResponse(planOpt.get());
    }
    
    public List<PlanResponse> getPlansByMaxEmployees(Integer maxEmployees) {
        List<Plan> plans = planRepository.findByIsActiveTrueAndMaxEmployeesGreaterThanEqualOrderBySortOrderAsc(maxEmployees);
        return plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanResponse> getPlansByPriceRange(Double minPrice, Double maxPrice) {
        List<Plan> plans = planRepository.findByIsActiveTrueAndMonthlyPriceBetweenOrderByMonthlyPriceAsc(minPrice, maxPrice);
        return plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private PlanResponse convertToResponse(Plan plan) {
        List<String> features = parseFeatures(plan.getFeatures());
        
        return new PlanResponse(
            plan.getId(),
            plan.getName(),
            plan.getDisplayName(),
            plan.getDescription(),
            plan.getMonthlyPrice(),
            plan.getYearlyPrice(),
            plan.getMaxEmployees(),
            features,
            plan.getIsActive(),
            plan.getIsFeatured(),
            plan.getSortOrder()
        );
    }
    
    private List<String> parseFeatures(String featuresJson) {
        try {
            if (featuresJson == null || featuresJson.isEmpty()) {
                return List.of();
            }
            return objectMapper.readValue(featuresJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // Return empty list if parsing fails
            return List.of();
        }
    }
} 
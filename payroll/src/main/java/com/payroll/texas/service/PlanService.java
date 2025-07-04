package com.payroll.texas.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.texas.dto.plan.PlanResponse;
import com.payroll.texas.model.Plan;
import com.payroll.texas.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public List<Plan> getAllPlansRaw() {
        return planRepository.findAll();
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
    
    @Transactional
    public void initializeDefaultPlans() {
        // Create Basic Plan
        Plan basicPlan = new Plan();
        basicPlan.setName("Basic");
        basicPlan.setDisplayName("Basic Plan");
        basicPlan.setDescription("Basic payroll features for small businesses");
        basicPlan.setMonthlyPrice(new java.math.BigDecimal("39.00"));
        basicPlan.setYearlyPrice(new java.math.BigDecimal("390.00"));
        basicPlan.setMaxEmployees(5);
        basicPlan.setFeatures("[\"Full payroll processing\",\"Unlimited direct deposits\",\"Federal & state tax filings\",\"Basic compliance reporting\",\"Email support (72hr response)\"]");
        basicPlan.setIsActive(true);
        basicPlan.setIsFeatured(false);
        basicPlan.setSortOrder(1);
        planRepository.save(basicPlan);
        
        // Create Standard Plan
        Plan standardPlan = new Plan();
        standardPlan.setName("Standard");
        standardPlan.setDisplayName("Standard Plan");
        standardPlan.setDescription("Advanced payroll features for growing businesses");
        standardPlan.setMonthlyPrice(new java.math.BigDecimal("99.00"));
        standardPlan.setYearlyPrice(new java.math.BigDecimal("990.00"));
        standardPlan.setMaxEmployees(50);
        standardPlan.setFeatures("[\"Full payroll processing\",\"Unlimited direct deposits\",\"Federal & state tax filings\",\"Basic compliance reporting\",\"Email support (72hr response)\",\"Benefits administration\",\"Employee self-service portal\",\"Custom reporting tools\",\"Priority chat support (24hr)\",\"W-2/1099 preparation\"]");
        standardPlan.setIsActive(true);
        standardPlan.setIsFeatured(true);
        standardPlan.setSortOrder(2);
        planRepository.save(standardPlan);
        
        // Create Premium Plan
        Plan premiumPlan = new Plan();
        premiumPlan.setName("Premium");
        premiumPlan.setDisplayName("Premium Plan");
        premiumPlan.setDescription("Complete payroll solution for large organizations");
        premiumPlan.setMonthlyPrice(new java.math.BigDecimal("199.00"));
        premiumPlan.setYearlyPrice(new java.math.BigDecimal("1990.00"));
        premiumPlan.setMaxEmployees(999999);
        premiumPlan.setFeatures("[\"Full payroll processing\",\"Unlimited direct deposits\",\"Federal & state tax filings\",\"Basic compliance reporting\",\"Email support (72hr response)\",\"Benefits administration\",\"Employee self-service portal\",\"Custom reporting tools\",\"Priority chat support (24hr)\",\"W-2/1099 preparation\",\"Dedicated payroll specialist\",\"HR compliance auditing\",\"Advanced analytics dashboard\",\"API & accounting integrations\",\"24/7 phone support\"]");
        premiumPlan.setIsActive(true);
        premiumPlan.setIsFeatured(false);
        premiumPlan.setSortOrder(3);
        planRepository.save(premiumPlan);
    }
} 
package com.payroll.texas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test-plans")
public class TestPlanController {

    @GetMapping
    public Map<String, Object> testPlans() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test plans endpoint working!");
        response.put("status", "success");
        response.put("timestamp", java.time.LocalDateTime.now());
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "TestPlanController");
        return response;
    }
} 
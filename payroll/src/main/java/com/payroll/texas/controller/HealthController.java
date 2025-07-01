package com.payroll.texas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        System.out.println("HealthController.healthCheck() called!");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Texas Payroll Backend");
        response.put("version", "1.0.0");
        response.put("environment", "development");
        return response;
    }

    @GetMapping("/health/ping")
    public String ping() {
        System.out.println("HealthController.ping() called!");
        return "pong";
    }
} 
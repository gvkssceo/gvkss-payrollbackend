package com.payroll.texas.controller;

import com.payroll.texas.model.Tax;
import com.payroll.texas.service.AuthService;
import com.payroll.texas.service.TaxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/taxes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5174"})
public class TaxController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaxController.class);
    
    @Autowired
    private TaxService taxService;
    
    @Autowired
    private AuthService authService;

    // Constructor to verify controller is being instantiated
    public TaxController() {
        logger.info("TaxController is being instantiated!");
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.info("Tax health check endpoint called");
        return ResponseEntity.ok("Tax Controller is working!");
    }

    // Get all taxes for a company
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getCompanyTaxes(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get taxes for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            List<Tax> taxes = taxService.getTaxesByCompanyId(companyId);
            logger.info("Successfully retrieved {} taxes for company: {}", taxes.size(), companyId);
            return ResponseEntity.ok(taxes);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for company taxes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving company taxes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get federal tax information for a company
    @GetMapping("/company/{companyId}/federal")
    public ResponseEntity<?> getFederalTaxInfo(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get federal tax info for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Map<String, Object> federalInfo = taxService.getFederalTaxInfo(companyId);
            logger.info("Successfully retrieved federal tax info for company: {}", companyId);
            return ResponseEntity.ok(federalInfo);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for federal tax info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving federal tax info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get state taxes for a company
    @GetMapping("/company/{companyId}/state")
    public ResponseEntity<?> getStateTaxes(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get state taxes for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            List<Tax> stateTaxes = taxService.getStateTaxes(companyId);
            logger.info("Successfully retrieved {} state taxes for company: {}", stateTaxes.size(), companyId);
            return ResponseEntity.ok(stateTaxes);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for state taxes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving state taxes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get local taxes for a company
    @GetMapping("/company/{companyId}/local")
    public ResponseEntity<?> getLocalTaxes(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get local taxes for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            List<Tax> localTaxes = taxService.getLocalTaxes(companyId);
            logger.info("Successfully retrieved {} local taxes for company: {}", localTaxes.size(), companyId);
            return ResponseEntity.ok(localTaxes);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for local taxes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving local taxes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get taxes needing action for a company
    @GetMapping("/company/{companyId}/needing-action")
    public ResponseEntity<?> getTaxesNeedingAction(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to get taxes needing action for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            List<Tax> taxesNeedingAction = taxService.getTaxesNeedingAction(companyId);
            logger.info("Successfully retrieved {} taxes needing action for company: {}", taxesNeedingAction.size(), companyId);
            return ResponseEntity.ok(taxesNeedingAction);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for taxes needing action: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving taxes needing action: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Add new tax for a company
    @PostMapping("/company/{companyId}")
    public ResponseEntity<?> addTax(
            @PathVariable Long companyId,
            @RequestBody Tax tax,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to add tax for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Tax savedTax = taxService.addTax(companyId, tax);
            logger.info("Successfully added tax with ID: {} for company: {}", savedTax.getId(), companyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTax);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request to add tax: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding tax: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update existing tax
    @PutMapping("/company/{companyId}/{taxId}")
    public ResponseEntity<?> updateTax(
            @PathVariable Long companyId,
            @PathVariable Long taxId,
            @RequestBody Tax updatedTax,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to update tax with ID: {} for company: {}", taxId, companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Tax savedTax = taxService.updateTax(companyId, taxId, updatedTax);
            logger.info("Successfully updated tax with ID: {} for company: {}", savedTax.getId(), companyId);
            return ResponseEntity.ok(savedTax);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request to update tax: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating tax: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Delete tax (soft delete)
    @DeleteMapping("/company/{companyId}/{taxId}")
    public ResponseEntity<?> deleteTax(
            @PathVariable Long companyId,
            @PathVariable Long taxId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to delete tax with ID: {} for company: {}", taxId, companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            taxService.deleteTax(companyId, taxId);
            logger.info("Successfully deleted tax with ID: {} for company: {}", taxId, companyId);
            return ResponseEntity.ok(Map.of("message", "Tax deleted successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request to delete tax: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting tax: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Initialize default federal tax for a company
    @PostMapping("/company/{companyId}/initialize-federal")
    public ResponseEntity<?> initializeFederalTax(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader
    ) {
        logger.info("Received request to initialize federal tax for company: {}", companyId);
        try {
            // Extract user info from JWT token
            String token = authHeader.substring(7); // Remove "Bearer "
            Map<String, Object> validationResult = authService.validateTokenAndGetUser(token);

            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Tax federalTax = taxService.initializeDefaultFederalTax(companyId);
            logger.info("Successfully initialized federal tax for company: {}", companyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(federalTax);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request to initialize federal tax: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error initializing federal tax: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
} 
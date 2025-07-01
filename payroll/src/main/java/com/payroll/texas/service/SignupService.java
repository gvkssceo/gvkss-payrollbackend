package com.payroll.texas.service;

import com.payroll.texas.dto.auth.SignupRequest;
import com.payroll.texas.dto.auth.SignupResponse;
import com.payroll.texas.model.*;
import com.payroll.texas.repository.CompanyRepository;
import com.payroll.texas.repository.PlanRepository;
import com.payroll.texas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SignupService {
    
    private static final Logger logger = LoggerFactory.getLogger(SignupService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Autowired
    private CustomFieldsService customFieldsService;
    
    @Autowired
    private PasswordService passwordService;
    
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        logger.info("Signup attempt for email: {}", request.getEmail());
        
        // Validate password strength
        if (!passwordService.isPasswordStrong(request.getPassword())) {
            logger.warn("Signup failed - weak password for email: {}", request.getEmail());
            throw new RuntimeException("Password does not meet security requirements. Password must be at least 8 characters long and contain uppercase, lowercase, digit, and special character.");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Signup failed - user already exists for email: {}", request.getEmail());
            throw new RuntimeException("User with this email already exists");
        }
        
        // Check if company email already exists
        if (companyRepository.existsByEmail(request.getEmail())) {
            logger.warn("Signup failed - company already exists for email: {}", request.getEmail());
            throw new RuntimeException("Company with this email already exists");
        }
        
        try {
            // Create company
            Company company = createCompany(request);
            company = companyRepository.save(company);
            logger.debug("Company created with ID: {}", company.getId());
            
            // Create user
            User user = createUser(request, company);
            user = userRepository.save(user);
            logger.debug("User created with ID: {}", user.getId());
            
            // Handle plan selection if provided
            if (request.getSelectedPlan() != null) {
                handlePlanSelection(company, request.getSelectedPlan());
                logger.debug("Plan selected: {}", request.getSelectedPlan());
            }
            
            // Create response
            SignupResponse.UserInfo userInfo = new SignupResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType(),
                user.getStatus().name()
            );
            
            SignupResponse.CompanyInfo companyInfo = new SignupResponse.CompanyInfo(
                company.getId(),
                company.getName(),
                company.getEmail(),
                company.getStatus().name(),
                company.getSubscriptionStatus().name()
            );
            
            logger.info("Signup successful for user: {} (ID: {})", user.getEmail(), user.getId());
            
            return new SignupResponse(
                "Account created successfully",
                userInfo,
                companyInfo
            );
        } catch (Exception e) {
            logger.error("Signup failed for email: {} - {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }
    
    private Company createCompany(SignupRequest request) {
        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setAddressLine1(request.getAddressLine1());
        company.setAddressLine2(request.getAddressLine2());
        company.setCity(request.getCity());
        company.setState(request.getState());
        company.setZipCode(request.getZipCode());
        company.setCountry(request.getCountry());
        company.setStatus(CompanyStatus.PENDING);
        company.setSubscriptionStatus(SubscriptionStatus.TRIAL);
        company.setTrialEndsAt(LocalDateTime.now().plusDays(30)); // 30-day trial
        
        // Set custom fields if provided
        if (request.getSelectedPlan() != null) {
            String customFields = customFieldsService.setCustomField(
                company.getCustomFields(),
                "selected_plan",
                request.getSelectedPlan()
            );
            company.setCustomFields(customFields);
        }
        
        return company;
    }
    
    private User createUser(SignupRequest request, Company company) {
        User user = new User();
        user.setEmail(request.getEmail());
        
        // Encrypt password using BCrypt
        String encryptedPassword = passwordService.encryptPassword(request.getPassword());
        user.setPasswordHash(encryptedPassword);
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setUserType(UserType.BUSINESS_OWNER);
        user.setStatus(UserStatus.PENDING);
        user.setCompany(company);
        
        // Set custom fields for user preferences
        String customFields = customFieldsService.setCustomField(
            user.getCustomFields(),
            "signup_source",
            "web"
        );
        user.setCustomFields(customFields);
        
        logger.debug("User object created for email: {}", request.getEmail());
        return user;
    }
    
    private void handlePlanSelection(Company company, String planName) {
        Optional<Plan> planOpt = planRepository.findByName(planName);
        if (planOpt.isPresent()) {
            Plan plan = planOpt.get();
            
            // Update company custom fields with plan selection
            String customFields = customFieldsService.setCustomField(
                company.getCustomFields(),
                "selected_plan",
                plan.getName()
            );
            customFields = customFieldsService.setCustomField(
                customFields,
                "plan_selected_before_login",
                true
            );
            company.setCustomFields(customFields);
            
            // In a full implementation, you would also create a CompanySubscription here
        }
    }
} 
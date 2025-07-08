package com.payroll.texas.service;

import com.payroll.texas.model.Company;
import com.payroll.texas.model.CompanyStatus;
import com.payroll.texas.model.CompanySubscription;
import com.payroll.texas.model.Plan;
import com.payroll.texas.model.SubscriptionStatus;
import com.payroll.texas.model.BillingCycle;
import com.payroll.texas.repository.CompanyRepository;
import com.payroll.texas.repository.CompanySubscriptionRepository;
import com.payroll.texas.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private CompanySubscriptionRepository subscriptionRepository;
    
    @Transactional
    public void updateCompanySubscriptionStatus(Long companyId, String planName) {
        try {
            System.out.println("SubscriptionService: Updating subscription for company ID: " + companyId + ", plan: " + planName);
            
            // Find the company
            Optional<Company> companyOpt = companyRepository.findById(companyId);
            if (companyOpt.isEmpty()) {
                throw new RuntimeException("Company not found with ID: " + companyId);
            }
            
            // Find the plan
            Optional<Plan> planOpt = planRepository.findByName(planName);
            if (planOpt.isEmpty()) {
                // Get all available plan names for debugging
                List<String> availablePlans = planRepository.findAll().stream()
                    .map(Plan::getName)
                    .collect(java.util.stream.Collectors.toList());
                System.err.println("SubscriptionService: Plan not found: '" + planName + "'. Available plans: " + availablePlans);
                throw new RuntimeException("Plan not found: '" + planName + "'. Available plans: " + availablePlans);
            }
            
            Company company = companyOpt.get();
            Plan plan = planOpt.get();
            
            System.out.println("SubscriptionService: Found company: " + company.getName() + ", plan: " + plan.getName());
            
            // Update company subscription status based on plan
            SubscriptionStatus newStatus = determineSubscriptionStatus(plan);
            company.setSubscriptionStatus(newStatus);
            
            // Set trial end date if it's a trial plan
            if (newStatus == SubscriptionStatus.TRIAL) {
                company.setTrialEndsAt(LocalDateTime.now().plusDays(30)); // 30-day trial
            }
            
            // Update custom fields with plan selection
            String customFields = company.getCustomFields();
            if (customFields == null || customFields.isEmpty()) {
                customFields = "{}";
            }
            
            // Add plan selection to custom fields
            customFields = customFields.replace("}", "");
            if (!customFields.endsWith("{")) {
                customFields += ",";
            }
            customFields += "\"selected_plan\":\"" + plan.getName() + "\",\"plan_selected_at\":\"" + LocalDateTime.now() + "\"}";
            company.setCustomFields(customFields);
            
            company = companyRepository.save(company);
            System.out.println("SubscriptionService: Company updated with subscription status: " + newStatus);
            
            // Create or update company subscription record
            createOrUpdateCompanySubscription(company, plan, newStatus);
            System.out.println("SubscriptionService: Company subscription record created/updated");
            
        } catch (Exception e) {
            System.err.println("SubscriptionService: Error in updateCompanySubscriptionStatus: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Transactional
    public void updateCompanySubscriptionStatusByEmail(String companyEmail, String planName) {
        try {
            System.out.println("SubscriptionService: Starting plan selection for email: " + companyEmail + ", plan: " + planName);
            
            // Find the company by email
            Optional<Company> companyOpt = companyRepository.findByEmail(companyEmail);
            if (companyOpt.isEmpty()) {
                // If company doesn't exist, create it
                System.out.println("SubscriptionService: Company not found, creating new company for email: " + companyEmail);
                Company newCompany = createCompanyFromEmail(companyEmail);
                companyOpt = Optional.of(newCompany);
            }
            
            Company company = companyOpt.get();
            System.out.println("SubscriptionService: Found/created company with ID: " + company.getId());
            
            updateCompanySubscriptionStatus(company.getId(), planName);
            System.out.println("SubscriptionService: Plan selection completed successfully");
            
        } catch (Exception e) {
            System.err.println("SubscriptionService: Error in updateCompanySubscriptionStatusByEmail: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update subscription status: " + e.getMessage(), e);
        }
    }
    
    private SubscriptionStatus determineSubscriptionStatus(Plan plan) {
        // Determine subscription status based on plan type
        String planName = plan.getName().toUpperCase();
        
        switch (planName) {
            case "BASIC":
                return SubscriptionStatus.TRIAL; // Start with trial for basic plan
            case "STANDARD":
                return SubscriptionStatus.ACTIVE; // Standard plan is active
            case "PREMIUM":
                return SubscriptionStatus.ACTIVE; // Premium plan is active
            default:
                return SubscriptionStatus.TRIAL; // Default to trial
        }
    }
    
    private void createOrUpdateCompanySubscription(Company company, Plan plan, SubscriptionStatus status) {
        try {
            System.out.println("SubscriptionService: Creating/updating subscription for company: " + company.getId());
            
            // Check if subscription already exists
            Optional<CompanySubscription> existingSubscription = subscriptionRepository.findByCompanyId(company.getId());
            
            CompanySubscription subscription;
            if (existingSubscription.isPresent()) {
                // Update existing subscription
                System.out.println("SubscriptionService: Updating existing subscription");
                subscription = existingSubscription.get();
                subscription.setPlan(plan);
                subscription.setStatus(status);
                subscription.setMonthlyPrice(plan.getMonthlyPrice());
                subscription.setUpdatedAt(LocalDateTime.now());
            } else {
                // Create new subscription
                System.out.println("SubscriptionService: Creating new subscription");
                subscription = new CompanySubscription(
                    company,
                    plan,
                    BillingCycle.MONTHLY, // Default to monthly billing
                    plan.getMonthlyPrice()
                );
                subscription.setStatus(status);
                subscription.setStartDate(LocalDate.now());
                
                // Set trial end date if it's a trial
                if (status == SubscriptionStatus.TRIAL) {
                    subscription.setTrialEndsAt(LocalDateTime.now().plusDays(30));
                }
            }
            
            subscription = subscriptionRepository.save(subscription);
            System.out.println("SubscriptionService: Subscription saved with ID: " + subscription.getId());
            
        } catch (Exception e) {
            System.err.println("SubscriptionService: Error in createOrUpdateCompanySubscription: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private Company createCompanyFromEmail(String email) {
        Company company = new Company();
        company.setName("Company for " + email);
        company.setEmail(email);
        company.setStatus(CompanyStatus.PENDING);
        company.setSubscriptionStatus(SubscriptionStatus.TRIAL);
        company.setCustomFields("{}");
        
        company = companyRepository.save(company);
        System.out.println("SubscriptionService: Created new company with ID: " + company.getId());
        return company;
    }
    
    public boolean hasActiveSubscription(String userEmail) {
        try {
            // Find company by user email
            Optional<Company> companyOpt = companyRepository.findByEmail(userEmail);
            if (companyOpt.isEmpty()) {
                return false;
            }
            
            Company company = companyOpt.get();
            
            // Check if company has selected one of the valid plans (BASIC, STANDARD, PREMIUM)
            String customFields = company.getCustomFields();
            if (customFields != null && customFields.contains("selected_plan")) {
                // Extract the selected plan name from custom fields
                // This is a simplified check - in production you'd want to parse the JSON properly
                if (customFields.contains("BASIC") || customFields.contains("STANDARD") || customFields.contains("PREMIUM")) {
                    // User has selected a valid plan, check subscription status
                    if (company.getSubscriptionStatus() == SubscriptionStatus.ACTIVE || 
                        company.getSubscriptionStatus() == SubscriptionStatus.TRIAL) {
                        return true;
                    }
                }
            }
            
            // Also check if there's an active subscription record with a valid plan
            Optional<CompanySubscription> subscriptionOpt = subscriptionRepository.findByCompanyId(company.getId());
            if (subscriptionOpt.isPresent()) {
                CompanySubscription subscription = subscriptionOpt.get();
                if (subscription.getPlan() != null) {
                    String planName = subscription.getPlan().getName().toUpperCase();
                    if (planName.equals("BASIC") || planName.equals("STANDARD") || planName.equals("PREMIUM")) {
                        return subscription.getStatus() == SubscriptionStatus.ACTIVE || 
                               subscription.getStatus() == SubscriptionStatus.TRIAL;
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            // Log error but return false to be safe
            System.err.println("Error checking subscription status for user: " + userEmail + " - " + e.getMessage());
            return false;
        }
    }
    
    public boolean hasSelectedPlan(String userEmail) {
        try {
            // Find company by user email
            Optional<Company> companyOpt = companyRepository.findByEmail(userEmail);
            if (companyOpt.isEmpty()) {
                return false;
            }
            
            Company company = companyOpt.get();
            
            // Check if company has selected one of the valid plans (BASIC, STANDARD, PREMIUM)
            String customFields = company.getCustomFields();
            if (customFields != null && customFields.contains("selected_plan")) {
                // Check if any of the valid plans are selected
                if (customFields.contains("BASIC") || customFields.contains("STANDARD") || customFields.contains("PREMIUM")) {
                    return true;
                }
            }
            
            // Also check if there's a subscription record with a valid plan
            Optional<CompanySubscription> subscriptionOpt = subscriptionRepository.findByCompanyId(company.getId());
            if (subscriptionOpt.isPresent()) {
                CompanySubscription subscription = subscriptionOpt.get();
                if (subscription.getPlan() != null) {
                    String planName = subscription.getPlan().getName().toUpperCase();
                    return planName.equals("BASIC") || planName.equals("STANDARD") || planName.equals("PREMIUM");
                }
            }
            
            return false;
        } catch (Exception e) {
            // Log error but return false to be safe
            System.err.println("Error checking plan selection for user: " + userEmail + " - " + e.getMessage());
            return false;
        }
    }
} 
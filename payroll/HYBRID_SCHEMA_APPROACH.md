# 🎯 Hybrid Schema Approach: Static + Dynamic Fields

## 📋 Overview

The Texas Payroll system uses a **hybrid approach** combining:
- **Static fields** for core payroll requirements and compliance
- **Dynamic fields** (JSONB) for flexible business customization

## 🏗️ Schema Design Philosophy

### **Why Hybrid?**
- ✅ **Compliance**: Static fields ensure required payroll data
- ✅ **Flexibility**: JSONB allows custom business needs
- ✅ **Performance**: Structured queries on core fields
- ✅ **Scalability**: Easy to add new custom fields without schema changes

## 📊 Field Categorization

### **Static Fields (Core Requirements)**

#### **Users Table - Static Fields**
```sql
-- Authentication & Core Identity (MANDATORY)
id BIGSERIAL PRIMARY KEY,
email VARCHAR(255) UNIQUE NOT NULL,           -- Login identifier
password_hash VARCHAR(255) NOT NULL,          -- Authentication
user_type user_type NOT NULL,                 -- Business/Employee routing
status user_status DEFAULT 'PENDING',         -- Account status
first_name VARCHAR(100) NOT NULL,             -- Legal name
last_name VARCHAR(100) NOT NULL,              -- Legal name
phone VARCHAR(20),                            -- Contact info
company_id BIGINT REFERENCES companies(id),   -- Organization link
last_login_at TIMESTAMP,                      -- Security audit
failed_login_attempts INTEGER DEFAULT 0,      -- Security
account_locked_until TIMESTAMP,               -- Security
remember_me BOOLEAN DEFAULT FALSE,            -- User preference
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
deleted_at TIMESTAMP NULL
```

#### **Companies Table - Static Fields**
```sql
-- Core Business Identity (MANDATORY)
id BIGSERIAL PRIMARY KEY,
name VARCHAR(255) NOT NULL,                   -- Business name
legal_name VARCHAR(255),                      -- Legal entity name
email VARCHAR(255) UNIQUE NOT NULL,           -- Business contact
phone VARCHAR(20),                            -- Business contact
address_line1 VARCHAR(255),                   -- Tax filing address
address_line2 VARCHAR(255),                   -- Tax filing address
city VARCHAR(100),                            -- Tax filing address
state VARCHAR(50),                            -- Tax filing address
zip_code VARCHAR(20),                         -- Tax filing address
country VARCHAR(100) DEFAULT 'USA',           -- Tax jurisdiction
ein VARCHAR(255),                             -- Tax filing requirement
ein_encrypted BOOLEAN DEFAULT TRUE,           -- Security requirement
status company_status DEFAULT 'PENDING',      -- Business status
subscription_status subscription_status DEFAULT 'TRIAL', -- Billing
trial_ends_at TIMESTAMP,                      -- Billing
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
deleted_at TIMESTAMP NULL
```

#### **Employees Table - Static Fields**
```sql
-- Payroll Processing Requirements (MANDATORY)
id BIGSERIAL PRIMARY KEY,
company_id BIGINT NOT NULL,                   -- Organization link
employee_id VARCHAR(50),                      -- Internal ID
first_name VARCHAR(100) NOT NULL,             -- Legal name
last_name VARCHAR(100) NOT NULL,              -- Legal name
email VARCHAR(255),                           -- Contact
phone VARCHAR(20),                            -- Contact
employee_type employee_type NOT NULL,         -- Tax classification
status employee_status DEFAULT 'INCOMPLETE',  -- Employment status

-- Encrypted Sensitive Data (SECURITY REQUIREMENT)
ssn_encrypted VARCHAR(255),                   -- Tax filing requirement
ssn_encrypted_iv VARCHAR(255),                -- Security requirement
date_of_birth DATE,                           -- Employment verification

-- Employment Details (PAYROLL REQUIREMENT)
job_title VARCHAR(100),                       -- Job classification
department VARCHAR(100),                      -- Cost center
hire_date DATE,                               -- Payroll calculations
termination_date DATE,                        -- Payroll calculations

-- Compensation (PAYROLL CALCULATIONS)
compensation_type compensation_type NOT NULL, -- Pay type
hourly_rate DECIMAL(10,2),                    -- Hourly calculations
salary DECIMAL(12,2),                         -- Salary calculations
pay_frequency pay_frequency DEFAULT 'BI_WEEKLY', -- Pay schedule

-- Tax Information (TAX FILING REQUIREMENT)
federal_tax_exemptions INTEGER DEFAULT 0,     -- W-4 requirement
state_tax_exemptions INTEGER DEFAULT 0,       -- State W-4
additional_federal_withholding DECIMAL(10,2) DEFAULT 0, -- W-4
additional_state_withholding DECIMAL(10,2) DEFAULT 0,   -- State W-4

-- Direct Deposit (PAYMENT PROCESSING)
bank_account_number_encrypted VARCHAR(255),   -- Payment requirement
bank_account_number_encrypted_iv VARCHAR(255), -- Security
bank_routing_number_encrypted VARCHAR(255),   -- Payment requirement
bank_routing_number_encrypted_iv VARCHAR(255), -- Security
bank_name VARCHAR(100),                       -- Payment processing
account_type account_type DEFAULT 'CHECKING', -- Payment processing

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
deleted_at TIMESTAMP NULL
```

### **Dynamic Fields (JSONB Flexibility)**

#### **Users Table - Dynamic Fields**
```sql
-- User Preferences & Settings (OPTIONAL)
custom_fields JSONB DEFAULT '{}'
```

**Example User Custom Fields:**
```json
{
  "preferred_language": "en",
  "timezone": "America/Chicago",
  "notification_preferences": {
    "email": true,
    "sms": false,
    "push": true,
    "frequency": "daily"
  },
  "ui_preferences": {
    "theme": "dark",
    "dashboard_layout": "compact",
    "sidebar_collapsed": false
  },
  "security_preferences": {
    "two_factor_enabled": true,
    "session_timeout": 3600,
    "login_notifications": true
  },
  "work_preferences": {
    "default_view": "employees",
    "recent_searches": ["payroll", "taxes"],
    "favorite_reports": ["payroll_summary", "tax_report"]
  }
}
```

#### **Companies Table - Dynamic Fields**
```sql
-- Business Settings & Configuration (OPTIONAL)
custom_fields JSONB DEFAULT '{}'
```

**Example Company Custom Fields:**
```json
{
  "industry": "technology",
  "company_size": "small",
  "payroll_frequency": "bi_weekly",
  "fiscal_year_start": "01-01",
  "timezone": "America/Chicago",
  "holiday_schedule": {
    "christmas": true,
    "thanksgiving": true,
    "custom_holidays": ["2024-12-24", "2024-07-05"]
  },
  "benefits_config": {
    "health_insurance": true,
    "retirement_401k": true,
    "pto_policy": "unlimited",
    "sick_leave": "separate"
  },
  "compliance_settings": {
    "state_tax_codes": ["TX"],
    "local_tax_required": false,
    "overtime_threshold": 40,
    "meal_break_required": true
  },
  "integration_settings": {
    "accounting_software": "quickbooks",
    "time_tracking": "toggl",
    "hr_software": "bamboo"
  }
}
```

#### **Employees Table - Dynamic Fields**
```sql
-- Flexible Employee Data (OPTIONAL)
custom_fields JSONB DEFAULT '{}'
```

**Example Employee Custom Fields:**
```json
{
  "emergency_contact": {
    "name": "Jane Doe",
    "relationship": "spouse",
    "phone": "555-123-4567"
  },
  "benefits_enrollment": {
    "health_plan": "family",
    "dental_plan": "individual",
    "vision_plan": "none",
    "life_insurance": 50000
  },
  "work_schedule": {
    "monday": {"start": "09:00", "end": "17:00"},
    "tuesday": {"start": "09:00", "end": "17:00"},
    "wednesday": {"start": "09:00", "end": "17:00"},
    "thursday": {"start": "09:00", "end": "17:00"},
    "friday": {"start": "09:00", "end": "17:00"}
  },
  "skills_certifications": [
    "project_management",
    "agile_certification",
    "first_aid_certified"
  ],
  "performance_metrics": {
    "last_review_date": "2024-01-15",
    "performance_rating": 4.5,
    "goals_achieved": 8,
    "goals_total": 10
  }
}
```

## 🔄 User Login Process Field Categorization

### **Static Fields (Required for Login)**
1. **`email`** - Primary identifier for login
2. **`password_hash`** - Authentication verification
3. **`user_type`** - Business/Employee routing logic
4. **`status`** - Active/Inactive/Pending validation
5. **`company_id`** - Organization association
6. **`failed_login_attempts`** - Security throttling
7. **`account_locked_until`** - Security lockout
8. **`last_login_at`** - Audit trail

### **Dynamic Fields (Optional for Login)**
1. **`remember_me`** - User preference (could be dynamic)
2. **`custom_fields`** - User preferences, UI settings, etc.

## 🛠️ Implementation Benefits

### **Static Fields Benefits**
- ✅ **Type Safety**: Compile-time validation
- ✅ **Performance**: Direct column queries
- ✅ **Indexing**: Efficient database indexes
- ✅ **Constraints**: Database-level validation
- ✅ **Compliance**: Required fields enforced

### **Dynamic Fields Benefits**
- ✅ **Flexibility**: Add fields without schema changes
- ✅ **Customization**: Business-specific data
- ✅ **Future-Proof**: Easy to extend
- ✅ **No Migration**: New fields don't require database changes

### **Hybrid Benefits**
- ✅ **Best of Both**: Structure + Flexibility
- ✅ **Compliance**: Required fields guaranteed
- ✅ **Customization**: Optional fields available
- ✅ **Performance**: Core queries optimized
- ✅ **Maintainability**: Clear separation of concerns

## 📊 Usage Examples

### **Setting Custom Fields**
```java
// Using CustomFieldsService
customFieldsService.setCustomField(
    user.getCustomFields(), 
    "preferred_language", 
    "es"
);

// Nested fields
customFieldsService.setNestedCustomField(
    user.getCustomFields(),
    "notification_preferences.email",
    true
);
```

### **Getting Custom Fields**
```java
// Get simple field
Optional<String> language = customFieldsService.getCustomFieldString(
    user.getCustomFields(), 
    "preferred_language"
);

// Get nested field
Optional<Boolean> emailNotifications = customFieldsService.getNestedCustomField(
    user.getCustomFields(),
    "notification_preferences.email"
).map(JsonNode::asBoolean);
```

### **Querying Custom Fields**
```sql
-- Find users with specific preference
SELECT * FROM users 
WHERE custom_fields->>'preferred_language' = 'es';

-- Find companies in specific industry
SELECT * FROM companies 
WHERE custom_fields->>'industry' = 'technology';

-- Find employees with specific certification
SELECT * FROM employees 
WHERE custom_fields->'skills_certifications' ? 'agile_certification';
```

## 🎯 When to Use Each Type

### **Use Static Fields When:**
- Field is required for payroll processing
- Field is needed for tax filing
- Field is used in business logic
- Field needs database constraints
- Field is frequently queried
- Field is part of core functionality

### **Use Dynamic Fields When:**
- Field is optional or business-specific
- Field structure might change
- Field is for user preferences
- Field is for customization
- Field is rarely queried
- Field is experimental or temporary

## 🔧 Migration Strategy

### **Adding New Static Fields**
```sql
-- Requires migration
ALTER TABLE users ADD COLUMN new_required_field VARCHAR(100);
```

### **Adding New Dynamic Fields**
```java
// No migration required
customFieldsService.setCustomField(user.getCustomFields(), "new_field", value);
```

## 📈 Performance Considerations

### **Static Field Queries**
```sql
-- Fast - uses indexes
SELECT * FROM users WHERE email = 'user@example.com';
SELECT * FROM employees WHERE company_id = 123;
```

### **Dynamic Field Queries**
```sql
-- Slower - JSONB queries
SELECT * FROM users WHERE custom_fields->>'preferred_language' = 'es';
SELECT * FROM companies WHERE custom_fields->>'industry' = 'technology';
```

### **Optimization**
- Use GIN indexes on JSONB columns
- Keep frequently queried data in static fields
- Use dynamic fields for rarely queried data

## 🎯 Conclusion

The hybrid approach provides:
- **Compliance** through static fields
- **Flexibility** through dynamic fields
- **Performance** through proper indexing
- **Maintainability** through clear separation
- **Scalability** through easy extension

This approach is ideal for payroll systems that need both regulatory compliance and business customization. 
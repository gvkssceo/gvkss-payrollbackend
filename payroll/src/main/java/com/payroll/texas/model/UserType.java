package com.payroll.texas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    BUSINESS_OWNER,
    BUSINESS_ADMIN,
    EMPLOYEE;
    
    @JsonValue
    public String getValue() {
        return this.name();
    }
} 
# Texas Payroll Backend API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication Endpoints

### Login
- **POST** `/auth/login`
- **Description**: Authenticate user and get JWT tokens
- **Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "rememberMe": false
}
```
- **Response**:
```json
{
  "accessToken": "jwt_token_here",
  "refreshToken": "refresh_token_here",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "expiresAt": "2024-01-15T10:30:00",
  "userInfo": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "userType": "BUSINESS_OWNER",
    "companyId": 1,
    "companyName": "Test Company"
  }
}
```

### Signup
- **POST** `/auth/signup`
- **Description**: Register new user and company
- **Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "companyName": "Test Company",
  "phone": "555-123-4567",
  "addressLine1": "123 Main St",
  "city": "Austin",
  "state": "TX",
  "zipCode": "78701",
  "selectedPlan": "BASIC",
  "planSelectedBeforeLogin": false
}
```
- **Response**:
```json
{
  "message": "Account created successfully",
  "userInfo": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "userType": "BUSINESS_OWNER",
    "status": "PENDING"
  },
  "companyInfo": {
    "id": 1,
    "name": "Test Company",
    "email": "user@example.com",
    "status": "PENDING",
    "subscriptionStatus": "TRIAL"
  },
  "createdAt": "2024-01-15T10:30:00"
}
```

### Refresh Token
- **POST** `/auth/refresh`
- **Description**: Get new access token using refresh token
- **Headers**: `Authorization: Bearer <refresh_token>`
- **Response**:
```json
{
  "accessToken": "new_jwt_token_here",
  "tokenType": "Bearer"
}
```

### Logout
- **POST** `/auth/logout`
- **Description**: Logout user and invalidate token
- **Headers**: `Authorization: Bearer <access_token>`
- **Response**:
```json
{
  "message": "Successfully logged out"
}
```

### Validate Token
- **GET** `/auth/validate`
- **Description**: Validate if token is still valid
- **Headers**: `Authorization: Bearer <access_token>`
- **Response**:
```json
{
  "valid": true
}
```

### Email Validation
- **POST** `/auth/signup/validate-email`
- **Description**: Validate email format
- **Request Body**:
```json
{
  "email": "user@example.com"
}
```
- **Response**:
```json
{
  "valid": true,
  "message": "Email format is valid"
}
```

### Password Validation
- **POST** `/auth/signup/validate-password`
- **Description**: Validate password strength
- **Request Body**:
```json
{
  "password": "password123"
}
```
- **Response**:
```json
{
  "valid": true,
  "strength": "Medium",
  "score": 2,
  "message": "Password is valid"
}
```

## Enrollment Endpoints

### Begin Enrollment
- **POST** `/enrollment/begin`
- **Description**: Start enrollment process
- **Request Body**:
```json
{
  "companyName": "Test Company",
  "contactName": "John Doe",
  "contactEmail": "john@testcompany.com",
  "contactPhone": "555-123-4567",
  "addressLine1": "123 Main St",
  "city": "Austin",
  "state": "TX",
  "zipCode": "78701",
  "selectedPlan": "BASIC",
  "planSelectedBeforeLogin": false,
  "customFields": "{}"
}
```
- **Response**:
```json
{
  "message": "Enrollment started successfully",
  "enrollmentId": 1,
  "currentStep": "INITIAL",
  "enrollmentData": {
    "companyName": "Test Company",
    "contactName": "John Doe",
    "contactEmail": "john@testcompany.com",
    "contactPhone": "555-123-4567",
    "selectedPlan": "BASIC",
    "planSelectedBeforeLogin": false,
    "customFields": "{}"
  },
  "createdAt": "2024-01-15T10:30:00"
}
```

### Update Enrollment Step
- **POST** `/enrollment/{enrollmentId}/step`
- **Description**: Update enrollment step
- **Request Body**:
```json
{
  "step": "COMPANY_INFO"
}
```
- **Response**: Same as begin enrollment

### Select Plan
- **POST** `/enrollment/{enrollmentId}/select-plan`
- **Description**: Select a plan during enrollment
- **Request Body**:
```json
{
  "planName": "STANDARD"
}
```
- **Response**: Same as begin enrollment

### Get Enrollment Status
- **GET** `/enrollment/status/{email}`
- **Description**: Get enrollment status by email
- **Response**: Same as begin enrollment

### Get Enrollment Steps
- **GET** `/enrollment/steps`
- **Description**: Get available enrollment steps
- **Response**:
```json
{
  "steps": ["INITIAL", "COMPANY_INFO", "PLAN_SELECTION", "ACCOUNT_CREATION", "COMPLETED"],
  "message": "Available enrollment steps retrieved"
}
```

### Validate Company Info
- **POST** `/enrollment/validate-company`
- **Description**: Validate company information
- **Request Body**:
```json
{
  "companyName": "Test Company",
  "contactName": "John Doe",
  "contactEmail": "john@testcompany.com",
  "contactPhone": "555-123-4567"
}
```
- **Response**:
```json
{
  "valid": true,
  "message": "Company information is valid"
}
```

## Plan Endpoints

### Get All Plans
- **GET** `/plans`
- **Description**: Get all active plans
- **Response**:
```json
[
  {
    "id": 1,
    "name": "BASIC",
    "displayName": "Basic",
    "description": "Full payroll processing for small businesses",
    "monthlyPrice": 39.00,
    "yearlyPrice": 390.00,
    "maxEmployees": 5,
    "features": [
      "Full payroll processing",
      "Unlimited direct deposits",
      "Federal & state tax filings",
      "Basic compliance reporting",
      "Email support (72hr response)"
    ],
    "isActive": true,
    "isFeatured": false,
    "sortOrder": 1
  }
]
```

### Get Featured Plans
- **GET** `/plans/featured`
- **Description**: Get featured plans only
- **Response**: Same as get all plans

### Get Plan by ID
- **GET** `/plans/{id}`
- **Description**: Get specific plan by ID
- **Response**: Single plan object

### Get Plan by Name
- **GET** `/plans/name/{name}`
- **Description**: Get specific plan by name
- **Response**: Single plan object

### Search Plans
- **GET** `/plans/search?maxEmployees=10&minPrice=30&maxPrice=100`
- **Description**: Search plans by criteria
- **Parameters**:
  - `maxEmployees`: Maximum number of employees
  - `minPrice`: Minimum monthly price
  - `maxPrice`: Maximum monthly price
- **Response**: List of matching plans

### Compare Plans
- **GET** `/plans/compare?planNames=BASIC&planNames=STANDARD`
- **Description**: Compare multiple plans
- **Response**:
```json
{
  "BASIC": {
    "id": 1,
    "name": "BASIC",
    "displayName": "Basic",
    "monthlyPrice": 39.00,
    "features": [...]
  },
  "STANDARD": {
    "id": 2,
    "name": "STANDARD",
    "displayName": "Standard",
    "monthlyPrice": 99.00,
    "features": [...]
  }
}
```

### Get Recommendations
- **GET** `/plans/recommendations?employeeCount=10&budget=100`
- **Description**: Get plan recommendations
- **Parameters**:
  - `employeeCount`: Number of employees
  - `budget`: Monthly budget
- **Response**: List of recommended plans

### Select Plan
- **POST** `/plans/select`
- **Description**: Select a plan and update subscription status
- **Request Body**:
```json
{
  "companyEmail": "company@example.com",
  "planName": "BASIC"
}
```
- **Response**:
```json
{
  "message": "Plan selected and subscription status updated successfully",
  "planName": "BASIC",
  "companyEmail": "company@example.com"
}
```

## Health Check Endpoints

### Health Check
- **GET** `/health`
- **Description**: Check service health
- **Response**:
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T10:30:00",
  "service": "Texas Payroll Backend",
  "version": "1.0.0",
  "environment": "development"
}
```

### Ping
- **GET** `/health/ping`
- **Description**: Simple ping endpoint
- **Response**: `"pong"`

## Test Endpoints

### Test
- **GET** `/test`
- **Description**: Test endpoint
- **Response**: `"Test endpoint working!"`

## Error Responses

All endpoints return consistent error responses:

```json
{
  "message": "Error description",
  "error": "Detailed error message",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

## CORS

All endpoints support CORS with the following configuration:
- Allowed Origins: `*`
- Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed Headers: `*`
- Allow Credentials: true

## Rate Limiting

Currently no rate limiting is implemented, but it's recommended for production use.

## Data Validation

All endpoints include comprehensive validation:
- Email format validation
- Password strength validation
- Required field validation
- Business logic validation

## Security Features

- JWT-based authentication
- Password strength requirements
- Input validation and sanitization
- CORS protection
- Error handling without sensitive data exposure 
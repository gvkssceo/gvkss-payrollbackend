commited by srinivas
commited by Dinesh

# Texas Payroll Backend

A comprehensive payroll management system built with Spring Boot, PostgreSQL, and JWT authentication.

## 🚀 Features

- **User Authentication**: JWT-based authentication with business/employee login
- **Multi-step Enrollment**: Complete enrollment process with plan selection
- **Company Management**: Full company profile and employee management
- **Plan Subscriptions**: Flexible subscription management
- **Data Encryption**: AES-256 encryption for sensitive data (SSN, bank details)
- **Audit Logging**: Comprehensive audit trail for compliance
- **RESTful APIs**: Complete API for frontend integration

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Java**: 17
- **Database**: PostgreSQL 14+
- **Security**: Spring Security + JWT
- **Encryption**: AES-256-GCM
- **Migration**: Flyway
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL 14 or higher
- Maven 3.6 or higher
- Node.js (for frontend integration)

## 🗄️ Database Setup

### 1. Install PostgreSQL
```bash
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# macOS
brew install postgresql

# Windows
# Download from https://www.postgresql.org/download/windows/
```

### 2. Create Database
```sql
-- Connect to PostgreSQL as superuser
sudo -u postgres psql

-- Create database and user
CREATE DATABASE texas_payroll;
CREATE USER texas_payroll_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE texas_payroll TO texas_payroll_user;
```

### 3. Update Configuration
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/texas_payroll
    username: texas_payroll_user
    password: your_secure_password
```

## 🔐 Security Configuration

### 1. Generate Encryption Keys
```bash
# Generate JWT secret (256 bits)
openssl rand -base64 32

# Generate encryption key (256 bits)
openssl rand -base64 32
```

### 2. Update Security Settings
Edit `src/main/resources/application.yml`:
```yaml
jwt:
  secret: your-generated-jwt-secret-here

app:
  encryption:
    key: your-generated-encryption-key-here
```

## 🚀 Running the Application

### 1. Development Mode
```bash
# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=development
```

### 2. Production Mode
```bash
# Build the application
mvn clean package

# Run the JAR
java -jar target/payroll-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
```

## 📊 API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user

### Enrollment
- `POST /api/enrollment/begin` - Start enrollment
- `POST /api/enrollment/complete` - Complete enrollment
- `GET /api/enrollment/status` - Check enrollment status

### Plans
- `GET /api/plans` - List available plans
- `POST /api/plans/select` - Select a plan

### Companies
- `GET /api/companies/profile` - Get company profile
- `PUT /api/companies/profile` - Update company profile

### Employees
- `GET /api/employees` - List employees
- `POST /api/employees` - Add employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee

## 🔄 User Journey Flow

1. **Enrollment**: User starts enrollment process
2. **Account Creation**: User creates account with email/password
3. **Plan Selection**: User selects subscription plan
4. **Login**: User logs in with credentials
5. **Dashboard**: User accesses company dashboard

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run with coverage
mvn test jacoco:report
```

### API Testing
```bash
# Test authentication
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","firstName":"John","lastName":"Doe","companyName":"Test Corp"}'

# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","userType":"business"}'
```

## 📁 Project Structure

```
src/main/java/com/payroll/texas/
├── PayrollApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── JwtConfig.java
├── controller/
│   ├── AuthController.java
│   ├── EnrollmentController.java
│   ├── CompanyController.java
│   ├── EmployeeController.java
│   └── PlanController.java
├── service/
│   ├── AuthService.java
│   ├── EnrollmentService.java
│   ├── CompanyService.java
│   ├── EmployeeService.java
│   ├── PlanService.java
│   └── EncryptionService.java
├── repository/
│   ├── UserRepository.java
│   ├── CompanyRepository.java
│   ├── EmployeeRepository.java
│   ├── PlanRepository.java
│   ├── CompanySubscriptionRepository.java
│   └── EnrollmentDataRepository.java
├── model/
│   ├── User.java
│   ├── Company.java
│   ├── Employee.java
│   ├── Plan.java
│   ├── CompanySubscription.java
│   └── EnrollmentData.java
├── dto/
│   ├── LoginRequest.java
│   ├── SignupRequest.java
│   ├── EnrollmentRequest.java
│   ├── EmployeeRequest.java
│   └── ApiResponse.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── CustomExceptions.java
```

## 🔧 Configuration

### Environment Variables
```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/texas_payroll
DATABASE_USERNAME=texas_payroll_user
DATABASE_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your-jwt-secret
JWT_EXPIRATION=86400000

# Encryption
ENCRYPTION_KEY=your-encryption-key
```

### Profiles
- **development**: Local development with detailed logging
- **production**: Production settings with minimal logging

## 📈 Monitoring

### Logs
- Application logs: `logs/texas-payroll.log`
- Console logs: Standard output
- Audit logs: Database table `audit_logs`

### Health Checks
- `GET /api/health` - Application health
- `GET /api/health/db` - Database health

## 🚀 Deployment

### Docker
```bash
# Build Docker image
docker build -t texas-payroll-backend .

# Run container
docker run -p 8080:8080 texas-payroll-backend
```

### Cloud Deployment
- **AWS**: Deploy to ECS or EC2
- **Azure**: Deploy to App Service
- **GCP**: Deploy to Cloud Run

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 🔄 Version History

- **v1.0.0**: Initial release with basic functionality
- **v1.1.0**: Added encryption and audit logging
- **v1.2.0**: Enhanced security and performance 
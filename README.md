# Employee Management System Backend

A production-ready Spring Boot backend for Employee Task, Work, HR & Performance Management.

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8+ |
| Build | Maven |
| Docs | SpringDoc OpenAPI 3 / Swagger |
| Tests | JUnit 5 + Mockito |

## Project Structure

```
backend/
├── src/main/java/com/company/employeemanagement/
│   ├── config/           # Security, OpenAPI, DataSeeder
│   ├── controller/       # 11 REST controllers
│   ├── dto/              # Request/Response DTOs (no entity exposure)
│   ├── entity/           # 11 JPA entities + 10 enums
│   ├── exception/        # GlobalExceptionHandler + custom exceptions
│   ├── mapper/           # Manual entity ↔ DTO mappers
│   ├── repository/       # 10 JPA repositories with custom queries
│   ├── security/         # JWT provider, filter, UserPrincipal
│   ├── service/          # Service interfaces + implementations
│   ├── specification/    # JPA Specifications for dynamic filtering
│   └── util/             # SecurityUtils, DateUtils
└── src/main/resources/
    ├── application.properties
    └── application-dev.properties
```

## Quick Setup

### 1. Prerequisites
- Java 17+ (`java --version`)
- Maven 3.8+ (`mvn --version`)
- MySQL 8+ running locally

### 2. Create Database
```sql
CREATE DATABASE employee_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure Environment
Copy `.env.example` to `.env` and set your values:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=employee_management
DB_USERNAME=root
DB_PASSWORD=your_password_here
JWT_SECRET=your-256-bit-base64-secret
```

### 4. Run the Application

**Option A — Pass env vars directly:**
```bash
cd backend

# Windows CMD
set DB_PASSWORD=yourpassword && mvn spring-boot:run

# Windows PowerShell
$env:DB_PASSWORD="yourpassword"; mvn spring-boot:run
```

**Option B — Use IDE run config:** Set environment variables in your IDE's run configuration.

### 5. Verify
- Health check: http://localhost:8080/api/health
- Swagger UI: http://localhost:8080/swagger-ui.html

## Test Credentials (auto-seeded on startup)

| Role | Employee ID | Email | Password |
|---|---|---|---|
| ADMIN | ADMIN001 | admin@company.com | `Admin@12345` |
| HR | HR001 | hr@company.com | `Hr@12345` |
| EMPLOYEE | EMP001 | emp@company.com | `Emp@12345` |

## API Overview

### Authentication
| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/login` | Login (employeeId/email + password) |
| POST | `/api/auth/logout` | Logout (JWT identity, saves session duration) |

### Admin Endpoints
| Method | Path | Description |
|---|---|---|
| GET | `/api/admin/dashboard` | Real-time dashboard stats |
| GET/PUT/PATCH/DELETE | `/api/admin/employees/**` | Employee management |
| GET | `/api/admin/login-activity` | Login activity (filterable) |
| GET | `/api/admin/login-activity/today` | Today's activity |
| GET | `/api/admin/login-activity/employee/{id}` | Employee history |
| POST/GET/PUT/DELETE | `/api/admin/tasks` | Task CRUD |
| GET | `/api/admin/reports/daily|weekly|monthly` | Reports |

### HR Endpoints
| Method | Path | Description |
|---|---|---|
| GET | `/api/hr/dashboard` | HR dashboard |
| POST | `/api/hr/employees` | Create employee |
| GET | `/api/hr/employees/**` | Browse employees |
| GET | `/api/hr/leaves` | All leave requests |
| POST | `/api/hr/leaves/{id}/approve` | Approve leave |
| POST | `/api/hr/leaves/{id}/reject` | Reject leave |
| GET | `/api/hr/attendance` | Attendance records |
| GET | `/api/hr/reports/**` | HR reports |

### Employee Endpoints
| Method | Path | Description |
|---|---|---|
| GET | `/api/employee/dashboard` | Own dashboard |
| GET | `/api/employee/profile` | Own profile |
| POST | `/api/employee/change-password` | Change password |
| GET/PATCH | `/api/employee/tasks/**` | Own tasks |
| POST/GET | `/api/employee/daily-work` | Daily work logs |
| POST/GET/PATCH | `/api/employee/leaves/**` | Leave management |

### Other
| Method | Path | Description |
|---|---|---|
| GET | `/api/health` | Health check |
| GET/PATCH | `/api/notifications/**` | Notifications |

## Running Tests
```bash
cd backend
mvn test
```

## Key Security Features

- **BCrypt** password hashing (never stored plain)
- **JWT** stateless authentication (24-hour expiry)
- **Role-based authorization**: `@PreAuthorize` on every endpoint
- **SecurityContext identity**: logout and data operations use JWT identity, never request body
- **CORS** configured for `http://localhost:5173` (React dev)
- **Input validation**: Bean Validation on all request DTOs
- **Audit logging**: All important actions recorded with user, timestamp, and description

## Login Activity Flow

```
Employee logs in
  → LoginActivity record CREATED (loginDate, loginTime, status=ACTIVE)
  → Never overwrites previous sessions

Employee logs out (POST /api/auth/logout)
  → JWT identity used to find user (never from request body)
  → Active session found → logoutTime saved
  → sessionDuration calculated (HH:MM:SS format)
  → status changed to LOGGED_OUT

Browser closes without logout
  → Session record stays ACTIVE with NULL logoutTime
  → Duration is NOT fabricated
```

## Database Tables Created Automatically

Via `spring.jpa.hibernate.ddl-auto=update`:

| Table | Description |
|---|---|
| `departments` | Company departments |
| `users` | All users (admin/hr/employee) |
| `login_activity` | Login/logout tracking |
| `tasks` | Task assignments |
| `daily_work` | Daily work logs |
| `leave_requests` | Leave applications |
| `employee_documents` | Document metadata |
| `notifications` | In-app notifications |
| `audit_logs` | Action audit trail |
| `manager_feedback` | Performance feedback |

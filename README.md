# DRRMS — Disaster Relief Resource Management System

A full-stack web application for coordinating disaster relief operations — managing resources, shelters, volunteer assignments, and citizen aid requests in real time.

## 🏗️ Architecture

```
DRRMS/
├── backend/          # Java Spring Boot 3.3 REST API
├── frontend/         # React 19 + Vite SPA
├── database/         # MySQL schema & seed data
└── README.md
```

### Tech Stack

| Layer      | Technology                                                            |
|------------|-----------------------------------------------------------------------|
| Frontend   | React 19, React Router 7, Vite 6, Vanilla CSS                        |
| Backend    | Java 17, Spring Boot 3.3.2, Spring Data JPA, Spring Security         |
| Auth       | JWT (jjwt 0.12.6) with BCrypt password hashing                       |
| Database   | MySQL 8+ with triggers for audit logging                              |
| Testing    | JUnit 5, Mockito, MockMvc, H2 (in-memory)                            |

---

## 🧱 SOLID Design Principles

### Single Responsibility (SRP)
- **Controllers** handle only HTTP request/response mapping — no business logic.
- **Services** encapsulate business rules and orchestrate transactions.
- **Repositories** handle persistence only.
- **GlobalExceptionHandler** is solely responsible for mapping exceptions to HTTP responses.

### Open/Closed (OCP)
- Service interfaces (`AuthService`, `ResourceService`, etc.) allow new implementations without modifying consumers.
- `SecurityConfig` allows new role rules without modifying existing authorization logic.
- `@EnableMethodSecurity` enables fine-grained access control via annotations.

### Liskov Substitution (LSP)
- `RequestServiceImpl` implements `CitizenRequestService`, `VolunteerTaskService`, and `AdminRequestService` — any of these interfaces can be substituted wherever the other is expected.
- `User` implements `UserDetails`, seamlessly integrating with Spring Security's authentication pipeline.

### Interface Segregation (ISP)
- Request operations are split into three focused interfaces:
  - `CitizenRequestService` — create requests, view my requests
  - `VolunteerTaskService` — accept tasks, complete tasks, view resources
  - `AdminRequestService` — view all requests, update status
- This ensures each role's controller only depends on the operations it needs.

### Dependency Inversion (DIP)
- Controllers depend on **service interfaces**, not implementations.
- `JwtAuthenticationFilter` depends on `JwtTokenProvider` (component), not raw JWT parsing.
- `AuthServiceImpl` depends on `PasswordEncoder` (interface), not `BCryptPasswordEncoder` directly.

---

## 📦 Backend Package Structure

```
com.relieflink
├── ReliefLinkApplication.java          # @SpringBootApplication entry point
├── common/
│   ├── ApiResponse.java                # Generic response wrapper
│   ├── GlobalExceptionHandler.java     # @ControllerAdvice
│   ├── enums/                          # Role, RequestStatus, WeatherAlert, etc.
│   └── exception/                      # ResourceNotFoundException, BadRequestException, etc.
├── config/
│   ├── SecurityConfig.java             # Spring Security + JWT filter chain
│   ├── JwtTokenProvider.java           # Token generation & validation
│   ├── JwtAuthenticationFilter.java    # OncePerRequestFilter
│   └── CorsConfig.java                # CORS for React frontend
├── auth/
│   ├── AuthService.java / AuthServiceImpl.java
│   ├── AuthController.java
│   └── dto/                            # LoginRequest, RegisterRequest, AuthResponse
├── user/
│   ├── User.java                       # JPA entity + UserDetails
│   ├── UserRepository.java
│   ├── UserService.java / UserServiceImpl.java
│   ├── UserController.java
│   └── dto/                            # UserProfileResponse, UpdateProfileRequest
├── resource/
│   ├── Resource.java, Location.java    # JPA entities
│   ├── ResourceRepository.java, LocationRepository.java
│   ├── ResourceService.java / ResourceServiceImpl.java
│   ├── LocationService.java / LocationServiceImpl.java
│   ├── ResourceController.java, LocationController.java
│   └── dto/                            # ResourceRequest/Response, LocationRequest/Response
├── shelter/
│   ├── Shelter.java                    # JPA entity
│   ├── ShelterRepository.java
│   ├── ShelterService.java / ShelterServiceImpl.java
│   ├── ShelterController.java
│   └── dto/                            # ShelterRequest, ShelterResponse
├── request/
│   ├── Request.java, VolunteerRequest.java                # JPA entities
│   ├── RequestRepository.java, VolunteerRequestRepository.java
│   ├── CitizenRequestService.java (interface)
│   ├── VolunteerTaskService.java (interface)
│   ├── AdminRequestService.java (interface)
│   ├── RequestServiceImpl.java         # Implements all 3 interfaces
│   ├── RequestController.java
│   └── dto/                            # CreateRequestDto, AdminRequestResponse, etc.
└── audit/
    ├── AuditLog.java, ResourceAuditLog.java
    ├── AuditLogRepository.java, ResourceAuditLogRepository.java
    ├── AuditService.java / AuditServiceImpl.java
    └── AuditController.java
```

---

## 🌐 API Routes

### Public
| Method | Endpoint               | Description            |
|--------|------------------------|------------------------|
| POST   | `/api/auth/login`      | Login with JWT         |
| POST   | `/api/auth/register`   | Register new user      |
| POST   | `/api/auth/logout`     | Client-side logout     |

### Citizen (ROLE_CITIZEN)
| Method | Endpoint               | Description              |
|--------|------------------------|--------------------------|
| POST   | `/api/requests`        | Submit a resource request|
| GET    | `/api/requests/my`     | View my requests         |

### Volunteer (ROLE_VOLUNTEER)
| Method | Endpoint                        | Description            |
|--------|---------------------------------|------------------------|
| GET    | `/api/volunteers/requests`      | View pending requests  |
| POST   | `/api/volunteers/assign-task`   | Accept a task          |
| POST   | `/api/volunteers/complete-task` | Mark task complete     |
| GET    | `/api/volunteers/my-tasks`      | View assigned tasks    |
| GET    | `/api/volunteers/resources`     | Browse resources       |

### Admin (ROLE_ADMIN)
| Method | Endpoint                              | Description              |
|--------|---------------------------------------|--------------------------|
| GET    | `/api/admin/requests`                 | All requests + volunteer |
| PUT    | `/api/admin/requests/{id}/status`     | Update request status    |
| GET    | `/api/admin/audit-logs`               | System audit logs        |
| GET    | `/api/admin/resource-audit-logs`      | Resource change history  |
| CRUD   | `/api/resources`, `/api/locations`, `/api/shelters` | Manage entities |
| GET    | `/api/users`                          | List all users           |

### Shared (Any Authenticated User)
| Method | Endpoint                           | Description        |
|--------|------------------------------------|--------------------|
| GET    | `/api/users/{id}/profile`          | View profile       |
| PUT    | `/api/users/{id}/profile`          | Update profile     |
| GET    | `/api/resources`, `/api/locations` | Read-only access   |
| GET    | `/api/shelters`                    | Read-only access   |

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (JDK)
- **Maven 3.8+**
- **Node.js 18+** and **npm 9+**
- **MySQL 8+** running on localhost:3306

### 1. Database Setup

Open terminal in the `DRRMS` root folder and connect to MySQL:

```bash
mysql -u root -p
```

Inside the `mysql>` prompt:

```sql
DROP DATABASE IF EXISTS drrms_db;
CREATE DATABASE drrms_db;
USE drrms_db;
SOURCE c:/Users/vijay/OneDrive/Desktop/DRRMS_Migration/DRRMS/database/schema.sql;
SOURCE c:/Users/vijay/OneDrive/Desktop/DRRMS_Migration/DRRMS/database/seed.sql;
```

> **Note**: `SOURCE` resolves paths relative to where your terminal was opened. If you opened `mysql` inside the `backend` directory, use `SOURCE ../database/schema.sql;` or full path `SOURCE c:/Users/vijay/OneDrive/Desktop/DRRMS_Migration/DRRMS/database/schema.sql;`.

### 2. Backend

```bash
cd backend

# Set environment variables (or use defaults in application.properties)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=drrms_db
export DB_USER=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-secret-key-at-least-32-characters-long

# Build and run (Windows)
mvn spring-boot:run

# Or (Linux / macOS)
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080/api`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

The app starts at `http://localhost:5173`.

### 4. Test Login Credentials (from seed data)

| Email              | Password   | Role      |
|--------------------|------------|-----------|
| admin@example.com  | adminpass  | admin     |
| raj@example.com    | volpass    | volunteer |
| deepa@example.com  | deeppass   | citizen   |

---

## 🧪 Running Tests

```bash
cd backend

# Unit tests (AuthService, ResourceService)
./mvnw test -Dtest=AuthServiceTest
./mvnw test -Dtest=ResourceServiceTest

# Integration tests (MockMvc, H2)
./mvnw test -Dtest=RequestControllerIntegrationTest

# All tests
./mvnw test
```

---

## 📁 Frontend Structure

```
frontend/src/
├── main.jsx                    # Entry point (BrowserRouter + AuthProvider)
├── App.jsx                     # Route definitions
├── hooks/
│   └── useAuth.jsx             # Auth context (login, register, logout, user state)
├── components/
│   ├── ProtectedRoute.jsx      # Role-based route guard
│   └── Navbar.jsx
├── services/                   # API service layer (centralized fetch logic)
│   ├── authService.js
│   ├── resourceService.js
│   ├── locationService.js
│   ├── shelterService.js
│   ├── requestService.js
│   ├── userService.js
│   └── auditService.js
├── utils/
│   └── apiFetch.js             # Centralized fetch with JWT injection + 401 handling
├── pages/
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── AdminDashboard.jsx
│   ├── VolunteerDashboard.jsx
│   ├── UserDashboard.jsx
│   ├── CreateRequest.jsx
│   ├── FindResources.jsx
│   ├── MyRequests.jsx
│   ├── Shelters.jsx
│   └── ProfileSettings.jsx
└── styles/                     # Vanilla CSS modules
```

---

## 📄 License

This project is for educational and interview demonstration purposes.

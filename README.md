# JWT Authentication Service — Spring Boot

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8%2B-blue?logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-purple?logo=jsonwebtokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-red?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow)

A **beginner-friendly**, production-structured **JWT Authentication REST API** built with **Spring Boot**. This project demonstrates real-world backend concepts including REST APIs, BCrypt password hashing, JWT token generation/validation, Spring Security filter chains, and JPA/MySQL integration — all with clean, heavily commented code.

---

##  Features

| Feature | Details |
|---|---|
|  **JWT Authentication** | Stateless token-based auth (no sessions) |
|  **BCrypt Password Hashing** | Passwords hashed with strength-12 BCrypt |
|  **User Registration** | Validates uniqueness and saves to DB |
|  **User Login** | Returns signed JWT token (24h expiry) |
|  **Protected Endpoint** | JWT filter intercepts and validates token |
|  **Role Support** | Basic role field (USER/ADMIN) on each user |
|  **MySQL + JPA** | Auto table creation via Hibernate DDL |
|  **Clean Architecture** | Controller → Service → Repository layers |

---

##  Project Structure

```
src/main/java/com/example/auth/
│
├── AuthServiceApplication.java       ← App entry point (@SpringBootApplication)
│
├── controller/
│   └── AuthController.java           ← REST endpoints (register, login, test)
│
├── service/
│   └── AuthService.java              ← Business logic (register, login)
│
├── repository/
│   └── UserRepository.java           ← JPA interface (DB queries)
│
├── model/
│   └── User.java                     ← User entity (@Entity → MySQL table)
│
├── dto/
│   ├── AuthRequest.java              ← Request body (username + password)
│   └── AuthResponse.java             ← Response body (JWT token)
│
├── security/
│   ├── JwtUtil.java                  ← Token generate / validate / extract
│   └── JwtAuthFilter.java            ← Filter: validates Bearer token per request
│
└── config/
    └── SecurityConfig.java           ← Spring Security rules + BCrypt bean
```

---

##  API Endpoints

### Base URL: `http://localhost:8080`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/auth/register` | 🟢 Public | Register a new user |
| `POST` | `/auth/login` | 🟢 Public | Login and receive JWT token |
| `GET` | `/auth/test` | 🔴 Protected | Access with a valid JWT token |

---

##  Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 17+ |
| Apache Maven | 3.8+ |
| MySQL Server | 8.0+ |

---

##  Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/spring-boot-jwt-auth.git
cd spring-boot-jwt-auth
```

### 2. Create the MySQL Database

```sql
CREATE DATABASE auth_db;
```

> **Note:** The `users` table is created automatically by Hibernate on first run.

### 3. Configure Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=yourpassword   # ← change this
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

You should see:
```
Auth Service is up and running on http://localhost:8080
```

---

##  API Usage (Postman / curl)

### 1️1. Register a New User

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "john", "password": "secret123"}'
```

**Response `201 Created`:**
```
User registered successfully: john
```

**Error `400 Bad Request` (username taken):**
```
Username already exists: john
```

---

### 2️2. Login and Get JWT Token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john", "password": "secret123"}'
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwia..."
}
```

**Error `401 Unauthorized`:**
```
Invalid credentials
```

---

### 3️3. Access Protected Endpoint

Copy the token from the login response and pass it in the `Authorization` header:

```bash
curl http://localhost:8080/auth/test \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Response `200 OK`:**
```
 Hello john! You've accessed a protected endpoint. Your role: [ROLE_USER]
```

**Without token — `403 Forbidden`:**
```json
{ "status": 403, "error": "Forbidden" }
```

---

##  How JWT Authentication Works

```
CLIENT                                  SERVER
  │                                       │
  │── POST /auth/login ─────────────────►│
  │   { username, password }             │
  │                                       │  1. Find user in DB
  │                                       │  2. Verify BCrypt password
  │                                       │  3. Generate JWT (24h expiry)
  │◄── { "token": "eyJ..." } ───────────│
  │                                       │
  │── GET /auth/test ───────────────────►│
  │   Authorization: Bearer eyJ...       │
  │                                       │  4. JwtAuthFilter reads the header
  │                                       │  5. Extracts username from token
  │                                       │  6. Validates signature & expiry
  │                                       │  7. Sets auth in SecurityContext
  │◄── 200 OK "Hello john!" ────────────│
```

---

##  Architecture Overview

```
HTTP Request
      │
      ▼
 JwtAuthFilter          ← Reads & validates Bearer token (runs on every request)
      │
      ▼
 SecurityConfig         ← Decides: is this endpoint public or protected?
      │
      ▼
 AuthController         ← Receives the request, delegates to service layer
      │
      ▼
 AuthService            ← Business logic: register / login / token generation
      │
      ▼
 UserRepository         ← JPA interface: talks to MySQL via Hibernate
      │
      ▼
 MySQL Database         ← Persists user data
```

---

## 🔧 Configuration Reference (`application.properties`)

```properties
# Server
server.port=8080

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db?...
spring.datasource.username=root
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update    # auto-creates/updates tables
spring.jpa.show-sql=true

# JWT
jwt.secret=myVerySecretKey...           # keep this PRIVATE in production
jwt.expiration=86400000                 # 24 hours in milliseconds
```

---

##  Security Notes

- **Passwords** are hashed with **BCrypt (strength 12)** — never stored in plain text
- **JWT Secret** should be stored in environment variables in production (not in source code)
- **CSRF** is disabled — appropriate for stateless REST APIs using JWT
- **No sessions** — `SessionCreationPolicy.STATELESS` ensures no server-side state
- This project is for **learning purposes** — production use would require refresh tokens, rate limiting, HTTPS, etc.

---

##  Key Concepts (Interview Ready)

| Concept | Explanation |
|---|---|
| **Why JWT over sessions?** | JWT is stateless — server stores no session data, perfect for scalable APIs |
| **Why BCrypt?** | Slow adaptive hashing with auto-salt, designed specifically for passwords |
| **Spring Security Filter Chain** | Every HTTP request passes through a series of filters; `JwtAuthFilter` is added before the default auth filter |
| **`@Repository` interface** | Spring Data JPA auto-implements the interface at runtime — no SQL needed for basic queries |
| **DTOs vs Entities** | DTOs separate the API contract from the DB model, allowing independent evolution |
| **`Optional<T>`** | Avoids `NullPointerException` when a DB lookup may return no result |

---

##  Dependencies

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-web` | Build REST APIs |
| `spring-boot-starter-security` | Security framework + filter chain |
| `spring-boot-starter-data-jpa` | ORM / database abstraction (Hibernate) |
| `mysql-connector-j` | MySQL JDBC driver |
| `jjwt-api/impl/jackson` | JWT creation, signing, parsing |

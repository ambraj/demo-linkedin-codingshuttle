# Users Service

Handles user authentication and management with JWT-based authentication.

## 🎯 Overview

- User registration and login
- JWT token generation
- PostgreSQL database
- Kafka event publishing
- Eureka service registration

## 💻 Stack

Spring Boot 3.5.7, Java 25, PostgreSQL, Spring Data JPA, JWT, Eureka Client, Kafka Producer

## ⚙️ Configuration

**Service:** `users-service`, Port 9010, Context `/users`
**Database:** PostgreSQL (users-db:5432), DDL auto-update
**Eureka:** http://discovery-server:8761/eureka/
**Kafka:** kafka:9092

## 📡 API Endpoints

**Base:** http://localhost:8080/api/v1/users (via Gateway) or http://localhost:9010/users (direct)

### Signup
```bash
POST /auth/signup
Content-Type: application/json
{"email":"user@example.com","password":"pass123","name":"John Doe"}
# Returns: 201 Created with user details
```

### Login
```bash
POST /auth/login
Content-Type: application/json
{"email":"user@example.com","password":"pass123"}
# Returns: 200 OK with JWT token
```

## 🚀 Running

**Docker Compose:** `docker-compose up users-service users-db`
**Maven:** `cd users-service && mvn spring-boot:run`
**Kubernetes:** `kubectl apply -f k8s/users-db.yml -f k8s/users-service.yml`

## 🌍 Environment Variables

| Variable | Default | Required |
|----------|---------|----------|
| DB_SERVICE | users-db | No |
| DB_NAME | users-db | No |
| DB_USER | postgres | No |
| DB_PASSWORD | password | No |
| EUREKA_URI | http://discovery-server:8761/eureka/ | No |

**Health Check:** `curl http://localhost:9010/users/actuator/health`


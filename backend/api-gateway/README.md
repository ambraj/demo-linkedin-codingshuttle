# API Gateway

Single entry point for all microservices with JWT authentication and routing.

## 🎯 Overview

- Request routing to microservices
- JWT authentication and validation
- Load balancing via Eureka
- Spring Cloud Gateway

## 💻 Stack

Spring Boot 3.5.7, Java 25, Spring Cloud Gateway, Eureka Client

## ⚙️ Configuration

**Service:** `api-gateway`, Port 8080
**Eureka:** http://discovery-server:8761/eureka/

## 🛣️ Routes

| Pattern | Target Service | Auth Required |
|---------|---------------|---------------|
| `/api/v1/users/auth/**` | USERS-SERVICE | ❌ |
| `/api/v1/posts/**` | POSTS-SERVICE | ✅ |
| `/api/v1/connections/**` | CONNECTIONS-SERVICE | ✅ |

All routes use `lb://SERVICE-NAME` for load balancing and strip `/api/v1` prefix.

## 🔐 Authentication

Custom filter validates JWT tokens for protected routes. Extracts user ID from token and adds `X-User-Id` header for downstream services.

**Public routes:** signup, login
**Protected routes:** all posts and connections endpoints

## 🚀 Running

**Docker Compose:** `docker-compose up api-gateway discovery-server`
**Maven:** `cd api-gateway && mvn spring-boot:run`
**Kubernetes:** `kubectl apply -f k8s/discovery-server.yml -f k8s/api-gateway.yml`

**Health Check:** `curl http://localhost:8080/actuator/health`
**View Routes:** `curl http://localhost:8080/actuator/gateway/routes`

## 🐛 Troubleshooting

- **503 Service Unavailable:** Check downstream services are registered with Eureka
- **401 Unauthorized:** Verify JWT token in Authorization header, check JWT secret matches Users Service
- **404 Not Found:** Verify route pattern and StripPrefix configuration

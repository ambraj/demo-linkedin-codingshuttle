# API Gateway

The API Gateway serves as the single entry point for all client requests in the LinkedIn clone microservices application. It handles request routing, authentication, and load balancing using Spring Cloud Gateway.

## 📋 Table of Contents
- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Configuration](#configuration)
- [Routes](#routes)
- [Authentication](#authentication)
- [Running the Service](#running-the-service)
- [Environment Variables](#environment-variables)

## 🎯 Overview

The API Gateway handles:
- Single entry point for all microservices
- Request routing to appropriate services
- JWT authentication and validation
- Load balancing across service instances
- Cross-cutting concerns (logging, monitoring)
- Service discovery integration
- Protocol translation if needed

## 💻 Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Gateway**: Spring Cloud Gateway
- **Java Version**: 25
- **Service Discovery**: Netflix Eureka Client
- **Security**: JWT-based authentication
- **Build Tool**: Maven

### Key Dependencies
- `spring-cloud-starter-gateway` - API Gateway functionality
- `spring-cloud-starter-netflix-eureka-client` - Service discovery
- `spring-boot-starter-actuator` - Health checks and monitoring
- `lombok` - Reduce boilerplate code

## ⚙️ Configuration

### Application Properties

**Service Configuration**
- **Application Name**: `api-gateway`
- **Server Port**: `8080`

**Eureka Configuration**
- **Eureka Server**: `http://discovery-server:8761/eureka/`

**Gateway Routes**
- Routes are configured to forward requests to respective microservices
- Automatic service discovery via Eureka
- Load balancing enabled by default

**JWT Configuration**
- **Secret Key**: Shared with Users Service for token validation

## 🛣️ Routes

The API Gateway defines the following routes:

### 1. Users Service Route

**Pattern**: `/api/v1/users/auth/**`

**Target Service**: `users-service`

**Service URI**: `lb://USERS-SERVICE` (load-balanced via Eureka)

**Filters**:
- `StripPrefix=2` - Removes `/api/v1` from the path

**Authentication**: ❌ Not required (public endpoints)

**Forwarded Path**: `/users/auth/**`

**Examples**:
- `POST http://localhost:8080/api/v1/users/auth/signup` → `users-service:9010/users/auth/signup`
- `POST http://localhost:8080/api/v1/users/auth/login` → `users-service:9010/users/auth/login`

### 2. Posts Service Route

**Pattern**: `/api/v1/posts/**`

**Target Service**: `posts-service`

**Service URI**: `lb://POSTS-SERVICE` (load-balanced via Eureka)

**Filters**:
- `StripPrefix=2` - Removes `/api/v1` from the path
- `AuthenticationFilter` - Validates JWT token

**Authentication**: ✅ Required (JWT token)

**Forwarded Path**: `/posts/**`

**Examples**:
- `POST http://localhost:8080/api/v1/posts/core` → `posts-service:9020/posts/core`
- `GET http://localhost:8080/api/v1/posts/core/1` → `posts-service:9020/posts/core/1`
- `POST http://localhost:8080/api/v1/posts/likes/1/like` → `posts-service:9020/posts/likes/1/like`

### 3. Connections Service Route

**Pattern**: `/api/v1/connections/**`

**Target Service**: `connections-service`

**Service URI**: `lb://CONNECTIONS-SERVICE` (load-balanced via Eureka)

**Filters**:
- `StripPrefix=2` - Removes `/api/v1` from the path
- `AuthenticationFilter` - Validates JWT token

**Authentication**: ✅ Required (JWT token)

**Forwarded Path**: `/connections/**`

**Examples**:
- `GET http://localhost:8080/api/v1/connections/core/first-degree` → `connections-service:9030/connections/core/first-degree`
- `POST http://localhost:8080/api/v1/connections/core/request/2` → `connections-service:9030/connections/core/request/2`

## 🔐 Authentication

### Authentication Filter

The API Gateway implements a custom `AuthenticationFilter` that:
1. Extracts JWT token from `Authorization` header
2. Validates token signature using shared secret
3. Extracts user information from token
4. Adds user context to request headers (`X-User-Id`)
5. Forwards authenticated request to downstream service

### Authentication Flow

```
Client Request
    │
    ▼
┌─────────────────────┐
│   API Gateway       │
│  (Port: 8080)       │
└──────────┬──────────┘
           │
           │ 1. Extract JWT from Authorization header
           │ 2. Validate JWT signature
           │ 3. Extract user ID
           │ 4. Add X-User-Id header
           │
           ▼
┌─────────────────────┐
│  Downstream Service │
│  (Posts/Connections)│
└─────────────────────┘
```

### Public vs Protected Routes

**Public Routes** (No authentication required):
- `POST /api/v1/users/auth/signup`
- `POST /api/v1/users/auth/login`

**Protected Routes** (JWT required):
- All `/api/v1/posts/**` endpoints
- All `/api/v1/connections/**` endpoints

### Using JWT Token

1. **Login and get token**:
```bash
TOKEN=$(curl -X POST http://localhost:8080/api/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password"}' \
  -s)
```

2. **Use token in requests**:
```bash
curl -X GET http://localhost:8080/api/v1/posts/core/users/allPosts \
  -H "Authorization: Bearer $TOKEN"
```

## 🚀 Running the Service

### Using Docker Compose (Recommended)

From the root directory:
```bash
docker-compose up api-gateway discovery-server
```

### Using Docker

1. **Build the Docker image**:
   ```bash
   cd api-gateway
   mvn clean package
   docker build -t ambraj/api-gateway .
   ```

2. **Run the container**:
   ```bash
   docker run -d \
     --name api-gateway \
     -p 8080:8080 \
     ambraj/api-gateway
   ```

### Using Maven (Local Development)

1. **Prerequisites**:
   - Eureka server running on `localhost:8761`
   - Downstream services registered with Eureka

2. **Run locally**:
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

3. **Build JAR**:
   ```bash
   mvn clean package
   java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
   ```

### Using Kubernetes

```bash
# Deploy discovery server first
kubectl apply -f k8s/discovery-server.yml

# Deploy API Gateway
kubectl apply -f k8s/api-gateway.yml

# Check status
kubectl get pods -l app=api-gateway
kubectl logs -f deployment/api-gateway
```

## 🌍 Environment Variables

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `EUREKA_URI` | Eureka server URL | `http://discovery-server:8761/eureka/` | Yes |
| `USERS_SERVICE_URI` | Users service URI | `lb://USERS-SERVICE` | No |
| `POSTS_SERVICE_URI` | Posts service URI | `lb://POSTS-SERVICE` | No |
| `CONNECTIONS_SERVICE_URI` | Connections service URI | `lb://CONNECTIONS-SERVICE` | No |
| `JWT_SECRET` | JWT secret key (must match Users Service) | (configured in yml) | Yes |

## 🔍 Health Check

The service exposes actuator endpoints for health monitoring:

**Health Check Endpoint**:
```bash
curl http://localhost:8080/actuator/health
```

**Response**:
```json
{
  "status": "UP",
  "components": {
    "gateway": {
      "status": "UP"
    },
    "eureka": {
      "status": "UP"
    }
  }
}
```

## 📊 Monitoring

### Actuator Endpoints

All actuator endpoints are exposed:
- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Gateway Routes**: `/actuator/gateway/routes`
- **Gateway Filters**: `/actuator/gateway/globalfilters`

### View Configured Routes

```bash
curl http://localhost:8080/actuator/gateway/routes | jq
```

**Response**:
```json
[
  {
    "route_id": "users-service",
    "uri": "lb://USERS-SERVICE",
    "predicates": ["/api/v1/users/auth/**"],
    "filters": ["StripPrefix=2"]
  },
  {
    "route_id": "posts-service",
    "uri": "lb://POSTS-SERVICE",
    "predicates": ["/api/v1/posts/**"],
    "filters": ["StripPrefix=2", "AuthenticationFilter"]
  }
]
```

### Eureka Dashboard

View registered services at: `http://localhost:8761`

## 🔧 Advanced Features

### Load Balancing

The gateway automatically load balances requests across multiple instances:
- Uses round-robin algorithm by default
- Automatically discovers new service instances
- Removes unhealthy instances from rotation

### Request/Response Logging

Enable detailed logging:
```yaml
logging:
  level:
    org.springframework.cloud.gateway: TRACE
```

### Circuit Breaker (Future Enhancement)

Can be added for resilience:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: posts-service
          filters:
            - name: CircuitBreaker
              args:
                name: postsServiceCircuitBreaker
                fallbackUri: forward:/fallback/posts
```

## 🐛 Troubleshooting

### Common Issues

1. **503 Service Unavailable**
   - Check if downstream services are running
   - Verify services are registered with Eureka
   - Check service names match route configurations

2. **401 Unauthorized on Protected Routes**
   - Ensure JWT token is included in Authorization header
   - Verify JWT secret matches between Gateway and Users Service
   - Check token hasn't expired

3. **404 Not Found**
   - Verify route pattern matches request path
   - Check StripPrefix configuration
   - Review downstream service context paths

4. **Gateway Not Registering with Eureka**
   - Check Eureka server is running
   - Verify eureka.client.serviceUrl.defaultZone
   - Check network connectivity

### Debug Mode

Enable debug logging:
```yaml
logging:
  level:
    com.codingshuttle.linkedin: DEBUG
    org.springframework.cloud.gateway: TRACE
    org.springframework.web: DEBUG
```

### Testing Routes

Test each route individually:
```bash
# Test public route
curl -X POST http://localhost:8080/api/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'

# Test protected route
curl -X GET http://localhost:8080/api/v1/posts/core/1 \
  -H "Authorization: Bearer <token>"
```

## 📝 Development Notes

### Adding New Routes

1. Update `application.yml` with new route configuration
2. Add appropriate predicates and filters
3. Register the target service with Eureka
4. Update this README

Example:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: new-service
          uri: lb://NEW-SERVICE
          predicates:
            - Path=/api/v1/new-service/**
          filters:
            - StripPrefix=2
            - name: AuthenticationFilter
```

### Custom Filters

Create custom gateway filters:
1. Implement `GatewayFilter` or `GlobalFilter`
2. Register as Spring Bean
3. Add to route configuration

### Route Configuration Best Practices

- Use service discovery URIs (`lb://SERVICE-NAME`)
- Apply `StripPrefix` to normalize paths
- Order routes from most specific to least specific
- Use authentication filter on protected routes
- Enable actuator endpoints for monitoring

## 🔗 Related Services

- **Discovery Server**: Eureka server for service registration
- **Users Service**: Provides authentication endpoints
- **Posts Service**: Downstream service for posts
- **Connections Service**: Downstream service for connections
- **Notification Service**: Not directly accessed via gateway

## 📚 Additional Resources

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Gateway Filters](https://cloud.spring.io/spring-cloud-gateway/reference/html/#gatewayfilter-factories)
- [Route Predicates](https://cloud.spring.io/spring-cloud-gateway/reference/html/#gateway-request-predicates-factories)
- [Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)

---

For questions or issues, please refer to the main project README or create an issue in the repository.


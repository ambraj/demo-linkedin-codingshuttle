# Users Service

The Users Service is responsible for user authentication and management in the LinkedIn clone application. It provides user registration, login functionality, and JWT-based authentication.

## 📋 Table of Contents
- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Running the Service](#running-the-service)
- [Environment Variables](#environment-variables)

## 🎯 Overview

The Users Service handles:
- User registration (signup)
- User authentication (login)
- JWT token generation
- User profile management
- Integration with Eureka for service discovery
- Publishing user-related events to Kafka

## 💻 Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: 25
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: JWT (JSON Web Tokens)
- **Service Discovery**: Netflix Eureka Client
- **Message Broker**: Apache Kafka (Producer)
- **Build Tool**: Maven

### Key Dependencies
- `spring-boot-starter-web` - RESTful API
- `spring-boot-starter-data-jpa` - Database operations
- `spring-boot-starter-actuator` - Health checks and monitoring
- `spring-cloud-starter-netflix-eureka-client` - Service registration
- `spring-kafka` - Event publishing
- `postgresql` - PostgreSQL driver
- `lombok` - Reduce boilerplate code

## ⚙️ Configuration

### Application Properties

**Service Configuration**
- **Application Name**: `users-service`
- **Server Port**: `9010`
- **Context Path**: `/users`

**Database Configuration**
- **Host**: Configurable via `DB_SERVICE` (default: `users-db`)
- **Port**: `5432`
- **Database Name**: Configurable via `DB_NAME` (default: `users-db`)
- **Username**: Configurable via `DB_USER` (default: `postgres`)
- **Password**: Configurable via `DB_PASSWORD` (default: `password`)
- **DDL Auto**: `update`

**Eureka Configuration**
- **Eureka Server**: `http://discovery-server:8761/eureka/`

**Kafka Configuration**
- **Bootstrap Servers**: `kafka:9092`
- **Key Serializer**: `LongSerializer`
- **Value Serializer**: `JsonSerializer`

**JWT Configuration**
- **Secret Key**: Configured in application.yml (should be externalized in production)

## 🗄️ Database Schema

The service uses PostgreSQL with the following main entities:

### User Entity
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## 📡 API Endpoints

### Base URL
- **Via API Gateway**: `http://localhost:8080/api/v1/users`
- **Direct Access**: `http://localhost:9010/users`

### 1. User Signup

Register a new user account.

**Endpoint**: `POST /auth/signup`

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123!",
  "name": "John Doe"
}
```

**Success Response** (201 Created):
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "name": "John Doe",
  "createdAt": "2025-10-27T10:30:00"
}
```

**Error Responses**:
- `400 Bad Request`: Invalid input data
- `409 Conflict`: Email already exists

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/users/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePassword123!",
    "name": "John Doe"
  }'
```

### 2. User Login

Authenticate user and receive JWT token.

**Endpoint**: `POST /auth/login`

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123!"
}
```

**Success Response** (200 OK):
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**Error Responses**:
- `400 Bad Request`: Invalid input data
- `401 Unauthorized`: Invalid credentials

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePassword123!"
  }'
```

**Using JWT Token**:
After receiving the JWT token, include it in subsequent requests:
```bash
curl -X GET http://localhost:8080/api/v1/posts/core \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 🚀 Running the Service

### Using Docker Compose (Recommended)

From the root directory:
```bash
docker-compose up users-service users-db
```

### Using Docker

1. **Build the Docker image**:
   ```bash
   cd users-service
   mvn clean package
   docker build -t ambraj/users-service .
   ```

2. **Run the container**:
   ```bash
   docker run -d \
     --name users-service \
     -p 9010:8080 \
     -e DB_SERVICE=users-db \
     -e DB_USERNAME=postgres \
     -e DB_PASSWORD=password \
     -e DB_NAME=users-db \
     ambraj/users-service
   ```

### Using Maven (Local Development)

1. **Prerequisites**:
   - PostgreSQL running on `localhost:5432`
   - Database `users-db` created
   - Eureka server running (optional for local dev)
   - Kafka running (optional for local dev)

2. **Run locally**:
   ```bash
   cd users-service
   mvn spring-boot:run
   ```

3. **Build JAR**:
   ```bash
   mvn clean package
   java -jar target/users-service-0.0.1-SNAPSHOT.jar
   ```

### Using Kubernetes

```bash
# Deploy database
kubectl apply -f k8s/users-db.yml

# Deploy service
kubectl apply -f k8s/users-service.yml

# Check status
kubectl get pods -l app=users-service
kubectl logs -f deployment/users-service
```

## 🌍 Environment Variables

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `DB_SERVICE` | Database host | `users-db` | No |
| `DB_NAME` | Database name | `users-db` | No |
| `DB_USER` | Database username | `postgres` | No |
| `DB_PASSWORD` | Database password | `password` | No |
| `EUREKA_URI` | Eureka server URL | `http://discovery-server:8761/eureka/` | No |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `kafka:9092` | No |

## 🔍 Health Check

The service exposes actuator endpoints for health monitoring:

**Health Check Endpoint**:
```bash
curl http://localhost:9010/users/actuator/health
```

**Response**:
```json
{
  "status": "UP"
}
```

## 📊 Monitoring

### Actuator Endpoints

- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`

### Eureka Dashboard

View service registration status at: `http://localhost:8761`

## 🔐 Security Considerations

### JWT Token
- Tokens are signed using HS256 algorithm
- Token contains user ID and email
- Tokens should be stored securely on client side
- Token validation is performed by API Gateway

### Password Security
- Passwords should be hashed before storage (implementation recommended)
- Use strong password validation rules
- Implement rate limiting for login attempts (recommended)

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify PostgreSQL is running
   - Check database credentials
   - Ensure database `users-db` exists

2. **Service Not Registering with Eureka**
   - Check Eureka server is running
   - Verify network connectivity
   - Check `eureka.client.serviceUrl.defaultZone` configuration

3. **JWT Token Invalid**
   - Ensure JWT secret matches between Users Service and API Gateway
   - Check token expiration time

### Debug Mode

Enable debug logging:
```bash
export LOGGING_LEVEL_ROOT=DEBUG
mvn spring-boot:run
```

Or add to `application.yml`:
```yaml
logging:
  level:
    com.codingshuttle.linkedin: DEBUG
```

## 📝 Development Notes

### Adding New Endpoints

1. Create DTO classes in `dto` package
2. Add service methods in `service` package
3. Create controller methods in `controller` package
4. Update this README with new endpoint documentation

### Database Migrations

For production, consider using:
- Flyway or Liquibase for database version control
- Change `ddl-auto` from `update` to `validate`

## 🔗 Related Services

- **API Gateway**: Routes requests to this service
- **Discovery Server**: Service registration
- **Posts Service**: Uses user authentication
- **Connections Service**: Uses user authentication
- **Notification Service**: Consumes user events from Kafka

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [JWT.io](https://jwt.io/) - JWT debugger
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

For questions or issues, please refer to the main project README or create an issue in the repository.


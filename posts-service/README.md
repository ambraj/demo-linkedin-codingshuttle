# Posts Service

The Posts Service manages all post-related functionality in the LinkedIn clone application, including creating posts, retrieving posts, and handling post likes/unlikes. It integrates with Kafka to publish post-related events.

## 📋 Table of Contents
- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Running the Service](#running-the-service)
- [Environment Variables](#environment-variables)

## 🎯 Overview

The Posts Service handles:
- Creating new posts
- Retrieving posts by ID
- Getting all posts for a user
- Liking posts
- Unliking posts
- Publishing post events to Kafka
- Integration with Eureka for service discovery

## 💻 Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: 25
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Service Discovery**: Netflix Eureka Client
- **Message Broker**: Apache Kafka (Producer)
- **AOP**: Spring AOP for cross-cutting concerns
- **Build Tool**: Maven

### Key Dependencies
- `spring-boot-starter-web` - RESTful API
- `spring-boot-starter-data-jpa` - Database operations
- `spring-boot-starter-actuator` - Health checks and monitoring
- `spring-boot-starter-aop` - Aspect-oriented programming
- `spring-cloud-starter-netflix-eureka-client` - Service registration
- `spring-kafka` - Event publishing
- `postgresql` - PostgreSQL driver
- `lombok` - Reduce boilerplate code

## ⚙️ Configuration

### Application Properties

**Service Configuration**
- **Application Name**: `posts-service`
- **Server Port**: `9020`
- **Context Path**: `/posts`

**Database Configuration**
- **Host**: Configurable via `DB_SERVICE` (default: `posts-db`)
- **Port**: `5432`
- **Database Name**: Configurable via `DB_NAME` (default: `posts-db`)
- **Username**: Configurable via `DB_USERNAME` (default: `postgres`)
- **Password**: Configurable via `DB_PASSWORD` (default: `password`)
- **DDL Auto**: `update`

**Eureka Configuration**
- **Eureka Server**: `http://discovery-server:8761/eureka/`

**Kafka Configuration**
- **Bootstrap Servers**: `kafka:9092`
- **Key Serializer**: `LongSerializer`
- **Value Serializer**: `JsonSerializer`

**AOP Configuration**
- **Auto**: `true`

## 🗄️ Database Schema

The service uses PostgreSQL with the following main entities:

### Post Entity
```sql
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Post Like Entity
```sql
CREATE TABLE post_likes (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT unique_post_user UNIQUE (post_id, user_id),
    CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts(id)
);
```

## 📡 API Endpoints

### Base URL
- **Via API Gateway**: `http://localhost:8080/api/v1/posts`
- **Direct Access**: `http://localhost:9020/posts`

**Authentication**: All endpoints require JWT token (except when accessed directly)

### 1. Create Post

Create a new post for the authenticated user.

**Endpoint**: `POST /core`

**Request Headers**:
```
Content-Type: application/json
Authorization: Bearer <jwt-token>
```

**Request Body**:
```json
{
  "content": "This is my first post on LinkedIn clone!"
}
```

**Success Response** (201 Created):
```json
{
  "id": 1,
  "content": "This is my first post on LinkedIn clone!",
  "userId": 1,
  "createdAt": "2025-10-27T10:30:00"
}
```

**Error Responses**:
- `400 Bad Request`: Invalid input data
- `401 Unauthorized`: Missing or invalid JWT token

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/posts/core \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "content": "This is my first post on LinkedIn clone!"
  }'
```

### 2. Get Post by ID

Retrieve a specific post by its ID.

**Endpoint**: `GET /core/{postId}`

**Request Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `postId` (Long): The ID of the post to retrieve

**Success Response** (200 OK):
```json
{
  "id": 1,
  "content": "This is my first post on LinkedIn clone!",
  "userId": 1,
  "createdAt": "2025-10-27T10:30:00"
}
```

**Error Responses**:
- `404 Not Found`: Post not found
- `401 Unauthorized`: Missing or invalid JWT token

**Example cURL**:
```bash
curl -X GET http://localhost:8080/api/v1/posts/core/1 \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. Get All Posts for Current User

Retrieve all posts created by the authenticated user.

**Endpoint**: `GET /core/users/allPosts`

**Request Headers**:
```
Authorization: Bearer <jwt-token>
```

**Success Response** (200 OK):
```json
[
  {
    "id": 1,
    "content": "This is my first post!",
    "userId": 1,
    "createdAt": "2025-10-27T10:30:00"
  },
  {
    "id": 2,
    "content": "Another post from me!",
    "userId": 1,
    "createdAt": "2025-10-27T11:00:00"
  }
]
```

**Error Responses**:
- `401 Unauthorized`: Missing or invalid JWT token

**Example cURL**:
```bash
curl -X GET http://localhost:8080/api/v1/posts/core/users/allPosts \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 4. Like a Post

Like a specific post.

**Endpoint**: `POST /likes/{postId}/like`

**Request Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `postId` (Long): The ID of the post to like

**Success Response** (204 No Content):
```
(Empty response body)
```

**Error Responses**:
- `404 Not Found`: Post not found
- `409 Conflict`: Post already liked
- `401 Unauthorized`: Missing or invalid JWT token

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/posts/likes/1/like \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 5. Unlike a Post

Remove a like from a specific post.

**Endpoint**: `DELETE /likes/{postId}/unlike`

**Request Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `postId` (Long): The ID of the post to unlike

**Success Response** (204 No Content):
```
(Empty response body)
```

**Error Responses**:
- `404 Not Found`: Post not found or like doesn't exist
- `401 Unauthorized`: Missing or invalid JWT token

**Example cURL**:
```bash
curl -X DELETE http://localhost:8080/api/v1/posts/likes/1/unlike \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 🚀 Running the Service

### Using Docker Compose (Recommended)

From the root directory:
```bash
docker-compose up posts-service posts-db kafka
```

### Using Docker

1. **Build the Docker image**:
   ```bash
   cd posts-service
   mvn clean package
   docker build -t ambraj/posts-service .
   ```

2. **Run the container**:
   ```bash
   docker run -d \
     --name posts-service \
     -p 9020:8080 \
     -e DB_SERVICE=posts-db \
     -e DB_USERNAME=postgres \
     -e DB_PASSWORD=password \
     -e DB_NAME=posts-db \
     ambraj/posts-service
   ```

### Using Maven (Local Development)

1. **Prerequisites**:
   - PostgreSQL running on `localhost:5433`
   - Database `posts-db` created
   - Eureka server running (optional for local dev)
   - Kafka running (optional for local dev)

2. **Create the database**:
   ```bash
   docker run --name posts-db \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=password \
     -e POSTGRES_DB=posts-db \
     -p 5433:5432 \
     -d postgres
   ```

3. **Run locally**:
   ```bash
   cd posts-service
   mvn spring-boot:run
   ```

4. **Build JAR**:
   ```bash
   mvn clean package
   java -jar target/posts-service-0.0.1-SNAPSHOT.jar
   ```

### Using Kubernetes

```bash
# Deploy database
kubectl apply -f k8s/posts-db.yml

# Deploy service
kubectl apply -f k8s/posts-service.yml

# Check status
kubectl get pods -l app=posts-service
kubectl logs -f deployment/posts-service
```

## 🌍 Environment Variables

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `DB_SERVICE` | Database host | `posts-db` | No |
| `DB_NAME` | Database name | `posts-db` | No |
| `DB_USERNAME` | Database username | `postgres` | No |
| `DB_PASSWORD` | Database password | `password` | No |
| `EUREKA_URI` | Eureka server URL | `http://discovery-server:8761/eureka/` | No |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `kafka:9092` | No |

## 🔍 Health Check

The service exposes actuator endpoints for health monitoring:

**Health Check Endpoint**:
```bash
curl http://localhost:9020/posts/actuator/health
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

## 🎯 Event Publishing

The Posts Service publishes events to Kafka topics:

### Post Created Event
When a new post is created, an event is published to notify other services.

**Topic**: `post-created-topic`

**Event Payload**:
```json
{
  "postId": 1,
  "userId": 1,
  "content": "This is my first post!",
  "timestamp": "2025-10-27T10:30:00"
}
```

### Post Liked Event
When a post is liked, an event is published.

**Topic**: `post-liked-topic`

**Event Payload**:
```json
{
  "postId": 1,
  "userId": 1,
  "timestamp": "2025-10-27T10:30:00"
}
```

## 🔒 Security

### Authentication
- User authentication is handled by the API Gateway
- User ID is extracted from JWT token via `UserContextHolder`
- Direct service access bypasses authentication (not recommended for production)

### Authorization
- Users can only create posts for themselves
- Users can view any post
- Users can like/unlike any post

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify PostgreSQL is running on port 5433
   - Check database credentials
   - Ensure database `posts-db` exists

2. **Service Not Registering with Eureka**
   - Check Eureka server is running
   - Verify network connectivity
   - Check `eureka.client.serviceUrl.defaultZone` configuration

3. **Kafka Connection Failed**
   - Ensure Kafka is running
   - Check bootstrap servers configuration
   - Verify network connectivity to Kafka

4. **User ID Not Found (401 Unauthorized)**
   - Ensure request is coming through API Gateway
   - Verify JWT token is valid
   - Check `UserContextHolder` implementation

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
    org.springframework.web: DEBUG
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

### AOP Features

The service uses Spring AOP for:
- Logging (method entry/exit)
- Performance monitoring
- Exception handling

## 🔗 Related Services

- **API Gateway**: Routes requests to this service and handles authentication
- **Discovery Server**: Service registration
- **Users Service**: Provides user authentication
- **Notification Service**: Consumes post events from Kafka
- **Connections Service**: May use post data for feed generation

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring AOP](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop)
- [Apache Kafka](https://kafka.apache.org/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

For questions or issues, please refer to the main project README or create an issue in the repository.


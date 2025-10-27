# Notification Service

The Notification Service is an event-driven microservice that consumes events from Apache Kafka and manages notifications in the LinkedIn clone application. It listens to various events from other services and stores notifications in PostgreSQL.

## 📋 Table of Contents
- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [Event Consumers](#event-consumers)
- [Running the Service](#running-the-service)
- [Environment Variables](#environment-variables)

## 🎯 Overview

The Notification Service handles:
- Consuming events from Kafka topics
- Creating and storing notifications
- Event-driven architecture for real-time notifications
- Integration with Eureka for service discovery
- Asynchronous processing of notification events

## 💻 Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Java Version**: 25
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Service Discovery**: Netflix Eureka Client
- **Message Broker**: Apache Kafka (Consumer)
- **Build Tool**: Maven

### Key Dependencies
- `spring-boot-starter-web` - RESTful API (minimal usage)
- `spring-boot-starter-data-jpa` - Database operations
- `spring-boot-starter-actuator` - Health checks and monitoring
- `spring-cloud-starter-netflix-eureka-client` - Service registration
- `spring-kafka` - Event consumption
- `postgresql` - PostgreSQL driver
- `lombok` - Reduce boilerplate code

## ⚙️ Configuration

### Application Properties

**Service Configuration**
- **Application Name**: `notification-service`
- **Server Port**: `9040`
- **Context Path**: None (default `/`)

**Database Configuration**
- **Host**: Configurable via `DB_SERVICE` (default: `notification-db`)
- **Port**: `5432`
- **Database Name**: Configurable via `DB_NAME` (default: `notification-db`)
- **Username**: Configurable via `DB_USER` (default: `postgres`)
- **Password**: Configurable via `DB_PASSWORD` (default: `password`)
- **DDL Auto**: `update`

**Eureka Configuration**
- **Eureka Server**: `http://discovery-server:8761/eureka/`

**Kafka Consumer Configuration**
- **Bootstrap Servers**: `kafka:9092`
- **Group ID**: `notification-service`
- **Auto Offset Reset**: `earliest`
- **Key Deserializer**: `LongDeserializer`
- **Value Deserializer**: `JsonDeserializer`
- **Trusted Packages**: `com.codingshuttle.linkedin.*`

## 🗄️ Database Schema

The service uses PostgreSQL with the following main entities:

### Notification Entity
```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    related_entity_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

CREATE INDEX idx_user_id ON notifications(user_id);
CREATE INDEX idx_created_at ON notifications(created_at);
CREATE INDEX idx_is_read ON notifications(is_read);
```

### Notification Types
- `POST_CREATED` - When someone you're connected with creates a post
- `POST_LIKED` - When someone likes your post
- `CONNECTION_REQUEST` - When someone sends you a connection request
- `CONNECTION_ACCEPTED` - When someone accepts your connection request
- `COMMENT_ADDED` - When someone comments on your post
- `MENTION` - When someone mentions you in a post

## 📡 Event Consumers

The service listens to multiple Kafka topics and processes events asynchronously.

### 1. Post Created Event

**Topic**: `post-created-topic`

**Event Structure**:
```json
{
  "postId": 1,
  "userId": 1,
  "content": "This is my first post!",
  "timestamp": "2025-10-27T10:30:00"
}
```

**Processing Logic**:
- Fetches all connections of the post creator
- Creates notifications for all connected users
- Stores notification in database

**Generated Notification**:
```json
{
  "userId": 2,
  "type": "POST_CREATED",
  "message": "John Doe created a new post",
  "relatedEntityId": 1,
  "isRead": false,
  "createdAt": "2025-10-27T10:30:00"
}
```

### 2. Post Liked Event

**Topic**: `post-liked-topic`

**Event Structure**:
```json
{
  "postId": 1,
  "userId": 2,
  "likedByUserId": 3,
  "timestamp": "2025-10-27T10:35:00"
}
```

**Processing Logic**:
- Creates notification for the post owner
- Only if the liker is different from the post owner

**Generated Notification**:
```json
{
  "userId": 2,
  "type": "POST_LIKED",
  "message": "Jane Smith liked your post",
  "relatedEntityId": 1,
  "isRead": false,
  "createdAt": "2025-10-27T10:35:00"
}
```

### 3. Connection Request Event

**Topic**: `connection-request-sent-topic`

**Event Structure**:
```json
{
  "fromUserId": 1,
  "toUserId": 2,
  "timestamp": "2025-10-27T10:40:00"
}
```

**Processing Logic**:
- Creates notification for the target user

**Generated Notification**:
```json
{
  "userId": 2,
  "type": "CONNECTION_REQUEST",
  "message": "John Doe sent you a connection request",
  "relatedEntityId": 1,
  "isRead": false,
  "createdAt": "2025-10-27T10:40:00"
}
```

### 4. Connection Accepted Event

**Topic**: `connection-accepted-topic`

**Event Structure**:
```json
{
  "userId1": 1,
  "userId2": 2,
  "timestamp": "2025-10-27T10:45:00"
}
```

**Processing Logic**:
- Creates notification for the user who sent the request

**Generated Notification**:
```json
{
  "userId": 1,
  "type": "CONNECTION_ACCEPTED",
  "message": "Jane Smith accepted your connection request",
  "relatedEntityId": 2,
  "isRead": false,
  "createdAt": "2025-10-27T10:45:00"
}
```

## 🚀 Running the Service

### Using Docker Compose (Recommended)

From the root directory:
```bash
docker-compose up notification-service notification-db kafka
```

### Using Docker

1. **Build the Docker image**:
   ```bash
   cd notification-service
   mvn clean package
   docker build -t ambraj/notification-service .
   ```

2. **Run the container**:
   ```bash
   docker run -d \
     --name notification-service \
     -p 9040:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://notification-db:5432/notification-db \
     -e SPRING_DATASOURCE_USERNAME=postgres \
     -e SPRING_DATASOURCE_PASSWORD=password \
     -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
     ambraj/notification-service
   ```

### Using Maven (Local Development)

1. **Prerequisites**:
   - PostgreSQL running on `localhost:5434`
   - Database `notification-db` created
   - Eureka server running (optional for local dev)
   - Kafka running (required for event consumption)

2. **Create the database**:
   ```bash
   docker run --name notification-db \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=password \
     -e POSTGRES_DB=notification-db \
     -p 5434:5432 \
     -d postgres
   ```

3. **Run locally**:
   ```bash
   cd notification-service
   mvn spring-boot:run
   ```

4. **Build JAR**:
   ```bash
   mvn clean package
   java -jar target/notification-service-0.0.1-SNAPSHOT.jar
   ```

### Using Kubernetes

```bash
# Deploy database
kubectl apply -f k8s/notification-db.yml

# Deploy service
kubectl apply -f k8s/notification-service.yml

# Check status
kubectl get pods -l app=notification-service
kubectl logs -f deployment/notification-service
```

## 🌍 Environment Variables

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://notification-db:5432/notification-db` | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `password` | Yes |
| `EUREKA_URI` | Eureka server URL | `http://discovery-server:8761/eureka/` | No |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `kafka:9092` | Yes |

## 🔍 Health Check

The service exposes actuator endpoints for health monitoring:

**Health Check Endpoint**:
```bash
curl http://localhost:9040/actuator/health
```

**Response**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "kafka": {
      "status": "UP"
    }
  }
}
```

## 📊 Monitoring

### Actuator Endpoints

- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`

### Eureka Dashboard

View service registration status at: `http://localhost:8761`

### Kafka Consumer Metrics

Monitor consumer lag and processing:
```bash
# Via Kafka UI
http://localhost:8090

# Via actuator metrics
curl http://localhost:9040/actuator/metrics/kafka.consumer.fetch.manager.records.consumed.total
```

## 📝 Event Processing Flow

```
┌─────────────────┐
│  Other Services │
│ (Posts, etc.)   │
└────────┬────────┘
         │
         │ Publish Event
         ▼
┌─────────────────┐
│  Kafka Topics   │
└────────┬────────┘
         │
         │ Subscribe
         ▼
┌─────────────────┐
│  Notification   │
│   Service       │
│  (Consumer)     │
└────────┬────────┘
         │
         │ Process & Store
         ▼
┌─────────────────┐
│   PostgreSQL    │
│ (notifications) │
└─────────────────┘
```

## 🔒 Security

### Event Processing
- Events are consumed from trusted topics only
- JSON deserialization is restricted to trusted packages
- Prevents deserialization attacks

### Database Security
- Use strong passwords in production
- Enable SSL for database connections
- Implement proper access controls

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify PostgreSQL is running on port 5434
   - Check database credentials
   - Ensure database `notification-db` exists

2. **Kafka Consumer Not Receiving Events**
   - Check Kafka is running and accessible
   - Verify topic names match event publishers
   - Check consumer group ID
   - Review trusted packages configuration

3. **Deserialization Errors**
   - Ensure event payload structure matches expected format
   - Verify trusted packages includes event class packages
   - Check JSON serializer/deserializer configuration

4. **Consumer Lag**
   - Monitor consumer lag in Kafka UI
   - Scale the service horizontally if needed
   - Optimize event processing logic

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
    org.springframework.kafka: DEBUG
    org.apache.kafka: DEBUG
```

### Testing Event Consumption

Send test events using Kafka console producer:
```bash
# Access Kafka container
docker exec -it kafka bash

# Send test event
kafka-console-producer --broker-list localhost:9092 --topic post-created-topic
# Then paste JSON payload
```

Or use Kafka UI at `http://localhost:8090`

## 📝 Development Notes

### Adding New Event Consumers

1. Create event DTO in `dto` package
2. Create consumer method with `@KafkaListener` annotation
3. Add processing logic in service layer
4. Update this README with new event documentation

Example:
```java
@KafkaListener(topics = "new-event-topic", groupId = "notification-service")
public void handleNewEvent(NewEvent event) {
    // Process event and create notification
}
```

### Consumer Configuration

**Key Points**:
- Use appropriate group ID for consumer groups
- Set `auto-offset-reset` to `earliest` for not missing events
- Configure proper error handling
- Implement idempotent processing

### Database Optimization

For better performance:
- Create indexes on frequently queried columns
- Implement pagination for notification retrieval
- Archive old notifications periodically
- Use database partitioning for large datasets

## 🔗 Related Services

- **Posts Service**: Publishes post-related events
- **Connections Service**: Publishes connection events
- **Users Service**: Provides user data
- **Discovery Server**: Service registration
- **Kafka**: Message broker for event streaming

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Kafka](https://spring.io/projects/spring-kafka)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Event-Driven Architecture](https://martinfowler.com/articles/201701-event-driven.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## 🚀 Future Enhancements

Potential improvements:
- WebSocket support for real-time notification delivery
- Email/SMS notifications via external services
- Notification preferences and filtering
- Batch notification processing
- Push notification integration
- Notification expiry and cleanup

---

For questions or issues, please refer to the main project README or create an issue in the repository.


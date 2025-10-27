# Connections Service

The Connections Service manages user connections and networking functionality in the LinkedIn clone application using Neo4j graph database. It handles connection requests, acceptance/rejection, and maintains the user network graph.

## 📋 Table of Contents
- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Running the Service](#running-the-service)
- [Environment Variables](#environment-variables)

## 🎯 Overview

The Connections Service handles:
- Sending connection requests
- Accepting connection requests
- Rejecting connection requests
- Getting first-degree connections
- Managing user network graph
- Publishing connection events to Kafka
- Integration with Eureka for service discovery

## 💻 Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: 25
- **Database**: Neo4j (Graph Database)
- **OGM**: Spring Data Neo4j
- **Service Discovery**: Netflix Eureka Client
- **Message Broker**: Apache Kafka (Producer)
- **Build Tool**: Maven

### Key Dependencies
- `spring-boot-starter-web` - RESTful API
- `spring-boot-starter-data-neo4j` - Graph database operations
- `spring-boot-starter-actuator` - Health checks and monitoring
- `spring-cloud-starter-netflix-eureka-client` - Service registration
- `spring-kafka` - Event publishing
- `neo4j-java-driver` - Neo4j connectivity
- `lombok` - Reduce boilerplate code

## ⚙️ Configuration

### Application Properties

**Service Configuration**
- **Application Name**: `connections-service`
- **Server Port**: `9030`
- **Context Path**: `/connections`

**Neo4j Configuration**
- **URI**: `bolt://neo4j:7687`
- **Username**: `neo4j`
- **Password**: `password`

**Eureka Configuration**
- **Eureka Server**: `http://discovery-server:8761/eureka/`

**Kafka Configuration**
- **Bootstrap Servers**: `kafka:9092`
- **Key Serializer**: `LongSerializer`
- **Value Serializer**: `JsonSerializer`

## 🗄️ Database Schema

The service uses Neo4j graph database with the following structure:

### Person Node
```cypher
(:Person {
  id: Long,
  name: String,
  email: String
})
```

### Connection Relationships

**CONNECTED_TO** - Bidirectional relationship between connected users
```cypher
(:Person)-[:CONNECTED_TO]->(:Person)
```

**CONNECTION_REQUESTED** - Pending connection request
```cypher
(:Person)-[:CONNECTION_REQUESTED]->(:Person)
```

### Graph Structure Example
```
(Person:1)-[:CONNECTED_TO]->(Person:2)
(Person:1)-[:CONNECTION_REQUESTED]->(Person:3)
(Person:2)-[:CONNECTED_TO]->(Person:1)
```

### Degrees of Connection
- **1st Degree**: Direct connections (CONNECTED_TO relationship)
- **2nd Degree**: Connections of connections
- **3rd Degree**: Connections of 2nd-degree connections

## 📡 API Endpoints

### Base URL
- **Via API Gateway**: `http://localhost:8080/api/v1/connections`
- **Direct Access**: `http://localhost:9030/connections`

**Authentication**: All endpoints require JWT token (except when accessed directly)

### 1. Get First-Degree Connections

Retrieve all direct connections of the authenticated user.

**Endpoint**: `GET /core/first-degree`

**Request Headers**:
```
Authorization: Bearer <jwt-token>
X-User-Id: 1
```

**Success Response** (200 OK):
```json
[
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane.smith@example.com"
  },
  {
    "id": 3,
    "name": "Bob Johnson",
    "email": "bob.johnson@example.com"
  }
]
```

**Error Responses**:
- `401 Unauthorized`: Missing or invalid JWT token
- `404 Not Found`: User not found

**Example cURL**:
```bash
curl -X GET http://localhost:8080/api/v1/connections/core/first-degree \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "X-User-Id: 1"
```

### 2. Send Connection Request

Send a connection request to another user.

**Endpoint**: `POST /core/request/{userId}`

**Request Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `userId` (Long): The ID of the user to send connection request to

**Success Response** (200 OK):
```json
true
```

**Error Responses**:
- `400 Bad Request`: Invalid user ID or already connected
- `404 Not Found`: Target user not found
- `401 Unauthorized`: Missing or invalid JWT token
- `409 Conflict`: Connection request already sent

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/connections/core/request/2 \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. Accept Connection Request

Accept a pending connection request from another user.

**Endpoint**: `POST /core/accept/{userId}`

**Request Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `userId` (Long): The ID of the user who sent the connection request

**Success Response** (200 OK):
```json
true
```

**Error Responses**:
- `400 Bad Request`: No pending request from this user
- `404 Not Found`: User or request not found
- `401 Unauthorized`: Missing or invalid JWT token

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/connections/core/accept/2 \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Note**: After acceptance:
- The `CONNECTION_REQUESTED` relationship is removed
- A bidirectional `CONNECTED_TO` relationship is created
- Both users become first-degree connections

### 4. Reject Connection Request

Reject a pending connection request from another user.

**Endpoint**: `POST /core/reject/{userId}`

**Request Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `userId` (Long): The ID of the user who sent the connection request

**Success Response** (200 OK):
```json
true
```

**Error Responses**:
- `400 Bad Request`: No pending request from this user
- `404 Not Found`: User or request not found
- `401 Unauthorized`: Missing or invalid JWT token

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/connections/core/reject/2 \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Note**: After rejection, the `CONNECTION_REQUESTED` relationship is simply removed.

## 🚀 Running the Service

### Using Docker Compose (Recommended)

From the root directory:
```bash
docker-compose up connections-service connections-db kafka
```

### Using Docker

1. **Build the Docker image**:
   ```bash
   cd connections-service
   mvn clean package
   docker build -t ambraj/connections-service .
   ```

2. **Run the container**:
   ```bash
   docker run -d \
     --name connections-service \
     -p 9030:8080 \
     -e SPRING_NEO4J_URI=bolt://connections-db:7687 \
     -e SPRING_NEO4J_AUTHENTICATION_USERNAME=neo4j \
     -e SPRING_NEO4J_AUTHENTICATION_PASSWORD=password \
     ambraj/connections-service
   ```

### Using Maven (Local Development)

1. **Prerequisites**:
   - Neo4j running on `localhost:7687`
   - Neo4j credentials: `neo4j/password`
   - Eureka server running (optional for local dev)
   - Kafka running (optional for local dev)

2. **Start Neo4j**:
   ```bash
   docker run --name connections-db \
     -p 7474:7474 -p 7687:7687 \
     -e NEO4J_AUTH=neo4j/password \
     -d neo4j:latest
   ```

3. **Access Neo4j Browser** (optional):
   - URL: `http://localhost:7474`
   - Username: `neo4j`
   - Password: `password`

4. **Run locally**:
   ```bash
   cd connections-service
   mvn spring-boot:run
   ```

5. **Build JAR**:
   ```bash
   mvn clean package
   java -jar target/connections-service-0.0.1-SNAPSHOT.jar
   ```

### Using Kubernetes

```bash
# Deploy Neo4j database
kubectl apply -f k8s/connections-db.yml

# Deploy service
kubectl apply -f k8s/connections-service.yml

# Check status
kubectl get pods -l app=connections-service
kubectl logs -f deployment/connections-service
```

## 🌍 Environment Variables

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `SPRING_NEO4J_URI` | Neo4j Bolt URI | `bolt://neo4j:7687` | Yes |
| `SPRING_NEO4J_AUTHENTICATION_USERNAME` | Neo4j username | `neo4j` | Yes |
| `SPRING_NEO4J_AUTHENTICATION_PASSWORD` | Neo4j password | `password` | Yes |
| `EUREKA_URI` | Eureka server URL | `http://discovery-server:8761/eureka/` | No |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `kafka:9092` | No |

## 🔍 Health Check

The service exposes actuator endpoints for health monitoring:

**Health Check Endpoint**:
```bash
curl http://localhost:9030/connections/actuator/health
```

**Response**:
```json
{
  "status": "UP",
  "components": {
    "neo4j": {
      "status": "UP",
      "details": {
        "server": "5.x.x@localhost:7687"
      }
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

### Neo4j Browser

Access Neo4j Browser for database visualization:
- **URL**: `http://localhost:7474`
- **Credentials**: `neo4j/password`

**Useful Cypher Queries**:

View all persons:
```cypher
MATCH (p:Person) RETURN p
```

View all connections:
```cypher
MATCH (p1:Person)-[r:CONNECTED_TO]->(p2:Person) 
RETURN p1, r, p2
```

View all pending requests:
```cypher
MATCH (p1:Person)-[r:CONNECTION_REQUESTED]->(p2:Person) 
RETURN p1, r, p2
```

## 🎯 Event Publishing

The Connections Service publishes events to Kafka topics:

### Connection Request Sent Event
When a connection request is sent.

**Topic**: `connection-request-sent-topic`

**Event Payload**:
```json
{
  "fromUserId": 1,
  "toUserId": 2,
  "timestamp": "2025-10-27T10:30:00"
}
```

### Connection Accepted Event
When a connection request is accepted.

**Topic**: `connection-accepted-topic`

**Event Payload**:
```json
{
  "userId1": 1,
  "userId2": 2,
  "timestamp": "2025-10-27T10:30:00"
}
```

## 🔒 Security

### Authentication
- User authentication is handled by the API Gateway
- User ID is extracted from JWT token via request header
- Direct service access bypasses authentication (not recommended for production)

### Authorization
- Users can only manage their own connections
- Users cannot send connection requests to themselves

## 🐛 Troubleshooting

### Common Issues

1. **Neo4j Connection Failed**
   - Verify Neo4j is running on port 7687
   - Check Neo4j credentials
   - Ensure Bolt protocol is enabled

2. **Service Not Registering with Eureka**
   - Check Eureka server is running
   - Verify network connectivity
   - Check `eureka.client.serviceUrl.defaultZone` configuration

3. **Kafka Connection Failed**
   - Ensure Kafka is running
   - Check bootstrap servers configuration
   - Verify network connectivity to Kafka

4. **Graph Queries Slow**
   - Create indexes on Person nodes
   - Optimize Cypher queries
   - Check Neo4j memory configuration

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
    org.springframework.data.neo4j: DEBUG
```

### Neo4j Performance Tuning

Create indexes for better performance:
```cypher
CREATE INDEX person_id FOR (p:Person) ON (p.id);
CREATE INDEX person_email FOR (p:Person) ON (p.email);
```

## 📝 Development Notes

### Graph Database Best Practices

1. **Use Cypher Efficiently**
   - Minimize the number of graph traversals
   - Use relationship direction when possible
   - Leverage indexes for lookups

2. **Connection Patterns**
   - Always create bidirectional CONNECTED_TO relationships
   - Keep CONNECTION_REQUESTED unidirectional
   - Clean up rejected requests

3. **Testing Graph Queries**
   - Use Neo4j Browser for query development
   - Test with realistic graph sizes
   - Monitor query performance

### Adding New Features

Common enhancements:
- Get 2nd and 3rd-degree connections
- Find mutual connections
- Connection suggestions based on graph proximity
- Connection statistics

## 🔗 Related Services

- **API Gateway**: Routes requests to this service and handles authentication
- **Discovery Server**: Service registration
- **Users Service**: Provides user authentication and user data
- **Notification Service**: Consumes connection events from Kafka
- **Posts Service**: May use connection data for feed personalization

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data Neo4j](https://spring.io/projects/spring-data-neo4j)
- [Neo4j Documentation](https://neo4j.com/docs/)
- [Cypher Query Language](https://neo4j.com/docs/cypher-manual/current/)
- [Graph Database Concepts](https://neo4j.com/developer/graph-database/)
- [Apache Kafka](https://kafka.apache.org/documentation/)

---

For questions or issues, please refer to the main project README or create an issue in the repository.


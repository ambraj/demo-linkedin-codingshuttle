# Discovery Server (Eureka)

The Discovery Server is a Netflix Eureka-based service registry that enables dynamic service discovery in the LinkedIn clone microservices application. It maintains a registry of all microservice instances and their locations.

## 📋 Table of Contents
- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Configuration](#configuration)
- [How It Works](#how-it-works)
- [Dashboard](#dashboard)
- [Running the Service](#running-the-service)
- [Environment Variables](#environment-variables)

## 🎯 Overview

The Discovery Server handles:
- Service registration and deregistration
- Service instance health monitoring
- Service discovery for client applications
- Load balancing support
- Fault tolerance and failover
- Central registry for all microservices

## 💻 Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: 25
- **Service Discovery**: Netflix Eureka Server
- **Build Tool**: Maven

### Key Dependencies
- `spring-cloud-starter-netflix-eureka-server` - Eureka Server
- `spring-boot-starter-actuator` - Health checks and monitoring
- `lombok` - Reduce boilerplate code

## ⚙️ Configuration

### Application Properties

**Service Configuration**
- **Application Name**: `discovery-server`
- **Server Port**: `8761`

**Eureka Server Configuration**
- **Register with Eureka**: `false` (This is the Eureka server itself)
- **Fetch Registry**: `false` (No need to fetch registry from itself)

### application.yml
```yaml
spring:
  application:
    name: discovery-server

server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

## 🔄 How It Works

### Service Registration Flow

```
┌─────────────────┐
│  Microservice   │
│  (e.g., Users)  │
└────────┬────────┘
         │
         │ 1. Register on startup
         │    (POST /eureka/apps/{appName})
         │
         ▼
┌─────────────────┐
│ Discovery Server│
│  (Eureka)       │
│  Port: 8761     │
└────────┬────────┘
         │
         │ 2. Send heartbeat every 30s
         │    (PUT /eureka/apps/{appName}/{instanceId})
         │
         ▼
┌─────────────────┐
│ Service Registry│
│   (In-Memory)   │
└─────────────────┘
```

### Service Discovery Flow

```
┌─────────────────┐
│   API Gateway   │
└────────┬────────┘
         │
         │ 1. Request service instances
         │    (GET /eureka/apps/{appName})
         │
         ▼
┌─────────────────┐
│ Discovery Server│
└────────┬────────┘
         │
         │ 2. Return list of instances
         │
         ▼
┌─────────────────┐
│   API Gateway   │
│  Load Balance   │
│  & Route        │
└─────────────────┘
```

## 📊 Dashboard

The Discovery Server provides a web-based dashboard to monitor registered services.

### Accessing the Dashboard

**URL**: `http://localhost:8761`

### Dashboard Features

1. **System Status**
   - Environment information
   - Data center information
   - Current time

2. **Instances Currently Registered**
   - Application name
   - AMI ID
   - Availability zones
   - Status (UP/DOWN)
   - Instance URLs

3. **General Info**
   - Total memory
   - Environment
   - CPU count

4. **Instance Info**
   - ipAddr
   - status
   - homePageUrl
   - healthCheckUrl
   - statusPageUrl

### Registered Services

When all services are running, you should see:
- `API-GATEWAY`
- `USERS-SERVICE`
- `POSTS-SERVICE`
- `CONNECTIONS-SERVICE`
- `NOTIFICATION-SERVICE`

### Example Dashboard View

```
Application             AMIs        Availability Zones      Status
API-GATEWAY            n/a (1)     (1)                     UP (1) - api-gateway:8080
USERS-SERVICE          n/a (1)     (1)                     UP (1) - users-service:9010
POSTS-SERVICE          n/a (1)     (1)                     UP (1) - posts-service:9020
CONNECTIONS-SERVICE    n/a (1)     (1)                     UP (1) - connections-service:9030
NOTIFICATION-SERVICE   n/a (1)     (1)                     UP (1) - notification-service:9040
```

## 🚀 Running the Service

### Using Docker Compose (Recommended)

From the root directory:
```bash
docker-compose up discovery-server
```

### Using Docker

1. **Build the Docker image**:
   ```bash
   cd discovery-server
   mvn clean package
   docker build -t ambraj/discovery-server .
   ```

2. **Run the container**:
   ```bash
   docker run -d \
     --name discovery-server \
     -p 8761:8761 \
     ambraj/discovery-server
   ```

### Using Maven (Local Development)

1. **Run locally**:
   ```bash
   cd discovery-server
   mvn spring-boot:run
   ```

2. **Build JAR**:
   ```bash
   mvn clean package
   java -jar target/discovery-server-0.0.1-SNAPSHOT.jar
   ```

### Using Kubernetes

```bash
# Deploy Discovery Server
kubectl apply -f k8s/discovery-server.yml

# Check status
kubectl get pods -l app=discovery-server
kubectl logs -f deployment/discovery-server

# Access dashboard (port-forward)
kubectl port-forward deployment/discovery-server 8761:8761
```

Then access at `http://localhost:8761`

## 🌍 Environment Variables

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `SERVER_PORT` | Server port | `8761` | No |
| `EUREKA_INSTANCE_HOSTNAME` | Instance hostname | `localhost` | No |

## 🔍 Health Check

The service exposes actuator endpoints for health monitoring:

**Health Check Endpoint**:
```bash
curl http://localhost:8761/actuator/health
```

**Response**:
```json
{
  "status": "UP"
}
```

## 📡 Eureka REST API

The Eureka server exposes REST endpoints for service registration and discovery:

### 1. Get All Registered Applications

```bash
curl http://localhost:8761/eureka/apps
```

Response includes XML/JSON with all registered applications.

### 2. Get Specific Application

```bash
curl http://localhost:8761/eureka/apps/USERS-SERVICE
```

### 3. Get Application Instance

```bash
curl http://localhost:8761/eureka/apps/USERS-SERVICE/{instanceId}
```

### 4. Register New Instance (Done by services automatically)

```bash
POST http://localhost:8761/eureka/apps/{appName}
```

### 5. Renew Instance (Heartbeat)

```bash
PUT http://localhost:8761/eureka/apps/{appName}/{instanceId}
```

### 6. Deregister Instance

```bash
DELETE http://localhost:8761/eureka/apps/{appName}/{instanceId}
```

## ⚙️ Configuration Details

### Client Configuration (For Microservices)

Microservices connect to Eureka using:

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://discovery-server:8761/eureka/
  instance:
    preferIpAddress: true
    leaseRenewalIntervalInSeconds: 30
    leaseExpirationDurationInSeconds: 90
```

### Server Configuration Options

```yaml
eureka:
  server:
    enable-self-preservation: true
    eviction-interval-timer-in-ms: 60000
    renewal-percent-threshold: 0.85
```

### Self-Preservation Mode

Eureka enters self-preservation mode when:
- Too many instances fail to send heartbeats
- Prevents cascading failures
- Keeps expired instances in registry

**Note**: Can be disabled in development:
```yaml
eureka:
  server:
    enable-self-preservation: false
```

## 📊 Monitoring

### Actuator Endpoints

- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`

### Registry Size Monitoring

```bash
curl http://localhost:8761/actuator/metrics/eureka.server.registry.count
```

### Heartbeat Monitoring

Monitor last renewal timestamps in the dashboard or via API.

## 🐛 Troubleshooting

### Common Issues

1. **Service Not Registering**
   - Check network connectivity to Eureka server
   - Verify `eureka.client.serviceUrl.defaultZone` in service config
   - Check firewall rules
   - Review service logs for registration errors

2. **Service Appears as DOWN**
   - Check service health endpoint
   - Verify heartbeat configuration
   - Check service instance is actually running
   - Review lease renewal settings

3. **Self-Preservation Mode Activated**
   ```
   EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT.
   ```
   - Normal in development when frequently stopping/starting services
   - Can disable self-preservation in dev environment
   - In production, indicates network issues

4. **Instances Not Being Evicted**
   - Check `eureka.server.eviction-interval-timer-in-ms`
   - Verify lease expiration duration
   - Review self-preservation mode status

### Debug Mode

Enable debug logging:
```yaml
logging:
  level:
    com.netflix.eureka: DEBUG
    com.netflix.discovery: DEBUG
```

### Testing Service Registration

1. Start Eureka server
2. Start a microservice
3. Check dashboard at `http://localhost:8761`
4. Verify service appears in registry
5. Stop microservice
6. Wait for lease expiration (90 seconds default)
7. Verify service is removed from registry

## 📝 Development Notes

### High Availability Setup

For production, run multiple Eureka instances:

**Eureka Server 1**:
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-server-2:8761/eureka/
```

**Eureka Server 2**:
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-server-1:8761/eureka/
```

### Security (Production)

Add security for production:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Configure authentication:
```yaml
spring:
  security:
    user:
      name: eureka
      password: ${EUREKA_PASSWORD}
```

### Peer Awareness

Enable peer awareness for multiple Eureka instances:
```yaml
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
```

## 🔧 Advanced Configuration

### Registry Fetch Interval

Client-side configuration:
```yaml
eureka:
  client:
    registry-fetch-interval-seconds: 30
```

### Instance Metadata

Services can add custom metadata:
```yaml
eureka:
  instance:
    metadata-map:
      zone: us-east-1a
      version: 1.0.0
```

### Response Cache

Configure response caching:
```yaml
eureka:
  server:
    response-cache-update-interval-ms: 30000
    response-cache-auto-expiration-in-seconds: 180
```

## 🔗 Related Services

- **API Gateway**: Uses Eureka for service discovery
- **Users Service**: Registers with Eureka
- **Posts Service**: Registers with Eureka
- **Connections Service**: Registers with Eureka
- **Notification Service**: Registers with Eureka

## 📚 Additional Resources

- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)
- [Netflix Eureka GitHub](https://github.com/Netflix/eureka)
- [Eureka REST API](https://github.com/Netflix/eureka/wiki/Eureka-REST-operations)
- [Service Discovery Pattern](https://microservices.io/patterns/service-registry.html)

---

For questions or issues, please refer to the main project README or create an issue in the repository.


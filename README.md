# LinkedIn Clone - Microservices Architecture

A comprehensive LinkedIn clone built using Spring Boot microservices architecture with event-driven communication using Apache Kafka.

## 📋 Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Running with Docker Compose](#running-with-docker-compose)
  - [Running on Google Kubernetes Engine (GKE)](#running-on-google-kubernetes-engine-gke)
- [API Documentation](#api-documentation)
- [Service Ports](#service-ports)

## 🎯 Overview

This project is a microservices-based LinkedIn clone that demonstrates enterprise-level architecture patterns including:
- Service Discovery (Eureka)
- API Gateway (Spring Cloud Gateway)
- Event-driven architecture (Apache Kafka)
- Containerization (Docker)
- Container orchestration (Kubernetes)
- Multiple database technologies (PostgreSQL, Neo4j)

## 🏗️ Architecture

```
                                    ┌─────────────────┐
                                    │   API Gateway   │
                                    │   Port: 8080    │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
            ┌───────▼────────┐      ┌───────▼────────┐      ┌───────▼────────┐
            │ Users Service  │      │ Posts Service  │      │  Connections   │
            │  Port: 9010    │      │  Port: 9020    │      │   Service      │
            │  (PostgreSQL)  │      │  (PostgreSQL)  │      │  Port: 9030    │
            └────────────────┘      └────────┬───────┘      │   (Neo4j)      │
                                             │               └────────────────┘
                                             │
                                    ┌────────▼────────┐
                                    │  Notification   │
                                    │    Service      │
                                    │  Port: 9040     │
                                    │  (PostgreSQL)   │
                                    └─────────────────┘
                                             ▲
                                             │
                                    ┌────────┴────────┐
                                    │  Apache Kafka   │
                                    │  Port: 9092     │
                                    └─────────────────┘
```

## 🚀 Microservices

### 1. Discovery Server (Eureka)
- **Port**: 8761
- **Purpose**: Service registry for microservice discovery
- **Technology**: Netflix Eureka
- [📖 Detailed Documentation](./discovery-server/README.md)

### 2. API Gateway
- **Port**: 8080
- **Purpose**: Single entry point for all client requests, handles routing and authentication
- **Technology**: Spring Cloud Gateway
- **Routes**:
  - `/api/v1/users/**` → Users Service
  - `/api/v1/posts/**` → Posts Service
  - `/api/v1/connections/**` → Connections Service
- [📖 Detailed Documentation](./api-gateway/README.md)

### 3. Users Service
- **Port**: 9010 (Direct), 8080 (via Gateway)
- **Context Path**: `/users`
- **Database**: PostgreSQL (Port: 5432)
- **Purpose**: User authentication and management
- **Endpoints**:
  - `POST /api/v1/users/auth/signup` - User registration
  - `POST /api/v1/users/auth/login` - User login (returns JWT token)
- [📖 Detailed Documentation](./users-service/README.md)

### 4. Posts Service
- **Port**: 9020 (Direct), 8080 (via Gateway)
- **Context Path**: `/posts`
- **Database**: PostgreSQL (Port: 5433)
- **Purpose**: Manage user posts and likes
- **Endpoints**:
  - `POST /api/v1/posts/core` - Create a post
  - `GET /api/v1/posts/core/{postId}` - Get post by ID
  - `GET /api/v1/posts/core/users/allPosts` - Get all posts for current user
  - `POST /api/v1/posts/likes/{postId}/like` - Like a post
  - `DELETE /api/v1/posts/likes/{postId}/unlike` - Unlike a post
- [📖 Detailed Documentation](./posts-service/README.md)

### 5. Connections Service
- **Port**: 9030 (Direct), 8080 (via Gateway)
- **Context Path**: `/connections`
- **Database**: Neo4j (Ports: 7687, 7474)
- **Purpose**: Manage user connections and network
- **Endpoints**:
  - `GET /api/v1/connections/core/first-degree` - Get first-degree connections
  - `POST /api/v1/connections/core/request/{userId}` - Send connection request
  - `POST /api/v1/connections/core/accept/{userId}` - Accept connection request
  - `POST /api/v1/connections/core/reject/{userId}` - Reject connection request
- [📖 Detailed Documentation](./connections-service/README.md)

### 6. Notification Service
- **Port**: 9040
- **Database**: PostgreSQL (Port: 5434)
- **Purpose**: Handle and store notifications via Kafka events
- **Technology**: Kafka consumer for event-driven notifications
- [📖 Detailed Documentation](./notification-service/README.md)

## 💻 Technology Stack

### Backend
- **Java**: 25
- **Spring Boot**: 3.5.6
- **Spring Cloud**: 2025.0.0
- **Spring Cloud Gateway**: API Gateway
- **Spring Cloud Netflix Eureka**: Service Discovery
- **Spring Data JPA**: ORM for PostgreSQL
- **Spring Data Neo4j**: Graph database integration
- **Spring Kafka**: Event streaming

### Databases
- **PostgreSQL**: Relational database for Users, Posts, and Notifications
- **Neo4j**: Graph database for Connections

### Message Broker
- **Apache Kafka**: Event streaming platform
- **Kafbat UI**: Kafka management UI (Port: 8090)

### DevOps
- **Docker**: Containerization
- **Docker Compose**: Local multi-container deployment
- **Kubernetes**: Container orchestration
- **Google Kubernetes Engine (GKE)**: Cloud deployment

### Security
- **JWT**: JSON Web Tokens for authentication
- **Custom Authentication Filter**: Gateway-level authentication

## 📦 Prerequisites

### For Docker Compose
- Docker Desktop (or Docker Engine + Docker Compose)
- Docker Compose V2+
- Minimum 8GB RAM allocated to Docker

### For Kubernetes/GKE
- kubectl CLI tool
- Google Cloud SDK (gcloud)
- Active GCP account with billing enabled
- Kubernetes cluster on GKE

### For Local Development
- Java 25 or compatible version
- Maven 3.6+
- PostgreSQL 15+
- Neo4j 5.x
- Apache Kafka 3.x

## 🚀 Getting Started

### Running with Docker Compose

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd demo-linkedin-codingshuttle
   ```

2. **Build all services** (Optional - images are available on Docker Hub)
   ```bash
   # Build individual services
   cd users-service && mvn clean package && cd ..
   cd posts-service && mvn clean package && cd ..
   cd connections-service && mvn clean package && cd ..
   cd notification-service && mvn clean package && cd ..
   cd api-gateway && mvn clean package && cd ..
   cd discovery-server && mvn clean package && cd ..
   ```

3. **Start all services**
   ```bash
   docker-compose up -d
   ```

4. **Verify all services are running**
   ```bash
   docker-compose ps
   ```

5. **Access the services**
   - API Gateway: http://localhost:8080
   - Eureka Dashboard: http://localhost:8761
   - Kafka UI: http://localhost:8090
   - Neo4j Browser: http://localhost:7474 (username: neo4j, password: password)

6. **Stop all services**
   ```bash
   docker-compose down
   ```

7. **Stop and remove volumes** (⚠️ This will delete all data)
   ```bash
   docker-compose down -v
   ```

### Running on Google Kubernetes Engine (GKE)

#### 1. Setup GKE Cluster

```bash
# Set your project ID
export PROJECT_ID=your-gcp-project-id
gcloud config set project $PROJECT_ID

# Create GKE cluster
gcloud container clusters create linkedin-cluster \
    --zone=us-central1-a \
    --num-nodes=3 \
    --machine-type=e2-medium \
    --enable-autoscaling \
    --min-nodes=2 \
    --max-nodes=5

# Get cluster credentials
gcloud container clusters get-credentials linkedin-cluster --zone=us-central1-a
```

#### 2. Deploy Databases

```bash
# Deploy PostgreSQL databases
kubectl apply -f k8s/users-db.yml
kubectl apply -f k8s/posts-db.yml
kubectl apply -f k8s/notification-db.yml

# Deploy Neo4j database
kubectl apply -f k8s/connections-db.yml

# Wait for databases to be ready
kubectl wait --for=condition=ready pod -l app=users-db --timeout=300s
kubectl wait --for=condition=ready pod -l app=posts-db --timeout=300s
kubectl wait --for=condition=ready pod -l app=notification-db --timeout=300s
kubectl wait --for=condition=ready pod -l app=connections-db --timeout=300s
```

#### 3. Deploy Kafka

```bash
# Deploy Kafka
kubectl apply -f k8s/kafka.yml

# Deploy Kafka UI
kubectl apply -f k8s/kafkaui.yml

# Wait for Kafka to be ready
kubectl wait --for=condition=ready pod -l app=kafka --timeout=300s
```

#### 4. Deploy Microservices

```bash
# Deploy Discovery Server (Eureka)
kubectl apply -f k8s/discovery-server.yml

# Wait for discovery server to be ready
kubectl wait --for=condition=ready pod -l app=discovery-server --timeout=300s

# Deploy microservices
kubectl apply -f k8s/users-service.yml
kubectl apply -f k8s/posts-service.yml
kubectl apply -f k8s/connections-service.yml
kubectl apply -f k8s/notification-service.yml

# Deploy API Gateway
kubectl apply -f k8s/api-gateway.yml

# Wait for all services to be ready
kubectl wait --for=condition=ready pod -l app=api-gateway --timeout=300s
```

#### 5. Setup Ingress

```bash
# Deploy ingress
kubectl apply -f k8s/ingress.yml

# Get external IP (may take a few minutes)
kubectl get ingress myingress
```

#### 6. Access the Application

Once the ingress has an external IP:
- API Gateway: http://<EXTERNAL-IP>/
- Kafka UI: http://<EXTERNAL-IP>/kafka-ui

#### 7. Monitor Deployments

```bash
# Check all pods
kubectl get pods

# Check all services
kubectl get services

# Check logs for a specific service
kubectl logs -f deployment/users-service

# Describe a pod for troubleshooting
kubectl describe pod <pod-name>
```

#### 8. Cleanup GKE Resources

```bash
# Delete all Kubernetes resources
kubectl delete -f k8s/

# Delete the GKE cluster
gcloud container clusters delete linkedin-cluster --zone=us-central1-a
```

## 📚 API Documentation

### Authentication Flow

1. **Register a new user**
   ```bash
   curl -X POST http://localhost:8080/api/v1/users/auth/signup \
     -H "Content-Type: application/json" \
     -d '{
       "email": "user@example.com",
       "password": "password123",
       "name": "John Doe"
     }'
   ```

2. **Login and get JWT token**
   ```bash
   curl -X POST http://localhost:8080/api/v1/users/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "email": "user@example.com",
       "password": "password123"
     }'
   ```

3. **Use JWT token for authenticated requests**
   ```bash
   curl -X POST http://localhost:8080/api/v1/posts/core \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <your-jwt-token>" \
     -d '{
       "content": "My first post!"
     }'
   ```

### Complete API Reference

For detailed API documentation for each service, please refer to the individual README.md files in each service directory.

## 🔌 Service Ports

### Docker Compose Ports

| Service               | Internal Port | External Port | Purpose                    |
|-----------------------|---------------|---------------|----------------------------|
| API Gateway           | 8080          | 8080          | Main application entry     |
| Discovery Server      | 8761          | 8761          | Service registry           |
| Users Service         | 8080          | 9010          | User management            |
| Posts Service         | 8080          | 9020          | Post management            |
| Connections Service   | 8080          | 9030          | Connection management      |
| Notification Service  | 8080          | 9040          | Notification handling      |
| Users DB (PostgreSQL) | 5432          | 5432          | User data storage          |
| Posts DB (PostgreSQL) | 5432          | 5433          | Post data storage          |
| Notification DB (PostgreSQL) | 5432   | 5434          | Notification data storage  |
| Connections DB (Neo4j) | 7687         | 7687          | Graph database (Bolt)      |
| Neo4j Browser         | 7474          | 7474          | Neo4j web interface        |
| Kafka                 | 9092          | -             | Message broker             |
| Kafka UI              | 8080          | 8090          | Kafka management UI        |

### Kubernetes Ports

All services use ClusterIP and are accessed via the Ingress controller on port 80/443.

## 🛠️ Development

### Building Individual Services

Each service can be built independently:

```bash
cd <service-name>
mvn clean package
docker build -t ambraj/<service-name> .
docker push ambraj/<service-name>
```

### Running Services Locally

For local development without Docker:

1. Start PostgreSQL, Neo4j, and Kafka locally
2. Update `application.yml` in each service with local connection details
3. Start Eureka server first
4. Start individual services using:
   ```bash
   cd <service-name>
   mvn spring-boot:run
   ```

## 🐛 Troubleshooting

### Docker Compose Issues

- **Services not starting**: Check logs with `docker-compose logs <service-name>`
- **Database connection errors**: Ensure databases are fully started before services
- **Port conflicts**: Make sure the ports aren't already in use

### Kubernetes Issues

- **Pods not ready**: Check pod logs with `kubectl logs <pod-name>`
- **Service not accessible**: Verify ingress is configured and has external IP
- **Database connection issues**: Check if database pods are running and ready

## 📝 License

This project is created for educational purposes as part of CodingShuttle training.

## 👥 Contributors

- Development Team at CodingShuttle

## 📞 Support

For issues and questions, please create an issue in the repository.

---

**Note**: This is a demo project for learning microservices architecture. Not intended for production use without proper security hardening and additional configurations.


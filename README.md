# LinkedIn Clone - Microservices Architecture

Spring Boot microservices application with event-driven communication using Kafka.

## 🎯 Overview

Enterprise-level microservices architecture demonstrating:
- Service Discovery (Eureka), API Gateway (Spring Cloud Gateway)
- Event-driven architecture (Kafka), Containerization (Docker/Kubernetes)
- Multiple databases (PostgreSQL, Neo4j)

## 🏗️ Architecture

API Gateway (8080) → Users Service (9010, PostgreSQL), Posts Service (9020, PostgreSQL), Connections Service (9030, Neo4j)
Posts/Connections → Kafka (9092) → Notification Service (9040, PostgreSQL)
All services register with Discovery Server (Eureka, 8761)

## 🚀 Microservices

1. **Discovery Server** (8761) - Eureka service registry
2. **API Gateway** (8080) - Routes to `/api/v1/users/**`, `/api/v1/posts/**`, `/api/v1/connections/**`
3. **Users Service** (9010) - Authentication, JWT tokens, PostgreSQL
4. **Posts Service** (9020) - Create/view posts, likes, PostgreSQL  
5. **Connections Service** (9030) - Network graph, connection requests, Neo4j
6. **Notification Service** (9040) - Kafka event consumer, PostgreSQL

## 💻 Technology Stack

**Backend:** Java 25, Spring Boot 3.5.7, Spring Cloud 2025.0.0, Spring Cloud Gateway, Eureka, Spring Data JPA, Spring Data Neo4j, Spring Kafka

**Databases:** PostgreSQL (Users, Posts, Notifications), Neo4j (Connections)

**DevOps:** Docker, Docker Compose, Kubernetes, GKE

**Security:** JWT authentication via custom Gateway filter

## 📦 Prerequisites

**Docker Compose:** Docker Desktop, 8GB+ RAM allocated
**Kubernetes/GKE:** kubectl, gcloud CLI, active GCP account
**Local Dev:** Java 25, Maven 3.6+, PostgreSQL 15+, Neo4j 5.x, Kafka 3.x

## 🚀 Getting Started

### Docker Compose

```bash
# Clone and navigate
git clone <repository-url> && cd demo-linkedin-codingshuttle

# Start all services (images available on Docker Hub)
docker-compose up -d

# Access services
# API Gateway: http://localhost:8080
# Eureka: http://localhost:8761
# Kafka UI: http://localhost:8090
# Neo4j Browser: http://localhost:7474 (neo4j/password)

# Stop services
docker-compose down      # Keep data
docker-compose down -v   # Delete all data
```

### Kubernetes (GKE)

```bash
# Setup cluster
gcloud container clusters create linkedin-cluster \
  --zone=us-central1-a --num-nodes=3 --machine-type=e2-medium \
  --enable-autoscaling --min-nodes=2 --max-nodes=5
gcloud container clusters get-credentials linkedin-cluster --zone=us-central1-a

# Deploy (order matters)
kubectl apply -f k8s/users-db.yml -f k8s/posts-db.yml -f k8s/notification-db.yml -f k8s/connections-db.yml
kubectl apply -f k8s/kafka.yml -f k8s/kafkaui.yml
kubectl apply -f k8s/discovery-server.yml
kubectl wait --for=condition=ready pod -l app=discovery-server --timeout=300s
kubectl apply -f k8s/users-service.yml -f k8s/posts-service.yml -f k8s/connections-service.yml -f k8s/notification-service.yml
kubectl apply -f k8s/api-gateway.yml -f k8s/ingress.yml

# Monitor
kubectl get pods
kubectl get ingress myingress  # Get external IP
kubectl logs -f deployment/<service-name>

# Cleanup
kubectl delete -f k8s/
gcloud container clusters delete linkedin-cluster --zone=us-central1-a
```

## 📚 API Usage

```bash
# 1. Register user
curl -X POST http://localhost:8080/api/v1/users/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass123","name":"John Doe"}'

# 2. Login (get JWT token)
TOKEN=$(curl -X POST http://localhost:8080/api/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass123"}')

# 3. Use token for authenticated requests
curl -X POST http://localhost:8080/api/v1/posts/core \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"My first post!"}'
```

See individual service READMEs for complete API documentation.

## 🔌 Service Ports

| Service | Internal | External | Database |
|---------|----------|----------|----------|
| API Gateway | 8080 | 8080 | - |
| Discovery Server | 8761 | 8761 | - |
| Users Service | 8080 | 9010 | PostgreSQL:5432 |
| Posts Service | 8080 | 9020 | PostgreSQL:5433 |
| Connections Service | 8080 | 9030 | Neo4j:7687/7474 |
| Notification Service | 8080 | 9040 | PostgreSQL:5434 |
| Kafka UI | 8080 | 8090 | - |

## 🛠️ Development

**Build services:**
```bash
cd <service-name> && mvn clean package
docker build -t ambraj/<service-name> .
docker push ambraj/<service-name>
```

**Run locally:**
1. Start PostgreSQL, Neo4j, Kafka
2. Update `application.yml` with local connection details
3. Start Eureka first, then individual services: `mvn spring-boot:run`

## 🐛 Troubleshooting

**Docker:** Check logs with `docker-compose logs <service-name>`, ensure ports aren't in use
**Kubernetes:** Use `kubectl logs <pod-name>`, verify ingress has external IP

---

**Note:** Demo project for learning. Not production-ready without security hardening.


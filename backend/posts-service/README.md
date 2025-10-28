# Posts Service

Manages posts, likes, and publishes events to Kafka.

## 🎯 Overview

- Create and retrieve posts
- Like/unlike functionality
- Kafka event publishing
- PostgreSQL storage

## 💻 Stack

Spring Boot 3.5.7, Java 25, PostgreSQL, Spring Data JPA, Eureka Client, Kafka Producer, Spring AOP

## ⚙️ Configuration

**Service:** `posts-service`, Port 9020, Context `/posts`
**Database:** PostgreSQL (posts-db:5432)
**Eureka:** http://discovery-server:8761/eureka/
**Kafka:** kafka:9092

## 📡 API Endpoints

**Base:** http://localhost:8080/api/v1/posts (via Gateway) or http://localhost:9020/posts (direct)
**Auth:** All endpoints require JWT token

```bash
# Create post
POST /core
{"content":"My first post!"}

# Get post by ID
GET /core/{postId}

# Get all user posts
GET /core/users/allPosts

# Like post
POST /likes/{postId}/like

# Unlike post
DELETE /likes/{postId}/unlike
```

## 🎯 Kafka Events

**post-created-topic:** Published when post is created
**post-liked-topic:** Published when post is liked

## 🚀 Running

**Docker Compose:** `docker-compose up posts-service posts-db kafka`
**Maven:** `cd posts-service && mvn spring-boot:run`
**Kubernetes:** `kubectl apply -f k8s/posts-db.yml -f k8s/posts-service.yml`

**Health Check:** `curl http://localhost:9020/posts/actuator/health`

## 🐛 Troubleshooting

- **Database Connection Failed:** Verify PostgreSQL on port 5433, check credentials
- **Kafka Connection Failed:** Ensure Kafka is running and accessible
- **401 Unauthorized:** Ensure request comes through API Gateway with valid JWT

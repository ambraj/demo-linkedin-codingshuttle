# Notification Service

Event-driven service consuming Kafka events and storing notifications.

## 🎯 Overview

- Kafka event consumer
- Creates notifications from events
- PostgreSQL storage
- Asynchronous processing

## 💻 Stack

Spring Boot 3.5.7, Java 25, PostgreSQL, Spring Data JPA, Eureka Client, Kafka Consumer

## ⚙️ Configuration

**Service:** `notification-service`, Port 9040
**Database:** PostgreSQL (notification-db:5432)
**Eureka:** http://discovery-server:8761/eureka/
**Kafka:** kafka:9092, Group ID: notification-service

## 📡 Event Consumers

| Topic | Event | Action |
|-------|-------|--------|
| post-created-topic | Post created | Notify all connections |
| post-liked-topic | Post liked | Notify post owner |
| connection-request-sent-topic | Connection request | Notify recipient |
| connection-accepted-topic | Connection accepted | Notify requester |

## 🗄️ Notification Types

`POST_CREATED`, `POST_LIKED`, `CONNECTION_REQUEST`, `CONNECTION_ACCEPTED`

## 🚀 Running

**Docker Compose:** `docker-compose up notification-service notification-db kafka`
**Maven:** `cd notification-service && mvn spring-boot:run`
**Kubernetes:** `kubectl apply -f k8s/notification-db.yml -f k8s/notification-service.yml`

**Health Check:** `curl http://localhost:9040/actuator/health`
**Monitor Kafka:** http://localhost:8090 (Kafka UI)

## 🐛 Troubleshooting

- **Kafka Consumer Not Receiving Events:** Check Kafka is running, verify topic names, review trusted packages config
- **Deserialization Errors:** Ensure event payload structure matches expected format
- **Consumer Lag:** Monitor in Kafka UI, consider horizontal scaling

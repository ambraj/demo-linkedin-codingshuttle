# Discovery Server (Eureka)

Netflix Eureka-based service registry for dynamic service discovery.

## 🎯 Overview

- Service registration/deregistration
- Health monitoring
- Service discovery for clients
- Load balancing support

## 💻 Stack

Spring Boot 3.5.7, Java 25, Netflix Eureka Server

## ⚙️ Configuration

**Service:** `discovery-server`, Port 8761
**Register with Eureka:** false (is the server itself)

## 📊 Dashboard

**URL:** http://localhost:8761

Shows registered services:
- API-GATEWAY
- USERS-SERVICE
- POSTS-SERVICE
- CONNECTIONS-SERVICE
- NOTIFICATION-SERVICE

## 🚀 Running

**Docker Compose:** `docker-compose up discovery-server`
**Maven:** `cd discovery-server && mvn spring-boot:run`
**Kubernetes:** `kubectl apply -f k8s/discovery-server.yml`

**Health Check:** `curl http://localhost:8761/actuator/health`
**View Apps:** `curl http://localhost:8761/eureka/apps`

## 🐛 Troubleshooting

- **Service Not Registering:** Check network connectivity, verify `eureka.client.serviceUrl.defaultZone` in service config
- **Self-Preservation Mode:** Normal in dev when frequently restarting services; can disable with `eureka.server.enable-self-preservation=false`

# Connections Service

Manages user network using Neo4j graph database.

## 🎯 Overview

- Send/accept/reject connection requests
- Get first-degree connections
- Neo4j graph relationships
- Kafka event publishing

## 💻 Stack

Spring Boot 3.5.7, Java 25, Neo4j, Spring Data Neo4j, Eureka Client, Kafka Producer

## ⚙️ Configuration

**Service:** `connections-service`, Port 9030, Context `/connections`
**Database:** Neo4j (bolt://neo4j:7687)
**Eureka:** http://discovery-server:8761/eureka/
**Kafka:** kafka:9092

## 🗄️ Graph Schema

**Person Node:** `{id, name, email}`
**Relationships:**
- `CONNECTED_TO` - Bidirectional, for connected users
- `CONNECTION_REQUESTED` - Unidirectional, for pending requests

## 📡 API Endpoints

**Base:** http://localhost:8080/api/v1/connections (via Gateway) or http://localhost:9030/connections (direct)
**Auth:** All endpoints require JWT token

```bash
# Get first-degree connections
GET /core/first-degree

# Send connection request
POST /core/request/{userId}

# Accept connection request
POST /core/accept/{userId}

# Reject connection request
POST /core/reject/{userId}
```

## 🎯 Kafka Events

**connection-request-sent-topic:** Published when request is sent
**connection-accepted-topic:** Published when request is accepted

## 🚀 Running

**Docker Compose:** `docker-compose up connections-service connections-db kafka`
**Maven:** `cd connections-service && mvn spring-boot:run`
**Kubernetes:** `kubectl apply -f k8s/connections-db.yml -f k8s/connections-service.yml`

**Health Check:** `curl http://localhost:9030/connections/actuator/health`
**Neo4j Browser:** http://localhost:7474 (neo4j/password)

## 🐛 Troubleshooting

- **Neo4j Connection Failed:** Verify Neo4j on port 7687, check credentials (neo4j/password)
- **Slow Graph Queries:** Create indexes: `CREATE INDEX person_id FOR (p:Person) ON (p.id)`

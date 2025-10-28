# LinkedIn Clone - Backend Reference

This document focuses on the backend microservices: exposed REST endpoints (via API Gateway and direct), event topics, data shapes, and end-to-end flow diagrams for the complete app.

## 🎯 Purpose

This README is a developer reference for the backend services in this repository. It documents service responsibilities, public API surface (gateway and direct), event flows (Kafka), and common data models so you can understand how the services interact.

## 🏗️ High-level architecture

API Gateway (8080) → Users Service (9010), Posts Service (9020), Connections Service (9030)
Notification Service (9040) consumes Kafka events produced by Posts/Connections/Users. Discovery Server (8761) provides service registry.

Sequence (simplified):
1. Client → API Gateway → Users Service (auth) or other services
2. Users/Posts/Connections publish domain events to Kafka
3. Notification Service consumes topics and writes notifications to DB

## 🔌 Service ports & context paths (direct access)

- Discovery Server: port 8761 (Eureka)
- API Gateway: port 8080, gateway base: `/api/v1`
- Users Service: port 9010, context-path: `/users`
- Posts Service: port 9020, context-path: `/posts`
- Connections Service: port 9030, context-path: `/connections`
- Notification Service: port 9040, context-path: `/notifications`

When services run inside containers they typically expose container port `8080`; docker-compose maps those to host ports (for example host 9010 → container 8080 for `users-service`). When calling through the API Gateway use the gateway base path.

## 🔐 Authentication

- JWT-based authentication.
- Public endpoints: auth endpoints (signup/login) and some health/actuator endpoints.
- All `/core/**` endpoints across services require `Authorization: Bearer <JWT>`.
- The API Gateway enforces JWT validation via a filter before forwarding to downstream services.

## 🌐 Gateway paths (external client-facing)

All client traffic should generally go through the API Gateway at `http://{host}:8080/api/v1`.

- Users: `/api/v1/users/**`
- Posts: `/api/v1/posts/**`
- Connections: `/api/v1/connections/**`
- Notifications: `/api/v1/notifications/**`

Example: register a user (via gateway)

```bash
curl -X POST http://localhost:8080/api/v1/users/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass123","name":"John Doe"}'
```

## 📡 Detailed endpoints (by service)

Notes: For each endpoint the examples show the gateway path (preferred) and the direct service URL.

1) Users Service (authentication, profile, events)
- Base (gateway): `POST http://localhost:8080/api/v1/users/auth/signup`  
  Direct: `POST http://localhost:9010/users/auth/signup`  
  Payload: { email, password, name }
  - Response: 201 Created with user info (id, email, name)

- `POST /auth/login` (gateway/direct)  
  Payload: { email, password }  
  - Response: 200 OK with JWT token and user details

- `GET /core/me`  (requires JWT)  
  - Returns the authenticated user's profile

- Health and actuator: `GET /users/actuator/health` (direct) or appropriate gateway mapping

- Events: on successful signup or profile updates, the service publishes user-related events to Kafka (see topics below).

2) Posts Service (create/read/like posts)
- `POST /core` (create post)  
  Gateway: `POST /api/v1/posts/core`  
  Direct: `POST /api/v1/posts/core` on port 9020
  Payload: { content, visibility, optional: media, authorId }
  - Publishes `post-created-topic` with minimal post payload

- `GET /core/feed`  
  Returns paginated feed for the authenticated user

- `POST /core/{postId}/like`  
  Publishes `post-liked-topic` to Kafka

3) Connections Service (network graph, requests)
- `GET /core/first-degree` (requires JWT)  
  Returns list of direct connections for authenticated user

- `POST /core/request/{userId}`  
  Send a connection request to userId. Publishes `connection-request-sent-topic`.

- `POST /core/accept/{userId}`  
  Accept a pending request. Publishes `connection-accepted-topic`.

- `POST /core/reject/{userId}`  
  Reject a pending request. No event or optionally a rejection event.

4) Notification Service (consumer + notification CRUD)
- The Notification Service consumes Kafka topics and stores notifications in PostgreSQL.
- Client-facing read endpoint (example): `GET /notifications/core` to fetch notifications for authenticated user (gateway path: `/api/v1/notifications/core`).

## 🧭 Kafka topics & events

Kafka bootstrap (internal) when running in Docker/Kubernetes: `kafka:29092` (container-internal listener). Docker-compose advertises `localhost:9092` for host tools.

Topics used by services (current):
- `post-created-topic` — Published when a new post is created. Payload: { postId, authorId, content, timestamp }
- `post-liked-topic` — Published when a post is liked. Payload: { postId, likedByUserId, timestamp }
- `connection-request-sent-topic` — Published when a connection request is sent. Payload: { fromUserId, toUserId, timestamp }
- `connection-accepted-topic` — Published when connection accepted. Payload: { fromUserId, toUserId, timestamp }

Notification Service subscribes to all of the above and translates events into Notification rows: { id, userId, type, referenceId, message, read, createdAt }

## 🔁 Typical end-to-end flows

1) New user registers
- Client -> Gateway -> Users Service (signup)
- Users Service creates user row in PostgreSQL
- Users Service publishes `user-created` (or signup) event to Kafka (optional)
- Other services (if any) can consume user-created to warm caches or send welcome notifications

2) Create post → notify connections
- Client (authenticated) -> Gateway -> Posts Service (create post)
- Posts Service persists post to PostgreSQL
- Posts Service publishes `post-created-topic`
- Notification Service consumes `post-created-topic` and creates notifications for the author's connections

3) Connection request → notify recipient
- Client -> Gateway -> Connections Service (send request)
- Connections Service creates a pending relationship in Neo4j (CONNECTION_REQUESTED)
- Connections Service publishes `connection-request-sent-topic` with from/to ids
- Notification Service consumes that topic and creates a notification for the recipient

4) Accept request → notify requester
- Client -> Gateway -> Connections Service (accept)
- Connections Service updates Neo4j relationship to `CONNECTED_TO`
- Connections Service publishes `connection-accepted-topic`
- Notification Service consumes and notifies the requester

## 📦 Data models (core / simplified)

User (users-service / PostgreSQL)
- id: long (UUID or numeric)
- name: string
- email: string
- passwordHash: string
- createdAt: timestamp

Post (posts-service / PostgreSQL)
- id: long
- authorId: long
- content: string
- visibility: enum (PUBLIC, CONNECTIONS)
- createdAt: timestamp

Connection (connections-service / Neo4j)
- Person node: { id, name, email }
- Relationship types: `CONNECTION_REQUESTED`, `CONNECTED_TO`

Notification (notification-service / PostgreSQL)
- id: long
- userId: long (recipient)
- type: enum (POST_CREATED, POST_LIKED, CONNECTION_REQUEST, CONNECTION_ACCEPTED)
- referenceId: long (e.g., postId or userId)
- message: string
- read: boolean
- createdAt: timestamp

## 🔍 Troubleshooting & notes for backend developers

- Kafka connectivity: inside Docker the services connect to `kafka:29092`. If you run Kafka on host, use the advertised external listener `localhost:9092` or adjust `application.yml` bootstrap-servers.
- Neo4j: connections-service expects Bolt port `7687` and credentials `neo4j/password` in the local compose configuration.
- Database migrations: services use `spring.jpa.hibernate.ddl-auto=update` for development; consider external migrations (Flyway/Liquibase) for production.
- Logging: services expose actuator endpoints under their context path; use them for health checks.

## 📈 Flow diagrams

Mermaid sequence diagram (if your viewer supports it):

```mermaid
sequenceDiagram
  participant Client
  participant Gateway
  participant Users as UsersService
  participant Posts as PostsService
  participant Conns as ConnectionsService
  participant Kafka
  participant Notif as NotificationService

  Client->>Gateway: POST /api/v1/users/auth/login
  Gateway->>Users: /users/auth/login
  Users-->>Gateway: 200 + JWT
  Gateway-->>Client: 200 + JWT

  Client->>Gateway: POST /api/v1/posts/core (Authorization)
  Gateway->>Posts: /posts/core
  Posts->>Posts: persist post
  Posts->>Kafka: publish post-created-topic
  Kafka->>Notif: deliver event
  Notif->>Notif: create notifications
```

If Mermaid rendering is not available, here's a simplified ASCII flow for the post-create flow:

Client -> Gateway -> Posts Service (persist) -> Kafka -> Notification Service -> DB

## 🗂️ Appendix: running the system (short)

This doc intentionally focuses on the backend contract and flows. For quick local runtime steps (docker-compose or kubernetes), see the top-level README or each service README (e.g. `backend/users-service/README.md`, `backend/connections-service/README.md`).

---

If you'd like, I can:
- Expand the API reference with full request/response JSON schemas for each endpoint.
- Add OpenAPI / swagger artifacts (or point to existing controller annotations) and include a generated OpenAPI file in `docs/`.
- Extract the Kafka event JSON schemas into separate files under `docs/events/` for easier consumption by downstream services.

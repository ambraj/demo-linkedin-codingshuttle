# LinkedIn Clone - Sequence Diagrams

This document contains detailed sequence diagrams for key user flows and system interactions.

## 1. User Registration Flow

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Gateway as API Gateway
    participant UsersService as Users Service
    participant DB as Users Database

    User->>Browser: Enter registration details
    Browser->>Gateway: POST /api/v1/users/auth/signup<br/>{email, password, name}
    Gateway->>UsersService: Forward request<br/>POST /users/auth/signup
    
    UsersService->>UsersService: Validate input
    UsersService->>UsersService: Hash password (BCrypt)
    UsersService->>DB: INSERT user record
    DB-->>UsersService: User created
    
    UsersService-->>Gateway: 201 Created<br/>{id, email, name, createdAt}
    Gateway-->>Browser: 201 Created with user data
    Browser-->>User: Show success message
```

## 2. User Login Flow

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Gateway as API Gateway
    participant UsersService as Users Service
    participant DB as Users Database

    User->>Browser: Enter credentials
    Browser->>Gateway: POST /api/v1/users/auth/login<br/>{email, password}
    Gateway->>UsersService: Forward request<br/>POST /users/auth/login
    
    UsersService->>DB: SELECT user by email
    DB-->>UsersService: User record
    
    UsersService->>UsersService: Verify password (BCrypt)
    alt Password valid
        UsersService->>UsersService: Generate JWT token<br/>(claims: userId, email)
        UsersService-->>Gateway: 200 OK<br/>{token: "eyJ..."}
        Gateway-->>Browser: 200 OK with JWT
        Browser->>Browser: Store JWT in localStorage
        Browser-->>User: Redirect to feed
    else Password invalid
        UsersService-->>Gateway: 401 Unauthorized
        Gateway-->>Browser: 401 Unauthorized
        Browser-->>User: Show error message
    end
```

## 3. Create Post Flow (with Event Publishing)

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Gateway as API Gateway
    participant PostsService as Posts Service
    participant PostsDB as Posts Database
    participant Kafka
    participant NotifService as Notification Service
    participant NotifDB as Notifications Database

    User->>Browser: Write post content
    Browser->>Gateway: POST /api/v1/posts/core<br/>Authorization: Bearer <JWT><br/>{content, visibility}
    
    Gateway->>Gateway: Validate JWT token
    Gateway->>Gateway: Extract userId from token
    Gateway->>PostsService: Forward with X-User-Id header<br/>POST /posts/core
    
    PostsService->>PostsService: Validate input
    PostsService->>PostsDB: INSERT post<br/>(author_id, content, visibility)
    PostsDB-->>PostsService: Post created (id: 123)
    
    PostsService->>Kafka: Publish event<br/>Topic: post-created-topic<br/>{postId: 123, authorId: user1,<br/>content: "...", timestamp}
    
    PostsService-->>Gateway: 201 Created<br/>{id: 123, authorId, content, ...}
    Gateway-->>Browser: 201 Created with post data
    Browser-->>User: Show new post in feed
    
    Note over Kafka,NotifService: Asynchronous processing
    
    Kafka->>NotifService: Deliver event<br/>post-created-topic
    NotifService->>NotifService: Process event<br/>Find author's connections
    
    loop For each connection
        NotifService->>NotifDB: INSERT notification<br/>(userId, type: POST_CREATED,<br/>referenceId: 123, message)
    end
    
    NotifDB-->>NotifService: Notifications created
```

## 4. Like Post Flow

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Gateway as API Gateway
    participant PostsService as Posts Service
    participant PostsDB as Posts Database
    participant Kafka
    participant NotifService as Notification Service
    participant NotifDB as Notifications Database

    User->>Browser: Click like button on post
    Browser->>Gateway: POST /api/v1/posts/likes/123/like<br/>Authorization: Bearer <JWT>
    
    Gateway->>Gateway: Validate JWT & extract userId
    Gateway->>PostsService: POST /posts/likes/123/like<br/>X-User-Id: user2
    
    PostsService->>PostsDB: Check if already liked
    alt Not already liked
        PostsDB-->>PostsService: No existing like
        PostsService->>PostsDB: INSERT into post_likes<br/>(post_id: 123, user_id: user2)
        PostsService->>PostsDB: UPDATE posts<br/>SET like_count = like_count + 1<br/>WHERE id = 123
        
        PostsService->>Kafka: Publish event<br/>Topic: post-liked-topic<br/>{postId: 123, likedByUserId: user2,<br/>timestamp}
        
        PostsService-->>Gateway: 204 No Content
        Gateway-->>Browser: 204 No Content
        Browser->>Browser: Update UI (increment count)
        Browser-->>User: Show liked state
        
        Note over Kafka,NotifService: Asynchronous notification
        
        Kafka->>NotifService: Deliver event
        NotifService->>NotifService: Get post author (user1)
        NotifService->>NotifDB: INSERT notification<br/>(userId: user1,<br/>type: POST_LIKED,<br/>referenceId: 123,<br/>message: "user2 liked your post")
    else Already liked
        PostsDB-->>PostsService: Like exists
        PostsService-->>Gateway: 409 Conflict
        Gateway-->>Browser: 409 Conflict
        Browser-->>User: Show error
    end
```

## 5. Send Connection Request Flow

```mermaid
sequenceDiagram
    actor User1
    participant Browser
    participant Gateway as API Gateway
    participant ConnsService as Connections Service
    participant Neo4j
    participant Kafka
    participant NotifService as Notification Service
    participant NotifDB as Notifications Database

    User1->>Browser: Click "Connect" on user2 profile
    Browser->>Gateway: POST /api/v1/connections/core/request/user2<br/>Authorization: Bearer <JWT>
    
    Gateway->>Gateway: Validate JWT<br/>Extract userId (user1)
    Gateway->>ConnsService: POST /connections/core/request/user2<br/>X-User-Id: user1
    
    ConnsService->>Neo4j: Check if relationship exists
    alt No existing relationship
        Neo4j-->>ConnsService: No relationship found
        
        ConnsService->>Neo4j: Cypher Query:<br/>MATCH (a:Person {id: 'user1'})<br/>MATCH (b:Person {id: 'user2'})<br/>CREATE (a)-[:CONNECTION_REQUESTED]->(b)
        
        Neo4j-->>ConnsService: Relationship created
        
        ConnsService->>Kafka: Publish event<br/>Topic: connection-request-sent-topic<br/>{fromUserId: user1,<br/>toUserId: user2, timestamp}
        
        ConnsService-->>Gateway: 200 OK {success: true}
        Gateway-->>Browser: 200 OK
        Browser-->>User1: Show "Request Sent"
        
        Note over Kafka,NotifService: Notify recipient
        
        Kafka->>NotifService: Deliver event
        NotifService->>NotifDB: INSERT notification<br/>(userId: user2,<br/>type: CONNECTION_REQUEST,<br/>referenceId: user1,<br/>message: "user1 sent connection request")
        
    else Relationship exists
        Neo4j-->>ConnsService: Relationship found
        ConnsService-->>Gateway: 400 Bad Request<br/>{error: "Request already exists"}
        Gateway-->>Browser: 400 Bad Request
        Browser-->>User1: Show error
    end
```

## 6. Accept Connection Request Flow

```mermaid
sequenceDiagram
    actor User2
    participant Browser
    participant Gateway as API Gateway
    participant ConnsService as Connections Service
    participant Neo4j
    participant Kafka
    participant NotifService as Notification Service
    participant NotifDB as Notifications Database

    User2->>Browser: Click "Accept" on request from user1
    Browser->>Gateway: POST /api/v1/connections/core/accept/user1<br/>Authorization: Bearer <JWT>
    
    Gateway->>Gateway: Validate JWT<br/>Extract userId (user2)
    Gateway->>ConnsService: POST /connections/core/accept/user1<br/>X-User-Id: user2
    
    ConnsService->>Neo4j: Cypher Query:<br/>MATCH (a:Person {id: 'user1'})<br/>      -[r:CONNECTION_REQUESTED]-><br/>      (b:Person {id: 'user2'})<br/>DELETE r
    
    ConnsService->>Neo4j: Cypher Query:<br/>MATCH (a:Person {id: 'user1'})<br/>MATCH (b:Person {id: 'user2'})<br/>CREATE (a)-[:CONNECTED_TO]->(b)<br/>CREATE (b)-[:CONNECTED_TO]->(a)
    
    Neo4j-->>ConnsService: Bidirectional connection created
    
    ConnsService->>Kafka: Publish event<br/>Topic: connection-accepted-topic<br/>{fromUserId: user1,<br/>toUserId: user2, timestamp}
    
    ConnsService-->>Gateway: 200 OK {success: true}
    Gateway-->>Browser: 200 OK
    Browser-->>User2: Update UI to show connection
    
    Note over Kafka,NotifService: Notify requester
    
    Kafka->>NotifService: Deliver event
    NotifService->>NotifDB: INSERT notification<br/>(userId: user1,<br/>type: CONNECTION_ACCEPTED,<br/>referenceId: user2,<br/>message: "user2 accepted your request")
```

## 7. Get Feed / User's Posts Flow

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Gateway as API Gateway
    participant PostsService as Posts Service
    participant PostsDB as Posts Database

    User->>Browser: Navigate to feed
    Browser->>Gateway: GET /api/v1/posts/core/users/allPosts<br/>Authorization: Bearer <JWT>
    
    Gateway->>Gateway: Validate JWT<br/>Extract userId
    Gateway->>PostsService: GET /posts/core/users/allPosts<br/>X-User-Id: userId
    
    PostsService->>PostsDB: SELECT posts<br/>WHERE author_id = userId<br/>OR visibility = 'PUBLIC'<br/>ORDER BY created_at DESC
    
    PostsDB-->>PostsService: List of posts
    
    PostsService->>PostsService: Convert entities to DTOs
    
    PostsService-->>Gateway: 200 OK<br/>[{id, authorId, content,<br/>visibility, likeCount, createdAt}, ...]
    
    Gateway-->>Browser: 200 OK with posts array
    Browser->>Browser: Render posts in feed
    Browser-->>User: Display feed
```

## 8. Get Suggested Connections Flow

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Gateway as API Gateway
    participant ConnsService as Connections Service
    participant Neo4j

    User->>Browser: Navigate to "People You May Know"
    Browser->>Gateway: GET /api/v1/connections/core/suggested-connections<br/>Authorization: Bearer <JWT>
    
    Gateway->>Gateway: Validate JWT<br/>Extract userId (user1)
    Gateway->>ConnsService: GET /connections/core/suggested-connections<br/>X-User-Id: user1
    
    ConnsService->>Neo4j: Cypher Query:<br/>MATCH (me:Person {id: 'user1'})<br/>MATCH (suggestion:Person)<br/>WHERE suggestion.id <> me.id<br/>AND NOT (me)-[:CONNECTED_TO]-(suggestion)<br/>AND NOT (me)-[:CONNECTION_REQUESTED]-(suggestion)<br/>RETURN suggestion<br/>LIMIT 10
    
    Neo4j-->>ConnsService: List of Person nodes
    
    ConnsService->>ConnsService: Convert to PersonDto
    
    ConnsService-->>Gateway: 200 OK<br/>[{id, name, email}, ...]
    
    Gateway-->>Browser: 200 OK with suggestions
    Browser->>Browser: Render suggestion cards
    Browser-->>User: Display suggested connections
```

## 9. Get Notifications Flow

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Gateway as API Gateway
    participant NotifService as Notification Service
    participant NotifDB as Notifications Database

    User->>Browser: Click notifications icon
    Browser->>Gateway: GET /api/v1/notifications/core/users/allNotifications<br/>Authorization: Bearer <JWT>
    
    Gateway->>Gateway: Validate JWT<br/>Extract userId
    Gateway->>NotifService: GET /notifications/core/users/allNotifications<br/>X-User-Id: userId
    
    NotifService->>NotifDB: SELECT * FROM notifications<br/>WHERE user_id = userId<br/>ORDER BY created_at DESC<br/>LIMIT 20
    
    NotifDB-->>NotifService: List of notifications
    
    NotifService->>NotifService: Convert to NotificationDto
    
    NotifService-->>Gateway: 200 OK<br/>[{id, type, referenceId,<br/>message, read, createdAt}, ...]
    
    Gateway-->>Browser: 200 OK with notifications
    Browser->>Browser: Render notification list
    Browser-->>User: Display notifications dropdown
```

## 10. Service Discovery & Registration Flow

```mermaid
sequenceDiagram
    participant Service as Microservice
    participant Eureka as Discovery Server
    participant Gateway as API Gateway

    Note over Service,Gateway: Service Startup
    
    Service->>Service: Application starts
    Service->>Eureka: POST /eureka/apps/{APP_NAME}<br/>Register service instance<br/>{hostname, port, metadata}
    
    Eureka->>Eureka: Add to service registry
    Eureka-->>Service: 204 No Content (registered)
    
    loop Every 30 seconds (Heartbeat)
        Service->>Eureka: PUT /eureka/apps/{APP_NAME}/{INSTANCE_ID}<br/>Send heartbeat
        Eureka-->>Service: 200 OK
    end
    
    Note over Gateway,Eureka: Gateway needs to call a service
    
    Gateway->>Eureka: GET /eureka/apps/{APP_NAME}<br/>Get service instances
    Eureka-->>Gateway: 200 OK<br/>{instances: [{host, port}, ...]}
    
    Gateway->>Gateway: Load balance<br/>Select instance
    Gateway->>Service: Forward request to selected instance
    Service-->>Gateway: Response
    
    Note over Service,Eureka: Service Shutdown
    
    Service->>Eureka: DELETE /eureka/apps/{APP_NAME}/{INSTANCE_ID}<br/>Deregister
    Eureka->>Eureka: Remove from registry
    Eureka-->>Service: 200 OK
```

## 11. API Gateway JWT Validation Flow

```mermaid
sequenceDiagram
    participant Client
    participant Gateway as API Gateway
    participant JWTFilter as JWT Filter
    participant Service as Downstream Service

    Client->>Gateway: Request with JWT<br/>Authorization: Bearer <token>
    
    Gateway->>JWTFilter: Process request
    
    JWTFilter->>JWTFilter: Extract token from header
    
    alt Token present
        JWTFilter->>JWTFilter: Validate token signature
        JWTFilter->>JWTFilter: Check expiration
        
        alt Token valid
            JWTFilter->>JWTFilter: Extract claims<br/>(userId, email)
            JWTFilter->>JWTFilter: Add X-User-Id header
            JWTFilter->>Gateway: Continue filter chain
            Gateway->>Service: Forward request with headers<br/>Authorization + X-User-Id
            Service-->>Gateway: Response
            Gateway-->>Client: Response
        else Token invalid/expired
            JWTFilter-->>Client: 401 Unauthorized<br/>{error: "Invalid token"}
        end
    else No token (public endpoint)
        JWTFilter->>JWTFilter: Check if endpoint is public
        alt Public endpoint
            JWTFilter->>Gateway: Continue without auth
            Gateway->>Service: Forward request
            Service-->>Gateway: Response
            Gateway-->>Client: Response
        else Protected endpoint
            JWTFilter-->>Client: 401 Unauthorized<br/>{error: "Missing token"}
        end
    end
```

## 12. Error Handling Flow

```mermaid
sequenceDiagram
    participant Client
    participant Gateway as API Gateway
    participant Service as Microservice
    participant DB as Database

    Client->>Gateway: Request
    Gateway->>Service: Forward request
    
    Service->>DB: Query database
    
    alt Database error
        DB-->>Service: SQLException
        Service->>Service: Catch exception<br/>@ControllerAdvice handler
        Service-->>Gateway: 500 Internal Server Error<br/>{error: "Database error",<br/>message: "..."}
        Gateway-->>Client: 500 error response
    else Validation error
        Service->>Service: Validate input
        Service->>Service: Validation fails
        Service-->>Gateway: 400 Bad Request<br/>{error: "Validation failed",<br/>fields: {...}}
        Gateway-->>Client: 400 error response
    else Business logic error
        Service->>Service: Process business logic
        Service->>Service: Business rule violated
        Service-->>Gateway: 409 Conflict<br/>{error: "Already exists"}
        Gateway-->>Client: 409 error response
    else Service unavailable
        Gateway->>Service: Request
        Service-->>Gateway: Timeout / No response
        Gateway->>Gateway: Circuit breaker opens
        Gateway-->>Client: 503 Service Unavailable<br/>{error: "Service temporarily unavailable"}
    else Success
        DB-->>Service: Data
        Service-->>Gateway: 200 OK with data
        Gateway-->>Client: 200 OK response
    end
```

---

**Last Updated**: November 1, 2024

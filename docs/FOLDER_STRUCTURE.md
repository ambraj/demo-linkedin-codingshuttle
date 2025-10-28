# LinkedIn Clone - Monorepo Folder Structure

## Overview
This document outlines the complete folder structure for a LinkedIn clone application using a monorepo approach with React frontend and Spring Boot microservices backend.

---

## Root Structure

```
linkedin-clone/
├── .github/
├── frontend/
├── backend/
├── infrastructure/
├── docs/
└── README.md
```

---

## GitHub Actions Workflows

```
.github/
└── workflows/
    ├── frontend-ci.yml           # Frontend CI pipeline
    ├── backend-ci.yml            # Backend services CI pipeline
    ├── deploy-frontend.yml       # Frontend deployment workflow
    └── deploy-backend.yml        # Backend deployment workflow
```

---

## Frontend Structure

```
frontend/
├── public/                       # Static files
├── src/
│   ├── assets/                   # Images, fonts, icons
│   ├── components/               # React components
│   │   ├── common/              # Reusable components (Button, Modal, etc.)
│   │   ├── feed/                # Feed related components
│   │   ├── profile/             # Profile components
│   │   ├── network/             # Network/Connections components
│   │   └── messaging/           # Messaging components
│   ├── pages/                   # Page-level components
│   ├── services/                # API service calls
│   ├── hooks/                   # Custom React hooks
│   ├── context/                 # React Context providers
│   ├── utils/                   # Utility functions
│   ├── redux/                   # State management
│   │   ├── slices/             # Redux slices
│   │   └── store.js            # Redux store configuration
│   ├── App.js                   # Root component
│   └── index.js                 # Entry point
├── .env.example                 # Environment variables template
├── .dockerignore                # Docker ignore rules
├── Dockerfile                   # Frontend Docker configuration
├── nginx.conf                   # Nginx configuration for production
├── package.json                 # NPM dependencies and scripts
└── README.md                    # Frontend documentation
```

---

## Backend Structure

### Parent POM
```
backend/
└── pom.xml                      # Parent POM for all microservices
```

### Infrastructure Services

```
backend/
├── api-gateway/                 # API Gateway (Spring Cloud Gateway)
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/linkedin/gateway/
│   │       │       ├── config/          # Gateway configuration
│   │       │       ├── filter/          # Custom filters
│   │       │       └── ApiGatewayApplication.java
│   │       └── resources/
│   │           ├── application.yml
│   │           └── application-prod.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── service-discovery/           # Service Registry (Eureka)
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/linkedin/discovery/
│   │       │       └── ServiceDiscoveryApplication.java
│   │       └── resources/
│   │           └── application.yml
│   ├── Dockerfile
│   └── pom.xml
│
└── config-server/               # Centralized Configuration
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   └── com/linkedin/config/
    │       │       └── ConfigServerApplication.java
    │       └── resources/
    │           └── application.yml
    ├── Dockerfile
    └── pom.xml
```

### Business Microservices

```
backend/
├── user-service/                # User management
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/linkedin/user/
│   │   │   │       ├── controller/      # REST controllers
│   │   │   │       ├── service/         # Business logic
│   │   │   │       ├── repository/      # Data access layer
│   │   │   │       ├── model/           # JPA entities
│   │   │   │       ├── dto/             # Data Transfer Objects
│   │   │   │       ├── mapper/          # Entity-DTO mappers
│   │   │   │       ├── exception/       # Custom exceptions
│   │   │   │       ├── config/          # Service configuration
│   │   │   │       └── UserServiceApplication.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-prod.yml
│   │   │       └── db/migration/        # Flyway migrations
│   │   └── test/                        # Unit and integration tests
│   ├── Dockerfile
│   └── pom.xml
│
├── post-service/                # Posts and feed management
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/linkedin/post/
│   │   │   │       ├── controller/
│   │   │   │       ├── service/
│   │   │   │       ├── repository/
│   │   │   │       ├── model/
│   │   │   │       ├── dto/
│   │   │   │       ├── kafka/
│   │   │   │       │   ├── producer/    # Kafka producers
│   │   │   │       │   └── consumer/    # Kafka consumers
│   │   │   │       ├── config/
│   │   │   │       └── PostServiceApplication.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── connection-service/          # Network connections
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/linkedin/connection/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── notification-service/        # Notifications (Kafka consumer)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/linkedin/notification/
│   │   │   │       ├── kafka/
│   │   │   │       │   └── consumer/
│   │   │   │       └── NotificationServiceApplication.java
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── messaging-service/           # Direct messaging
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/linkedin/messaging/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── search-service/              # Search functionality
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/linkedin/search/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
└── common-lib/                  # Shared library
    ├── src/
    │   └── main/
    │       └── java/
    │           └── com/linkedin/common/
    │               ├── dto/             # Common DTOs
    │               ├── exception/       # Common exceptions
    │               ├── util/            # Utility classes
    │               └── config/          # Common configurations
    └── pom.xml
```

---

## Infrastructure

```
infrastructure/
├── kubernetes/                  # Kubernetes manifests
│   ├── base/                   # Base configurations
│   │   ├── api-gateway/
│   │   │   ├── deployment.yaml
│   │   │   ├── service.yaml
│   │   │   └── configmap.yaml
│   │   ├── user-service/
│   │   ├── post-service/
│   │   └── ... (other services)
│   └── overlays/               # Environment-specific overlays
│       ├── dev/
│       ├── staging/
│       └── production/
│
├── docker/                     # Docker Compose
│   └── docker-compose.yml      # Local development setup
│
├── terraform/                  # Infrastructure as Code
│   ├── modules/               # Reusable modules
│   │   ├── eks/              # EKS cluster
│   │   ├── rds/              # PostgreSQL databases
│   │   ├── kafka/            # Kafka cluster
│   │   └── vpc/              # VPC networking
│   ├── environments/          # Environment configurations
│   │   ├── dev/
│   │   ├── staging/
│   │   └── production/
│   └── main.tf
│
└── helm/                       # Helm charts
    └── linkedin-clone/
        ├── Chart.yaml
        ├── values.yaml
        └── templates/
```

---

## Documentation

```
docs/
├── architecture/               # Architecture diagrams and decisions
├── api/                       # API documentation
└── deployment/                # Deployment guides
```
---
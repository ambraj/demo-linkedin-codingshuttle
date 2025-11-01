# docker-bake.hcl

# Platform configuration - Override with PLATFORMS env var
# For CI (faster): export PLATFORMS="linux/amd64"
# For production: export PLATFORMS="linux/amd64,linux/arm64"
variable "PLATFORMS" {
  default = "linux/amd64,linux/arm64"
}

variable "API_GATEWAY_VERSION" {
  default = "v1.0.1"
}

variable "CONNECTIONS_SERVICE_VERSION" {
  default = "v1.0.0"
}

variable "DISCOVERY_SERVER_VERSION" {
  default = "v1.0.0"
}

variable "NOTIFICATION_SERVICE_VERSION" {
  default = "v1.0.0"
}

variable "POSTS_SERVICE_VERSION" {
  default = "v1.0.0"
}

variable "USERS_SERVICE_VERSION" {
  default = "v1.0.0"
}

variable "MAVEN_OPTS" {
  default = "-Dmaven.artifact.threads=5 -Dhttp.keepAlive=false"
}

# Define a group of targets to build
group "default" {
  targets = [
    "discovery-server",
    "api-gateway",
    "users-service",
    "posts-service",
    "connections-service",
    "notification-service"
  ]
}

# Discovery Server
target "discovery-server" {
  dockerfile = "Dockerfile"
  context    = "../../backend/discovery-server"
  tags       = [
    "docker.io/ambraj/discovery-server:${DISCOVERY_SERVER_VERSION}",
    "docker.io/ambraj/discovery-server:latest"
  ]
  platforms  = split(",", PLATFORMS)
  output     = ["type=registry"]
  labels     = {
    "version" = "${DISCOVERY_SERVER_VERSION}"
  }
  args = {
    MAVEN_OPTS = "${MAVEN_OPTS}"
  }
}

# API Gateway Service
target "api-gateway" {
  dockerfile = "Dockerfile"
  context    = "../../backend/api-gateway"
  tags       = [
    "docker.io/ambraj/api-gateway:${API_GATEWAY_VERSION}",
    "docker.io/ambraj/api-gateway:latest"
  ]
  platforms  = split(",", PLATFORMS)
  output     = ["type=registry"]
  labels     = {
    "version" = "${API_GATEWAY_VERSION}"
  }
  args = {
    MAVEN_OPTS = "${MAVEN_OPTS}"
  }
}

# Users Service
target "users-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/users-service"
  tags       = [
    "docker.io/ambraj/users-service:${USERS_SERVICE_VERSION}",
    "docker.io/ambraj/users-service:latest"
  ]
  platforms  = split(",", PLATFORMS)
  output     = ["type=registry"]
  labels     = {
    "version" = "${USERS_SERVICE_VERSION}"
  }
  args = {
    MAVEN_OPTS = "${MAVEN_OPTS}"
  }
}

# Posts Service
target "posts-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/posts-service"
  tags       = [
    "docker.io/ambraj/posts-service:${POSTS_SERVICE_VERSION}",
    "docker.io/ambraj/posts-service:latest"
  ]
  platforms  = split(",", PLATFORMS)
  output     = ["type=registry"]
  labels     = {
    "version" = "${POSTS_SERVICE_VERSION}"
  }
  args = {
    MAVEN_OPTS = "${MAVEN_OPTS}"
  }
}

# Connections Service
target "connections-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/connections-service"
  tags       = [
    "docker.io/ambraj/connections-service:${CONNECTIONS_SERVICE_VERSION}",
    "docker.io/ambraj/connections-service:latest"
  ]
  platforms  = split(",", PLATFORMS)
  output     = ["type=registry"]
  labels     = {
    "version" = "${CONNECTIONS_SERVICE_VERSION}"
  }
  args = {
    MAVEN_OPTS = "${MAVEN_OPTS}"
  }
}

# Notification Service
target "notification-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/notification-service"
  tags       = [
    "docker.io/ambraj/notification-service:${NOTIFICATION_SERVICE_VERSION}",
    "docker.io/ambraj/notification-service:latest"
  ]
  platforms  = split(",", PLATFORMS)
  output     = ["type=registry"]
  labels     = {
    "version" = "${NOTIFICATION_SERVICE_VERSION}"
  }
  args = {
    MAVEN_OPTS = "${MAVEN_OPTS}"
  }
}


# docker-bake.hcl

# Define a group of targets to build
group "default" {
  targets = [
    "api-gateway",
    "connections-service",
    "discovery-server",
    "notification-service",
    "posts-service",
    "users-service"
  ]
}

# API Gateway Service
target "api-gateway" {
  dockerfile = "Dockerfile"
  context    = "../../backend/api-gateway"
  tags       = [
    "docker.io/ambraj/api-gateway:v1.0.0",
    "docker.io/ambraj/api-gateway:latest"
  ]
  platforms  = ["linux/amd64", "linux/arm64"]
  output     = ["type=registry"]
}

# Connections Service
target "connections-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/connections-service"
  tags       = [
    "docker.io/ambraj/connections-service:v1.0.0",
    "docker.io/ambraj/connections-service:latest"
  ]
  platforms  = ["linux/amd64", "linux/arm64"]
  output     = ["type=registry"]
}

# Discovery Server
target "discovery-server" {
  dockerfile = "Dockerfile"
  context    = "../../backend/discovery-server"
  tags       = [
    "docker.io/ambraj/discovery-server:v1.0.0",
    "docker.io/ambraj/discovery-server:latest"
  ]
  platforms  = ["linux/amd64", "linux/arm64"]
  output     = ["type=registry"]
}

# Notification Service
target "notification-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/notification-service"
  tags       = [
    "docker.io/ambraj/notification-service:v1.0.0",
    "docker.io/ambraj/notification-service:latest"
  ]
  platforms  = ["linux/amd64", "linux/arm64"]
  output     = ["type=registry"]
}

# Posts Service
target "posts-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/posts-service"
  tags       = [
    "docker.io/ambraj/posts-service:v1.0.0",
    "docker.io/ambraj/posts-service:latest"
  ]
  platforms  = ["linux/amd64", "linux/arm64"]
  output     = ["type=registry"]
}

# Users Service
target "users-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/users-service"
  tags       = [
    "docker.io/ambraj/users-service:v1.0.0",
    "docker.io/ambraj/users-service:latest"
  ]
  platforms  = ["linux/amd64", "linux/arm64"]
  output     = ["type=registry"]
}

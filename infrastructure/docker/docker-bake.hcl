
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
  tags       = ["ghcr.io/ambraj/api-gateway:latest"]
  platforms  = ["linux/amd64", "linux/arm64"]
  cache-from = ["type=local,src=/tmp/docker-cache/api-gateway"]
  cache-to   = ["type=local,dest=/tmp/docker-cache/api-gateway"]
}

# Connections Service
target "connections-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/connections-service"
  tags       = ["ghcr.io/ambraj/connections-service:latest"]
  platforms  = ["linux/amd64", "linux/arm64"]
  cache-from = ["type=local,src=/tmp/docker-cache/connections-service"]
  cache-to   = ["type=local,dest=/tmp/docker-cache/connections-service"]
}

# Discovery Server
target "discovery-server" {
  dockerfile = "Dockerfile"
  context    = "../../backend/discovery-server"
  tags       = ["ghcr.io/ambraj/discovery-server:latest"]
  platforms  = ["linux/amd64", "linux/arm64"]
  cache-from = ["type=local,src=/tmp/docker-cache/discovery-server"]
  cache-to   = ["type=local,dest=/tmp/docker-cache/discovery-server"]
}

# Notification Service
target "notification-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/notification-service"
  tags       = ["ghcr.io/ambraj/notification-service:latest"]
  platforms  = ["linux/amd64", "linux/arm64"]
  cache-from = ["type=local,src=/tmp/docker-cache/notification-service"]
  cache-to   = ["type=local,dest=/tmp/docker-cache/notification-service"]
}

# Posts Service
target "posts-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/posts-service"
  tags       = ["ghcr.io/ambraj/posts-service:latest"]
  platforms  = ["linux/amd64", "linux/arm64"]
  cache-from = ["type=local,src=/tmp/docker-cache/posts-service"]
  cache-to   = ["type=local,dest=/tmp/docker-cache/posts-service"]
}

# Users Service
target "users-service" {
  dockerfile = "Dockerfile"
  context    = "../../backend/users-service"
  tags       = ["ghcr.io/ambraj/users-service:latest"]
  platforms  = ["linux/amd64", "linux/arm64"]
  cache-from = ["type=local,src=/tmp/docker-cache/users-service"]
  cache-to   = ["type=local,dest=/tmp/docker-cache/users-service"]
}

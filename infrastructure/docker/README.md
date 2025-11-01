# Docker Build and Version Management

## Overview

This directory contains the Docker build configuration using Docker Bake for multi-architecture builds. The setup is optimized for selective building and deployment of services.

## Files

- **docker-bake.hcl** - Docker Bake configuration with multi-arch build settings
- **versions.env** - Centralized version management for all services
- **README.md** - This file

## Version Management

All service versions are managed in a single file: `versions.env`

### How to Update a Service Version

1. Open `versions.env`
2. Update the version for the specific service you want to rebuild/redeploy
3. Commit the change
4. The CI/CD pipeline will detect and build only the changed service

**Example:**
```bash
# To upgrade posts-service from v1.0.0 to v1.0.1
POSTS_SERVICE_VERSION=v1.0.1
```

### Current Version Variables

- `API_GATEWAY_VERSION` - API Gateway service
- `CONNECTIONS_SERVICE_VERSION` - Connections service
- `DISCOVERY_SERVER_VERSION` - Discovery server
- `NOTIFICATION_SERVICE_VERSION` - Notification service
- `POSTS_SERVICE_VERSION` - Posts service
- `USERS_SERVICE_VERSION` - Users service

## Building with Docker Bake

### Build all services
```bash
cd infrastructure/docker
docker buildx bake --push
```

### Build specific services only
```bash
# Build only posts-service and users-service
docker buildx bake --push posts-service users-service
```

### Build with custom versions
```bash
# Export version variables
export POSTS_SERVICE_VERSION=v1.0.2
export USERS_SERVICE_VERSION=v1.1.0

# Build with overridden versions
docker buildx bake --push posts-service users-service
```

### Load versions from file
```bash
# Source the versions file
set -a
source versions.env
set +a

# Build with versions from file
docker buildx bake --push
```

## Multi-Architecture Support

All services are built for:
- `linux/amd64` (Intel/AMD 64-bit)
- `linux/arm64` (ARM 64-bit, Apple Silicon, AWS Graviton)

## Image Tags

Each service creates two tags:
1. Version-specific tag: `ambraj/{service}:v1.0.0`
2. Latest tag: `ambraj/{service}:latest`

## CI/CD Integration

The GitHub workflow uses this configuration and can be optimized to:
1. Detect which services changed (code or version)
2. Build only the changed services
3. Deploy only the updated services

### Manual Selective Build in CI/CD

To build specific services in the workflow, you can modify the targets parameter:

```yaml
- name: Build and Push Multi-arch Docker images
  uses: docker/bake-action@v6
  with:
    files: infrastructure/docker/docker-bake.hcl
    targets: posts-service,users-service  # Only build these services
    push: true
```

## Best Practices

1. **Single Place to Update**: Always update versions in `versions.env` - don't modify `docker-bake.hcl` directly
2. **Semantic Versioning**: Use semantic versioning (v1.0.0, v1.0.1, v1.1.0, v2.0.0)
3. **Commit Messages**: Include version change in commit message (e.g., "Bump posts-service to v1.0.2")
4. **Testing**: Test builds locally before pushing to avoid unnecessary CI/CD runs

## Troubleshooting

### Variables not being picked up
Make sure to source the versions.env file before building:
```bash
set -a && source versions.env && set +a
```

### Multi-arch build issues
Ensure Docker Buildx is properly set up:
```bash
docker buildx create --use
docker buildx inspect --bootstrap
```

# Config Server Setup for Kubernetes

## Problem
Config server in K8s cannot authenticate to the private GitHub repository because the token is expired.

## Solution

### Step 1: Create a new GitHub Personal Access Token

1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Give it a name: `config-server-k8s`
4. Select scopes: `repo` (full control of private repositories)
5. Click "Generate token"
6. **Copy the token immediately** (you won't be able to see it again)

### Step 2: Create Kubernetes Secret

```bash
# Option A: Create secret directly (token will be visible in command history)
kubectl create secret generic git-credentials \
  --from-literal=username=ambraj \
  --from-literal=password=YOUR_GITHUB_TOKEN_HERE \
  -n default

# Option B: Create from file (more secure)
# 1. Copy the template
cp infrastructure/k8s/git-credentials-secret.yml infrastructure/k8s/git-credentials-secret.yml

# 2. Edit the file and replace YOUR_GITHUB_USERNAME and YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
# 3. Apply it
kubectl apply -f infrastructure/k8s/git-credentials-secret.yml

# 4. Delete the file (it's in .gitignore but be safe)
rm infrastructure/k8s/git-credentials-secret.yml
```

### Step 3: Rebuild and Deploy Config Server

```bash
# Rebuild the Docker image
cd backend/config-server
docker build -t ambraj/config-server:v1.0.2 .
docker push ambraj/config-server:v1.0.2

# Update the image tag in config-server.yml
# Change: image: ambraj/config-server:v1.0.1
# To:     image: ambraj/config-server:v1.0.2

# Apply the updated deployment
kubectl apply -f infrastructure/k8s/config-server.yml

# Verify config server can now access the repo
kubectl logs -f -l app=config-server
```

### Step 4: Restart API Gateway

```bash
kubectl rollout restart deployment api-gateway -n default
kubectl logs -f -l app=api-gateway
```

## Verification

Test that config server can fetch configurations:

```bash
kubectl run test-curl --image=curlimages/curl:latest --rm -i --restart=Never -- \
  curl -s http://config-server:8888/api-gateway/default
```

You should see configuration properties, not a 404 error.

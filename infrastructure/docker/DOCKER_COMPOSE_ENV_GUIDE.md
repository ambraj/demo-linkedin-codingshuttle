# How Docker Compose Uses .env Files

## 🎯 The Simple Answer

Docker Compose **automatically** reads `.env` files from the same directory as `docker-compose.yml`. No configuration needed!

---

## 📂 File Location

```
infrastructure/docker/
├── docker-compose.yml    ← Your compose file
└── .env                  ← Docker Compose reads THIS automatically
```

**Important:** The `.env` file MUST be in the same directory as `docker-compose.yml`!

---

## 🔄 How It Works - Step by Step

### Step 1: You Create .env File

```bash
cd infrastructure/docker
cat > .env << EOF
GIT_USERNAME=ambraj
GIT_PASSWORD=your_token_here
EOF
```

### Step 2: Docker Compose Reads It Automatically

When you run `docker-compose up`, Docker Compose:
1. Looks for `.env` in the current directory
2. Reads all `KEY=VALUE` pairs
3. Makes them available for variable substitution

### Step 3: Variables Are Substituted in docker-compose.yml

In your `docker-compose.yml`:

```yaml
config-server:
  image: ambraj/config-server:v1.0.2
  environment:
    - GIT_USERNAME=${GIT_USERNAME}  # ← Replaced with value from .env
    - GIT_PASSWORD=${GIT_PASSWORD}  # ← Replaced with value from .env
```

**What happens:**
- `${GIT_USERNAME}` becomes `ambraj`
- `${GIT_PASSWORD}` becomes `your_token_here`

### Step 4: Environment Variables Passed to Container

Docker runs the container with these environment variables set:
```bash
docker run ... \
  -e GIT_USERNAME=ambraj \
  -e GIT_PASSWORD=your_token_here \
  ambraj/config-server:v1.0.2
```

### Step 5: Spring Boot Inside Container Reads Them

Inside the container, Spring Boot's `application.yml` sees:
```yaml
git:
  username: ${GIT_USERNAME}  # Spring resolves from container env vars
  password: ${GIT_PASSWORD}  # Spring resolves from container env vars
```

---

## 🧪 Live Demonstration

Let me show you what Docker Compose actually does:

### Current docker-compose.yml Section:
```yaml
config-server:
  image: ambraj/config-server:v1.0.2
  container_name: config-server
  ports:
    - "8888:8888"
  environment:
    - GIT_USERNAME=${GIT_USERNAME}
    - GIT_PASSWORD=${GIT_PASSWORD}
  depends_on:
    - discovery-server
  networks:
    - linkedin-network
```

### With .env File:
```env
GIT_USERNAME=ambraj
GIT_PASSWORD=my_secret_token
```

### What Docker Actually Runs:
```yaml
config-server:
  image: ambraj/config-server:v1.0.2
  container_name: config-server
  ports:
    - "8888:8888"
  environment:
    - GIT_USERNAME=ambraj          # ← Substituted!
    - GIT_PASSWORD=my_secret_token  # ← Substituted!
  depends_on:
    - discovery-server
  networks:
    - linkedin-network
```

---

## 🔍 Testing Your Setup

### 1. Check if Docker Compose Can Read .env

```bash
cd infrastructure/docker

# This shows you what Docker Compose sees
docker-compose config
```

**Expected output for config-server:**
```yaml
config-server:
  container_name: config-server
  depends_on:
    discovery-server:
      condition: service_started
      required: true
  environment:
    GIT_PASSWORD: your_actual_token    # ← Should show your token
    GIT_USERNAME: ambraj               # ← Should show your username
  image: ambraj/config-server:v1.0.2
  # ... rest of config
```

### 2. Verify Variables Are Set in Container

```bash
# Start only config-server
docker-compose up -d config-server

# Check environment variables inside the container
docker exec config-server env | grep GIT_
```

**Expected output:**
```
GIT_USERNAME=ambraj
GIT_PASSWORD=your_actual_token
```

### 3. Check Container Logs

```bash
docker-compose logs config-server
```

**Should see:**
- Successful connection to Git repository
- NO authentication errors
- NO "401 Unauthorized" errors

---

## ⚙️ Docker Compose .env Features

### Priority Order (Highest to Lowest):

1. **Shell environment variables**
   ```bash
   GIT_USERNAME=shell_value docker-compose up
   ```

2. **.env file in same directory**
   ```bash
   # infrastructure/docker/.env
   GIT_USERNAME=env_file_value
   ```

3. **Default values in docker-compose.yml**
   ```yaml
   - GIT_USERNAME=${GIT_USERNAME:-default_value}
   ```

### Example with All Three:

```bash
# .env file
GIT_USERNAME=from_env_file

# Shell
export GIT_USERNAME=from_shell

# docker-compose.yml
- GIT_USERNAME=${GIT_USERNAME:-from_default}

# Result: "from_shell" (shell wins)
```

---

## 🚀 Complete Workflow

### Initial Setup:
```bash
cd infrastructure/docker

# Create .env from template
cp .env.example .env

# Edit with your credentials
nano .env
# or
vim .env
# or
code .env
```

### Run Services:
```bash
# All services
docker-compose up

# Specific service
docker-compose up config-server

# In background
docker-compose up -d
```

### Verify:
```bash
# Check config
docker-compose config | grep -A 5 config-server

# Check running container
docker exec config-server env | grep GIT_

# Check logs
docker-compose logs config-server
```

---

## 🔐 Security Notes

### ✅ What's Safe:

1. `.env.example` - Template with fake values (committed to git)
2. `docker-compose.yml` - Uses `${VARIABLES}` syntax (committed to git)

### ❌ What's Protected:

1. `.env` - Actual credentials (in .gitignore, NOT committed)
2. Container environment - Only visible inside container

### Best Practices:

```bash
# ✅ DO: Use .env file
GIT_PASSWORD=actual_secret

# ❌ DON'T: Hardcode in docker-compose.yml
environment:
  - GIT_PASSWORD=actual_secret  # NEVER DO THIS!

# ✅ DO: Use variable substitution
environment:
  - GIT_PASSWORD=${GIT_PASSWORD}
```

---

## 🐛 Troubleshooting

### Problem: Variables Not Substituted

**Symptom:**
```bash
docker-compose config
# Shows: GIT_USERNAME=${GIT_USERNAME}  ← Not replaced!
```

**Solutions:**
1. Check `.env` file exists in same directory as `docker-compose.yml`
   ```bash
   ls -la infrastructure/docker/.env
   ```

2. Check `.env` file format (no spaces around `=`)
   ```bash
   # ❌ Wrong
   GIT_USERNAME = ambraj
   
   # ✅ Correct
   GIT_USERNAME=ambraj
   ```

3. Check for BOM or special characters
   ```bash
   file infrastructure/docker/.env
   # Should show: ASCII text
   ```

### Problem: Permission Denied

**Symptom:**
```
Error: Cannot read .env file
```

**Solution:**
```bash
chmod 600 infrastructure/docker/.env
```

### Problem: Variables Empty in Container

**Check:**
```bash
# 1. Verify docker-compose sees them
docker-compose config | grep GIT_

# 2. Check container env
docker exec config-server env | grep GIT_

# 3. Check file encoding
cat infrastructure/docker/.env | od -c
```

---

## 📝 Quick Reference

| Action | Command |
|--------|---------|
| Create .env | `cp .env.example .env` |
| Edit .env | `nano .env` |
| Verify substitution | `docker-compose config` |
| Start services | `docker-compose up` |
| Check container env | `docker exec config-server env \| grep GIT_` |
| View logs | `docker-compose logs config-server` |
| Stop services | `docker-compose down` |

---

## 🎓 Key Takeaways

1. ✅ Docker Compose reads `.env` **automatically**
2. ✅ No code changes needed - it's built-in
3. ✅ Use `${VARIABLE}` syntax in docker-compose.yml
4. ✅ `.env` must be in same directory as docker-compose.yml
5. ✅ Variables are substituted before containers start
6. ✅ Inside container, they're regular environment variables
7. ✅ Spring Boot reads them like any other env vars
8. ✅ Never commit `.env` file to git

---

## 🔗 Related Files

- **docker-compose.yml** - Uses `${GIT_USERNAME}` and `${GIT_PASSWORD}`
- **.env** - Contains actual credentials (you create this)
- **.env.example** - Template (committed to git)
- **.gitignore** - Prevents committing .env files

Your setup is ready to use! Just add your credentials to `.env` and run `docker-compose up`! 🚀
# Git credentials for Config Server
# Replace with your actual credentials

GIT_USERNAME=ambraj
GIT_PASSWORD=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


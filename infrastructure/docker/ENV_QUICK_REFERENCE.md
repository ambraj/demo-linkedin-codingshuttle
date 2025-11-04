# Docker Compose .env File - Quick Reference

## 🎯 The One-Line Answer

**Docker Compose automatically reads the `.env` file from the same directory as `docker-compose.yml` and substitutes `${VARIABLES}` with their values.**

---

## 📋 Side-by-Side Comparison

### BEFORE (.env file + docker-compose.yml)

**File: `infrastructure/docker/.env`**
```dotenv
GIT_USERNAME=ambraj
GIT_PASSWORD=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
```

**File: `infrastructure/docker/docker-compose.yml`**
```yaml
config-server:
  image: ambraj/config-server:v1.0.2
  container_name: config-server
  environment:
    - GIT_USERNAME=${GIT_USERNAME}    # Variable placeholder
    - GIT_PASSWORD=${GIT_PASSWORD}    # Variable placeholder
```

### AFTER (What Docker actually runs)

**Command: `docker compose config`**
```yaml
config-server:
  image: ambraj/config-server:v1.0.2
  container_name: config-server
  environment:
    GIT_USERNAME: ambraj                                    # ← Substituted!
    GIT_PASSWORD: github_pat_11AA5VG6A0Ge6zE759xZYw...     # ← Substituted!
```

---

## 🔄 The Substitution Process

```
┌──────────────────────────────────────────────────────────┐
│ .env file contains:                                      │
│ GIT_USERNAME=ambraj                                      │
└───────────────────────────┬──────────────────────────────┘
                            ↓
┌──────────────────────────────────────────────────────────┐
│ docker-compose.yml contains:                             │
│ - GIT_USERNAME=${GIT_USERNAME}                           │
└───────────────────────────┬──────────────────────────────┘
                            ↓
                   Docker Compose runs
                            ↓
┌──────────────────────────────────────────────────────────┐
│ Container receives:                                      │
│ GIT_USERNAME=ambraj                                      │
└──────────────────────────────────────────────────────────┘
```

---

## ✅ Live Test Results

**Current .env file:**
```bash
$ cat infrastructure/docker/.env
GIT_USERNAME=ambraj
GIT_PASSWORD=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
```

**Docker Compose config output:**
```bash
$ docker compose config | grep -A 8 "config-server:"
config-server:
  container_name: config-server
  environment:
    GIT_PASSWORD: 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
    GIT_USERNAME: ambraj
  image: ambraj/config-server:v1.0.2
```

**✅ Working perfectly! Variables are substituted automatically.**

---

## 📝 To Use It

```bash
# 1. Make sure .env file exists
ls -la infrastructure/docker/.env

# 2. Check it has your credentials
cat infrastructure/docker/.env

# 3. Run Docker Compose
cd infrastructure/docker
docker compose up

# That's it! Docker Compose automatically:
# - Finds the .env file
# - Reads the variables
# - Substitutes them in docker-compose.yml
# - Passes them to containers
```

---

## 🔍 Verify It's Working

```bash
# Test 1: Check config substitution
docker compose config | grep GIT_

# Test 2: Start container and check environment
docker compose up -d config-server
docker exec config-server env | grep GIT_

# Test 3: Check application logs
docker compose logs config-server
```

---

## ⚠️ Important Notes

1. **File location**: `.env` MUST be in same directory as `docker-compose.yml`
2. **Format**: `KEY=VALUE` (no spaces around `=`)
3. **Automatic**: No configuration needed, Docker Compose does it automatically
4. **Priority**: `.env` file < shell environment variables
5. **Security**: `.env` is in `.gitignore`, never committed to git

---

## 🎓 Key Takeaway

You don't need to "load" the `.env` file. Docker Compose does it automatically when you run any `docker compose` command!

**Just create the file, and Docker Compose handles the rest.** 🚀


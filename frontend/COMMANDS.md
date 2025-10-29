# LinkedIn Clone - Command Reference

## 🚀 Development Commands

### Start Development Server

```bash
npm run dev
```

Starts Vite dev server on http://localhost:3000

### Build for Production

```bash
npm run build
```

Creates optimized production build in `dist/` folder

### Preview Production Build

```bash
npm run preview
```

Preview the production build locally

### Lint Code

```bash
npm run lint
```

Run ESLint to check for code quality issues

## 📦 Package Management

### Install Dependencies

```bash
npm install
```

### Install New Package

```bash
npm install package-name
```

### Install Dev Dependency

```bash
npm install -D package-name
```

### Update Dependencies

```bash
npm update
```

### Check for Outdated Packages

```bash
npm outdated
```

## 🧹 Maintenance Commands

### Clear Node Modules and Reinstall

```bash
rm -rf node_modules package-lock.json
npm install
```

### Fix Security Vulnerabilities

```bash
npm audit fix
```

### Clear Vite Cache

```bash
rm -rf node_modules/.vite
```

## 🔍 Debugging Commands

### Check for Unused Dependencies

```bash
npm prune
```

### Validate package.json

```bash
npm doctor
```

### View Dependency Tree

```bash
npm list
```

## 🏗️ Build Commands

### Build and Analyze Bundle

```bash
npm run build
ls -lh dist/
```

### Clean Build Directory

```bash
rm -rf dist
```

## 🧪 Testing (when tests are added)

```bash
# Run tests
npm test

# Run tests in watch mode
npm run test:watch

# Generate coverage report
npm run test:coverage
```

## 🚢 Deployment

### Build for Production

```bash
npm run build
```

### Deploy to Vercel (example)

```bash
npm install -g vercel
vercel --prod
```

### Deploy to Netlify (example)

```bash
npm install -g netlify-cli
netlify deploy --prod
```

## 📊 Performance

### Analyze Bundle Size

Add to package.json:

```json
{
  "scripts": {
    "analyze": "vite build --mode analyze"
  }
}
```

## 🔧 Environment Setup

### Development

```bash
cp .env.example .env
npm run dev
```

### Production

```bash
npm run build
npm run preview
```

## 🐛 Common Issues

### Port Already in Use

```bash
# Kill process on port 3000
lsof -ti:3000 | xargs kill -9

# Or use different port
npm run dev -- --port 3001
```

### Module Not Found

```bash
npm install
```

### Cache Issues

```bash
rm -rf node_modules/.vite
npm run dev
```

### Build Fails

```bash
npm run lint
npm run build
```

## 📱 Mobile Testing

### Test on Local Network

```bash
npm run dev -- --host
```

Then access via http://YOUR_IP:3000 on mobile

## 🔐 Backend Connection

### Check Backend Status

```bash
curl http://localhost:8080/api/v1/users/actuator/health
```

### Test API Gateway

```bash
curl http://localhost:8080/api/v1/
```

## 💾 Database

### Clear LocalStorage (in browser console)

```javascript
localStorage.clear();
```

### View Stored Token

```javascript
console.log(localStorage.getItem('token'));
```

## 🎨 Tailwind

### Generate Tailwind Config

```bash
npx tailwindcss init -p
```

### Rebuild Tailwind

```bash
npm run dev
```

## 📝 Git Commands

### Initial Commit

```bash
git add .
git commit -m "Initial commit: LinkedIn Clone frontend"
git push
```

### Create Feature Branch

```bash
git checkout -b feature/new-feature
```

## 🚀 Quick Start (Copy & Paste)

```bash
# First time setup
cd /Users/arajput/dev/IdeaProjects/CodingShuttle/linkedin-app/frontend
npm install
npm run dev

# Daily development
cd /Users/arajput/dev/IdeaProjects/CodingShuttle/linkedin-app/frontend
npm run dev

# Before committing
npm run lint
npm run build

# Deploy
npm run build
# Upload dist/ folder to hosting
```

## 🌐 Browser DevTools

### React DevTools

Install Chrome extension: "React Developer Tools"

### Network Tab

- Monitor API calls
- Check request/response
- Verify authentication headers

### Console

- Check for errors
- View logged data
- Test JavaScript

## 📚 Helpful NPM Scripts

Add these to `package.json` if needed:

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext js,jsx",
    "format": "prettier --write \"src/**/*.{js,jsx,json,css,md}\"",
    "clean": "rm -rf dist node_modules",
    "reinstall": "npm run clean && npm install"
  }
}
```

---

**Pro Tip:** Bookmark this file for quick reference during development!

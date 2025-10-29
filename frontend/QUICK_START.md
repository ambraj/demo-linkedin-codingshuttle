# LinkedIn Clone Frontend - Quick Start Guide

## 🚀 Quick Start

### 1. Install Dependencies

```bash
npm install
```

### 2. Start Development Server

```bash
npm run dev
```

The app will be available at `http://localhost:3000`

### 3. Make Sure Backend is Running

Ensure your backend API Gateway is running on `http://localhost:8080`

## 📋 Prerequisites

- Node.js 16 or higher
- npm or yarn
- Backend services running (API Gateway on port 8080)

## 🔑 First Steps

1. **Sign Up**: Create a new account at `/signup`
2. **Login**: Sign in with your credentials at `/login`
3. **Create Posts**: Share your first post on the feed
4. **Connect**: Search for users and send connection requests
5. **Notifications**: Check your notifications for activity

## 🎯 Features Overview

### Authentication

- ✅ User registration with validation
- ✅ JWT-based login
- ✅ Automatic token management
- ✅ Protected routes

### Posts & Feed

- ✅ Create posts with visibility controls
- ✅ View feed of posts
- ✅ Like posts
- ✅ Pagination/load more

### Connections

- ✅ Search for users
- ✅ Send connection requests
- ✅ Accept/reject requests
- ✅ View all connections

### Notifications

- ✅ Real-time notifications
- ✅ Mark as read
- ✅ Different notification types

### UI/UX

- ✅ Fully responsive design
- ✅ Mobile-friendly navigation
- ✅ Loading states
- ✅ Error handling
- ✅ Toast notifications

## 🛠️ Available Scripts

```bash
npm run dev      # Start development server (port 3000)
npm run build    # Build for production
npm run preview  # Preview production build
npm run lint     # Run ESLint
```

## 🔧 Configuration

### API Proxy (Vite)

The frontend proxies API requests to avoid CORS:

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- Proxy: `/api` → `http://localhost:8080`

### Environment Variables (Optional)

If you need custom configuration, create a `.env` file:

```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

## 📱 Responsive Breakpoints

- Mobile: < 768px
- Tablet: 768px - 1024px
- Desktop: > 1024px

## 🎨 Color Scheme

- Primary Blue: `#0A66C2` (LinkedIn blue)
- Background: `#f3f2ef`
- White cards: `#ffffff`
- Text: `#000000`, `#666666`

## 🐛 Common Issues

### Issue: Backend not accessible

**Solution**: Ensure API Gateway is running on port 8080

### Issue: CORS errors

**Solution**: Vite proxy should handle this. Check `vite.config.js`

### Issue: Login not working

**Solution**: Clear localStorage and cookies, then try again

### Issue: Styles not loading

**Solution**: Run `npm install` again and restart dev server

## 📂 Project Structure

```
src/
├── components/         # Reusable UI components
├── context/           # React Context (Auth)
├── pages/             # Route pages
├── services/          # API services
├── App.jsx            # Main app with routing
├── main.jsx           # Entry point
└── index.css          # Global styles
```

## 🔐 Authentication Flow

1. User enters credentials on login page
2. Frontend sends POST to `/api/v1/users/auth/login`
3. Backend returns JWT token
4. Token stored in localStorage
5. Axios interceptor adds token to all requests
6. Protected routes check for token

## 🌐 API Endpoints Used

### Users Service

- `POST /api/v1/users/auth/signup` - Register
- `POST /api/v1/users/auth/login` - Login
- `GET /api/v1/users/core/me` - Get current user

### Posts Service

- `POST /api/v1/posts/core` - Create post
- `GET /api/v1/posts/core/feed` - Get feed
- `POST /api/v1/posts/core/{id}/like` - Like post

### Connections Service

- `GET /api/v1/connections/core/first-degree` - Get connections
- `POST /api/v1/connections/core/request/{userId}` - Send request
- `POST /api/v1/connections/core/accept/{userId}` - Accept request
- `POST /api/v1/connections/core/reject/{userId}` - Reject request
- `GET /api/v1/connections/core/pending` - Get pending requests
- `GET /api/v1/connections/core/search` - Search users

### Notifications Service

- `GET /api/v1/notifications/core` - Get notifications
- `PUT /api/v1/notifications/core/{id}/read` - Mark as read
- `PUT /api/v1/notifications/core/read-all` - Mark all as read

## 💡 Tips

1. **Mobile Testing**: Use browser DevTools to test responsive design
2. **Network Tab**: Monitor API calls in browser DevTools
3. **React DevTools**: Install React DevTools extension for debugging
4. **Hot Reload**: Vite provides instant hot module replacement

## 📞 Need Help?

Check the main `README.md` for comprehensive documentation.

---

Happy coding! 🚀

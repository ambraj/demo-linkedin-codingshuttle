# LinkedIn Clone - React Frontend

A modern, responsive React frontend for the LinkedIn Clone application, built with Vite, React Router, and Tailwind CSS.

## 🎯 Features

This frontend provides complete UI coverage for all backend microservices:

### ✅ Authentication & User Management

- User signup and login with JWT authentication
- Protected routes with automatic token management
- User profile viewing and management

### 📝 Posts & Feed

- Create posts with visibility controls (Public/Connections only)
- View personalized feed of posts
- Like posts with real-time updates
- Responsive post cards with user avatars

### 🤝 Connections & Networking

- Search for users across the platform
- Send connection requests
- Accept/reject pending connection requests
- View all your connections
- Tab-based interface for easy navigation

### 🔔 Notifications

- Real-time notification viewing
- Different notification types (posts, likes, connection requests)
- Mark notifications as read (individual or all)
- Unread notification indicators
- Icon-based notification categorization

## 🏗️ Tech Stack

- **React 18** - UI library
- **Vite** - Build tool and dev server
- **React Router v6** - Client-side routing
- **Axios** - HTTP client with interceptors
- **Tailwind CSS** - Utility-first styling
- **Lucide React** - Modern icon library
- **React Hot Toast** - Toast notifications

## 📂 Project Structure

```
frontend/
├── public/              # Static assets
├── src/
│   ├── components/      # Reusable UI components
│   │   ├── CreatePost.jsx
│   │   ├── Header.jsx
│   │   ├── Layout.jsx
│   │   ├── Loader.jsx
│   │   ├── PostCard.jsx
│   │   └── ProtectedRoute.jsx
│   ├── context/         # React context providers
│   │   └── AuthContext.jsx
│   ├── pages/          # Route-based page components
│   │   ├── Connections.jsx
│   │   ├── Feed.jsx
│   │   ├── Login.jsx
│   │   ├── Notifications.jsx
│   │   ├── Profile.jsx
│   │   └── Signup.jsx
│   ├── services/       # API service layer
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── connectionsService.js
│   │   ├── notificationsService.js
│   │   └── postsService.js
│   ├── App.jsx         # Main app component with routing
│   ├── main.jsx        # React entry point
│   └── index.css       # Global styles with Tailwind
├── index.html
├── package.json
├── vite.config.js
└── tailwind.config.js
```

## 🚀 Getting Started

### Prerequisites

- Node.js 16+ and npm/yarn
- Backend services running on port 8080 (API Gateway)

### Installation

1. Install dependencies:

```bash
npm install
```

2. Start the development server:

```bash
npm run dev
```

The app will open at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

Preview production build:

```bash
npm run preview
```

## 🔌 Backend Integration

The frontend connects to the backend via the API Gateway at `http://localhost:8080/api/v1`.

### API Proxy Configuration

Vite is configured to proxy API requests to avoid CORS issues during development:

```javascript
// vite.config.js
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

### Service Endpoints

All API services are centralized in the `src/services/` directory:

- **authService.js** - `/api/v1/users/auth/*` (signup, login, getCurrentUser)
- **postsService.js** - `/api/v1/posts/core/*` (create, feed, like)
- **connectionsService.js** - `/api/v1/connections/core/*` (connect, accept, reject, search)
- **notificationsService.js** - `/api/v1/notifications/core/*` (get, mark as read)

### Authentication Flow

1. User logs in via `/login`
2. JWT token stored in localStorage
3. Axios interceptor adds `Authorization: Bearer <token>` to all requests
4. On 401 response, user is redirected to login and tokens are cleared

## 🎨 UI/UX Features

### Responsive Design

- Mobile-first approach with Tailwind CSS
- Responsive navigation with mobile menu
- Adaptive layouts for all screen sizes
- Optimized for tablets and desktops

### User Experience

- Loading states for async operations
- Error handling with toast notifications
- Form validation
- Optimistic UI updates (e.g., like button)
- Smooth transitions and hover effects

### Design System

- LinkedIn-inspired color scheme (`#0A66C2` primary blue)
- Consistent spacing and typography
- Card-based layouts
- Custom scrollbar styling

## 📱 Pages & Routes

| Route            | Component     | Description          | Auth Required |
| ---------------- | ------------- | -------------------- | ------------- |
| `/login`         | Login         | User login form      | No            |
| `/signup`        | Signup        | User registration    | No            |
| `/feed`          | Feed          | Main feed with posts | Yes           |
| `/connections`   | Connections   | Network management   | Yes           |
| `/notifications` | Notifications | Notification center  | Yes           |
| `/profile`       | Profile       | User profile page    | Yes           |
| `/`              | -             | Redirects to `/feed` | -             |

## 🔐 Security

- JWT tokens stored securely in localStorage
- Automatic token injection via Axios interceptors
- Protected routes with authentication guards
- Automatic logout on token expiration (401 responses)
- XSS protection via React's built-in escaping

## 🧪 Development

### Available Scripts

```bash
npm run dev      # Start dev server
npm run build    # Build for production
npm run preview  # Preview production build
npm run lint     # Run ESLint
```

### Code Style

- ESLint configured for React best practices
- Prettier-compatible formatting
- Functional components with hooks
- PropTypes disabled (using TypeScript types would be better for larger projects)

## � Troubleshooting

### Backend Connection Issues

If you see CORS errors or connection refused:

1. Ensure backend API Gateway is running on port 8080
2. Check Vite proxy configuration in `vite.config.js`
3. Verify backend endpoints match the service layer

### Authentication Issues

If login doesn't work:

1. Check browser console for API errors
2. Verify backend auth endpoints are accessible
3. Clear localStorage and try again
4. Check JWT token format in backend response

### Build Issues

If Tailwind styles aren't working:

1. Run `npm install` to ensure PostCSS dependencies are installed
2. Check `tailwind.config.js` content paths
3. Verify `@tailwind` directives in `index.css`

## 🔄 Future Enhancements

Potential improvements:

- TypeScript migration for type safety
- Real-time updates with WebSockets
- Image upload for posts and profile pictures
- Infinite scroll for feed
- Search functionality
- Comments on posts
- Post sharing
- Rich text editor for posts
- Dark mode toggle
- User settings page
- Email notifications toggle

## � Backend Reference

For backend API documentation, service ports, and Kafka events, see the original backend documentation in this file's git history or the backend service READMEs.

**API Gateway:** `http://localhost:8080/api/v1`

**Key Services:**

- Users: `/api/v1/users/**`
- Posts: `/api/v1/posts/**`
- Connections: `/api/v1/connections/**`
- Notifications: `/api/v1/notifications/**`

---

Built with ❤️ using React, Vite, and Tailwind CSS

import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading, user } = useAuth();

  console.log('ProtectedRoute - Auth state:', { isAuthenticated, loading, user: !!user }); // Debug

  if (loading) {
    console.log('ProtectedRoute - Still loading, showing spinner'); // Debug
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-linkedin-blue"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    console.log('ProtectedRoute - Not authenticated, redirecting to login'); // Debug
    return <Navigate to="/login" replace />;
  }

  console.log('ProtectedRoute - Authenticated, rendering protected content'); // Debug
  return children;
};

export default ProtectedRoute;

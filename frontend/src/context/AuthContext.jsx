import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on mount
    const storedUser = authService.getStoredUser();
    const token = authService.getToken();
    
    console.log('AuthContext - Checking stored auth:', { storedUser, token: !!token }); // Debug
    
    if (storedUser && token) {
      console.log('AuthContext - Restoring user from localStorage'); // Debug
      setUser(storedUser);
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    const data = await authService.login(credentials);
    console.log('AuthContext - Login response data:', data);
    
    // authService.login now returns { token, user } or { token }
    // User is already stored in localStorage by authService
    if (data.user) {
      console.log('AuthContext - Setting user from response:', data.user);
      setUser(data.user);
    } else {
      // User will be loaded from localStorage or fetched on next reload
      const storedUser = authService.getStoredUser();
      console.log('AuthContext - Loading user from localStorage:', storedUser);
      if (storedUser) {
        setUser(storedUser);
      }
    }
    
    return data;
  };

  const signup = async (userData) => {
    const data = await authService.signup(userData);
    return data;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const value = {
    user,
    login,
    signup,
    logout,
    isAuthenticated: !!user,
    loading
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

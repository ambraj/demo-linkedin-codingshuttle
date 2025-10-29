import api from './api';

export const authService = {
  async signup(userData) {
    const response = await api.post('/users/auth/signup', userData);
    return response.data;
  },

  async login(credentials) {
    try {
      const response = await api.post('/users/auth/login', credentials);
      console.log('authService - Full login response:', response);
      console.log('authService - Response data:', response.data);

      const data = response.data;

      // Backend returns just the JWT token as a string
      let token;

      if (typeof data === 'string') {
        // Backend returns token directly as a string
        token = data;
        console.log('authService - Token received as string:', token);
      } else if (data.token) {
        // Backend returns object with token property
        token = data.token;
        console.log('authService - Token extracted from object:', token);
      }

      if (token) {
        localStorage.setItem('token', token);
        console.log('authService - Token saved to localStorage');

        // Decode JWT to extract user info (JWT format: header.payload.signature)
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          console.log('authService - Decoded JWT payload:', payload);

          // Create user object from JWT payload
          const user = {
            id: payload.sub,
            email: payload.email,
            // Add any other fields from your JWT payload
          };

          localStorage.setItem('user', JSON.stringify(user));
          console.log('authService - User created from JWT and saved:', user);
          return { token, user };
        } catch (decodeError) {
          console.error('authService - Failed to decode JWT:', decodeError);
          // Still return token, user will be null
          return { token };
        }
      } else {
        console.error('authService - No token in response!');
        throw new Error('No token received from server');
      }
    } catch (error) {
      console.error('authService - Login error:', error);
      throw error;
    }
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  async getCurrentUser() {
    const response = await api.get('/users/core/me');
    return response.data;
  },

  getStoredUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  getToken() {
    return localStorage.getItem('token');
  },
};

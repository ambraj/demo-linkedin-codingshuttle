import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // Redirect if already authenticated (for when user manually navigates to /login)
  useEffect(() => {
    if (isAuthenticated) {
      console.log('Already authenticated, redirecting to feed');
      navigate('/feed', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!email || !password) {
      toast.error('Please fill in all fields');
      return;
    }

    console.log('Login - Starting login process...'); // Debug
    setLoading(true);
    try {
      console.log('Login - Calling login function...'); // Debug
      const result = await login({ email, password });
      console.log('Login - Login completed, result:', result); // Debug log
      
      // Check what's actually in localStorage right after login
      const storedToken = localStorage.getItem('token');
      const storedUser = localStorage.getItem('user');
      console.log('Login - Token in localStorage:', storedToken); // Debug
      console.log('Login - User in localStorage:', storedUser); // Debug
      
      if (!storedToken) {
        console.error('Login - ERROR: Token not saved to localStorage!');
        toast.error('Login succeeded but session could not be saved. Please try again.');
        setLoading(false);
        return;
      }
      
      toast.success('Login successful!');
      
      // Small delay to ensure state is updated before navigation
      setTimeout(() => {
        console.log('Login - Navigating to feed'); // Debug
        navigate('/feed', { replace: true });
      }, 100);
    } catch (error) {
      console.error('Login - Login error:', error); // Debug log
      console.error('Login - Error response:', error.response); // Debug log
      toast.error(error.response?.data?.message || 'Login failed. Please try again.');
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-white flex items-center justify-center px-4">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-linkedin-blue mb-2">LinkedIn</h1>
          <p className="text-gray-600">Sign in to your account</p>
        </div>

        <div className="bg-white rounded-lg shadow-lg p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                Email address
              </label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-linkedin-blue focus:border-transparent outline-none transition"
                placeholder="you@example.com"
                required
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                Password
              </label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-linkedin-blue focus:border-transparent outline-none transition"
                placeholder="••••••••"
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-linkedin-blue text-white py-2.5 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-gray-600">
              Don't have an account?{' '}
              <Link to="/signup" className="text-linkedin-blue font-semibold hover:underline">
                Sign up
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;

import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Home, Users, Bell, LogOut, Menu, X } from 'lucide-react';
import { useState } from 'react';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="bg-white shadow-sm sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4">
        <div className="flex items-center justify-between h-14">
          {/* Logo */}
          <Link to="/feed" className="text-linkedin-blue font-bold text-xl">
            LinkedIn
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center space-x-6">
            <Link
              to="/feed"
              className="flex flex-col items-center text-gray-600 hover:text-linkedin-blue transition"
            >
              <Home size={20} />
              <span className="text-xs mt-1">Home</span>
            </Link>
            <Link
              to="/connections"
              className="flex flex-col items-center text-gray-600 hover:text-linkedin-blue transition"
            >
              <Users size={20} />
              <span className="text-xs mt-1">Network</span>
            </Link>
            <Link
              to="/notifications"
              className="flex flex-col items-center text-gray-600 hover:text-linkedin-blue transition"
            >
              <Bell size={20} />
              <span className="text-xs mt-1">Notifications</span>
            </Link>
          </nav>

          {/* User Menu */}
          <div className="hidden md:flex items-center space-x-4">
            <Link to="/profile" className="text-sm font-medium text-gray-700 hover:text-linkedin-blue">
              {user?.name}
            </Link>
            <button
              onClick={handleLogout}
              className="flex items-center text-gray-600 hover:text-red-600 transition"
            >
              <LogOut size={20} />
            </button>
          </div>

          {/* Mobile Menu Button */}
          <button
            className="md:hidden"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden py-4 border-t">
            <nav className="flex flex-col space-y-4">
              <Link
                to="/feed"
                className="flex items-center space-x-2 text-gray-700 hover:text-linkedin-blue"
                onClick={() => setMobileMenuOpen(false)}
              >
                <Home size={20} />
                <span>Home</span>
              </Link>
              <Link
                to="/connections"
                className="flex items-center space-x-2 text-gray-700 hover:text-linkedin-blue"
                onClick={() => setMobileMenuOpen(false)}
              >
                <Users size={20} />
                <span>Network</span>
              </Link>
              <Link
                to="/notifications"
                className="flex items-center space-x-2 text-gray-700 hover:text-linkedin-blue"
                onClick={() => setMobileMenuOpen(false)}
              >
                <Bell size={20} />
                <span>Notifications</span>
              </Link>
              <Link
                to="/profile"
                className="flex items-center space-x-2 text-gray-700 hover:text-linkedin-blue"
                onClick={() => setMobileMenuOpen(false)}
              >
                <span>{user?.name}</span>
              </Link>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-2 text-red-600 hover:text-red-700"
              >
                <LogOut size={20} />
                <span>Logout</span>
              </button>
            </nav>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;

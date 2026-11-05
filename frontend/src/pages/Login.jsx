import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import '../styles/login.css';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('citizen');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const user = await login(email, password, role);
      const routes = { admin: '/admin', volunteer: '/volunteer', citizen: '/citizen' };
      navigate(routes[user.role] || '/citizen');
    } catch (err) {
      setError(err.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-image-section">
        <div className="welcome-text">
          <h1>Disaster Relief Resource Management</h1>
          <p>Coordinate resources, shelters, and volunteers during emergencies</p>
        </div>
      </div>
      <div className="login-form-section">
        <div className="login-card">
          <h2>Sign In</h2>
          {error && <div className="error-message">{error}</div>}

          <div className="demo-credentials">
            <span className="demo-title">⚡ Quick Demo Login</span>
            <div className="demo-btn-group">
              <button
                type="button"
                className="demo-pill"
                onClick={() => { setEmail('admin@example.com'); setPassword('adminpass'); setRole('admin'); }}
              >
                👑 Admin
              </button>
              <button
                type="button"
                className="demo-pill"
                onClick={() => { setEmail('raj@example.com'); setPassword('volpass'); setRole('volunteer'); }}
              >
                🤝 Volunteer
              </button>
              <button
                type="button"
                className="demo-pill"
                onClick={() => { setEmail('deepa@example.com'); setPassword('deeppass'); setRole('citizen'); }}
              >
                👤 Citizen
              </button>
            </div>
          </div>

          <form onSubmit={handleSubmit}>
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <label htmlFor="role">Login As</label>
            <select id="role" value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="citizen">Citizen</option>
              <option value="volunteer">Volunteer</option>
              <option value="admin">Admin</option>
            </select>
            <button type="submit" className="login-button" disabled={loading}>
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>
          <p className="link-text">
            Don't have an account? <Link to="/register">Register</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;

import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import '../styles/Register.css';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    role: 'citizen',
    contact_number: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();
  const { register } = useAuth();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await register(formData);
      setSuccess('Registration successful! Redirecting to login...');
      setTimeout(() => navigate('/'), 1500);
    } catch (err) {
      setError(err.message || 'Registration failed.');
    }
  };

  return (
    <div className="register-container">
      <h2>Create Account</h2>
      <p className="register-subtitle">Join the disaster relief network today</p>
      <form onSubmit={handleSubmit}>
        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message" style={{
          background: 'rgba(34,197,94,0.12)', color: '#22c55e',
          border: '1px solid rgba(34,197,94,0.25)',
          padding: '0.625rem 0.875rem', borderRadius: '8px', fontSize: '0.8rem'
        }}>{success}</div>}

        <div>
          <label htmlFor="username">Username</label>
          <input id="username" name="username" value={formData.username}
                 onChange={handleChange} placeholder="Choose a username" required />
        </div>
        <div>
          <label htmlFor="reg-email">Email</label>
          <input id="reg-email" name="email" type="email" value={formData.email}
                 onChange={handleChange} placeholder="Enter your email" required />
        </div>
        <div>
          <label htmlFor="reg-password">Password</label>
          <input id="reg-password" name="password" type="password" value={formData.password}
                 onChange={handleChange} placeholder="Min. 6 characters" required minLength={6} />
        </div>
        <div>
          <label htmlFor="reg-role">Role</label>
          <select id="reg-role" name="role" value={formData.role} onChange={handleChange}>
            <option value="citizen">Citizen</option>
            <option value="volunteer">Volunteer</option>
          </select>
        </div>
        <div>
          <label htmlFor="contact">Contact Number</label>
          <input id="contact" name="contact_number" value={formData.contact_number}
                 onChange={handleChange} placeholder="Optional" />
        </div>
        <button type="submit">Register</button>
      </form>
      <p className="link-text">
        Already have an account? <Link to="/">Sign In</Link>
      </p>
    </div>
  );
};

export default Register;

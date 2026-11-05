import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { userService } from '../services/userService';
import '../styles/profile.css';

const ProfileSettings = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ username: '', email: '', contact_number: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const profile = await userService.getProfile(user.id);
        setFormData({
          username: profile.username || '',
          email: profile.email || '',
          contact_number: profile.contact_number || '',
        });
      } catch {
        setError('Failed to load profile.');
      } finally {
        setLoading(false);
      }
    };
    if (user?.id) loadProfile();
  }, [user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await userService.updateProfile(user.id, formData);
      setSuccess('Profile updated successfully!');
    } catch (err) {
      setError('Failed to update profile.');
    }
  };

  const getDashboardRoute = () => {
    const routes = { admin: '/admin', volunteer: '/volunteer', citizen: '/citizen' };
    return routes[user?.role] || '/';
  };

  if (loading) return <div className="profile-container"><div style={{ color: 'var(--text-muted)', padding: '3rem' }}>Loading...</div></div>;

  return (
    <div className="profile-container">
      <h2>Profile Settings</h2>

      <div className="profile-form">
        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message" style={{
          background: 'rgba(34,197,94,0.12)', color: '#22c55e',
          border: '1px solid rgba(34,197,94,0.25)',
          padding: '0.625rem 0.875rem', borderRadius: '8px', fontSize: '0.8rem', marginBottom: '1rem'
        }}>{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="prof-username">Username</label>
            <input id="prof-username" name="username" value={formData.username} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label htmlFor="prof-email">Email</label>
            <input id="prof-email" name="email" type="email" value={formData.email} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label htmlFor="prof-contact">Contact Number</label>
            <input id="prof-contact" name="contact_number" value={formData.contact_number} onChange={handleChange} />
          </div>
          <div className="btn-row">
            <button type="button" className="btn-outline" onClick={() => navigate(getDashboardRoute())}>Cancel</button>
            <button type="submit" className="btn-primary">Save Changes</button>
          </div>
        </form>
      </div>

      <Link to={getDashboardRoute()} className="back-link">← Back to Dashboard</Link>
    </div>
  );
};

export default ProfileSettings;

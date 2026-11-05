import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import '../styles/user.css';

const UserDashboard = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const cards = [
    { title: 'Request Resources', desc: 'Submit a new request for relief resources such as food, water, or medical supplies.', path: '/create-request', icon: '📋' },
    { title: 'My Requests', desc: 'Track the status of your submitted requests and see updates from volunteers.', path: '/my-requests', icon: '📦' },
    { title: 'Find Resources', desc: 'Browse available relief resources across all locations.', path: '/find-resources', icon: '🔍' },
    { title: 'Shelters', desc: 'View nearby shelters, their capacity, and contact information.', path: '/shelters', icon: '🏠' },
    { title: 'Profile Settings', desc: 'Update your personal information and contact details.', path: '/profiles', icon: '⚙️' },
  ];

  return (
    <div className="dashboard-container">
      <button className="logout-btn" onClick={handleLogout}>⎋ Logout</button>

      <div className="dashboard-header">
        <p className="dashboard-greeting">👋 Welcome back,</p>
        <h1 className="dashboard-title">{user?.username || user?.email || 'Citizen'}</h1>
        <p className="dashboard-desc">What do you need help with today?</p>
      </div>

      <div className="dashboard-grid">
        {cards.map((card) => (
          <div key={card.path} className="dashboard-card" onClick={() => navigate(card.path)}>
            <div className="dashboard-icon-wrap">
              <span className="dashboard-icon">{card.icon}</span>
            </div>
            <h3>{card.title}</h3>
            <p className="dashboard-description">{card.desc}</p>
            <span className="dashboard-button">Open →</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserDashboard;

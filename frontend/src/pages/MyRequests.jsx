import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { requestService } from '../services/requestService';
import '../styles/MyRequests.css';

const MyRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadRequests = async () => {
      try {
        const data = await requestService.getMyRequests();
        setRequests(data || []);
      } catch {
        setError('Failed to load your requests.');
      } finally {
        setLoading(false);
      }
    };
    loadRequests();
  }, []);

  if (loading) return <div className="my-requests-container"><div className="loading-spinner">Loading...</div></div>;

  return (
    <div className="my-requests-container">
      <Link to="/citizen" className="back-link">← Back to Dashboard</Link>
      <h2>My Requests</h2>

      {error && <div className="error-message">{error}</div>}

      {requests.length === 0 ? (
        <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>
          You haven't submitted any requests yet.
        </p>
      ) : (
        <div className="requests-list">
          {requests.map(r => (
            <div key={r.id} className="request-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                <span style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--text-primary)' }}>Request #{r.id}</span>
                <span className={`status-badge status-${r.status}`}>{r.status}</span>
              </div>
              <p><b>Resource:</b> {r.resource}</p>
              <p><b>Location:</b> {r.location}</p>
              <p><b>Quantity:</b> {r.quantity_requested}</p>
              {r.remarks && <p><b>Remarks:</b> {r.remarks}</p>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MyRequests;

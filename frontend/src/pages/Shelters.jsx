import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { shelterService } from '../services/shelterService';
import '../styles/shelters.css';

const Shelters = () => {
  const [shelters, setShelters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        const data = await shelterService.getShelters();
        setShelters(data || []);
      } catch {
        setError('Failed to load shelters.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const getOccupancyClass = (occupancy, capacity) => {
    if (!capacity || capacity === 0) return '';
    const ratio = occupancy / capacity;
    if (ratio >= 0.9) return 'high';
    if (ratio >= 0.6) return 'medium';
    return '';
  };

  if (loading) return <div className="shelters-container"><div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '3rem' }}>Loading...</div></div>;

  return (
    <div className="shelters-container">
      <Link to="/citizen" className="back-link">← Back to Dashboard</Link>
      <h2>Relief Shelters</h2>

      {error && <div className="error-message">{error}</div>}

      {shelters.length === 0 ? (
        <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>
          No shelters available at this time.
        </p>
      ) : (
        <div className="shelters-grid">
          {shelters.map(s => (
            <div key={s.id} className="shelter-card">
              <h3>{s.name}</h3>
              <p><b>Location:</b> {s.location || '—'}</p>
              <p><b>Capacity:</b> {s.capacity}</p>
              <p><b>Current Occupancy:</b> {s.current_occupancy}</p>
              <p><b>Available Spots:</b> {(s.capacity || 0) - (s.current_occupancy || 0)}</p>
              {s.contact_number && <p><b>Contact:</b> {s.contact_number}</p>}
              <div className="occupancy-bar">
                <div
                  className={`occupancy-fill ${getOccupancyClass(s.current_occupancy, s.capacity)}`}
                  style={{ width: `${s.capacity ? Math.min((s.current_occupancy / s.capacity) * 100, 100) : 0}%` }}
                />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Shelters;

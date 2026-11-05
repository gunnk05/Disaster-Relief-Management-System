import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { resourceService } from '../services/resourceService';
import '../styles/Resources.css';
import '../styles/VolunteerDashboard.css';

const FindResources = () => {
  const [resources, setResources] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        const data = await resourceService.getResources();
        setResources(data || []);
        setFiltered(data || []);
      } catch {
        setError('Failed to load resources.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  useEffect(() => {
    let result = resources;
    if (searchTerm) {
      result = result.filter(r =>
        r.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (r.location_name || '').toLowerCase().includes(searchTerm.toLowerCase())
      );
    }
    if (typeFilter) {
      result = result.filter(r => r.type === typeFilter);
    }
    setFiltered(result);
  }, [searchTerm, typeFilter, resources]);

  if (loading) return <div className="resources-container"><div className="loading-spinner">Loading...</div></div>;

  return (
    <div className="resources-container">
      <Link to="/citizen" className="back-link">← Back to Dashboard</Link>
      <h2>Available Resources</h2>

      {error && <div className="error-message">{error}</div>}

      <div className="search-filters">
        <input
          type="text"
          placeholder="Search by name or location..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
          <option value="">All Types</option>
          <option value="food">Food</option>
          <option value="water">Water</option>
          <option value="medical">Medical</option>
          <option value="clothing">Clothing</option>
          <option value="other">Other</option>
        </select>
      </div>

      {filtered.length === 0 ? (
        <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>
          No resources match your search.
        </p>
      ) : (
        <div className="resources-grid">
          {filtered.map(r => (
            <div key={r.id} className="card resource-card">
              <div className="resource-header">
                <h3 style={{ fontSize: '0.95rem', fontWeight: 600 }}>{r.name}</h3>
                <span className={`resource-type type-${r.type}`}>{r.type}</span>
              </div>
              <p><b>Quantity:</b> {r.quantity} {r.unit}</p>
              <p><b>Location:</b> {r.location_name || '—'}</p>
              {r.last_updated && (
                <span className="resource-updated">
                  Updated: {new Date(r.last_updated).toLocaleDateString()}
                </span>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default FindResources;

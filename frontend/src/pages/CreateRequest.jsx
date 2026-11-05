import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { requestService } from '../services/requestService';
import { resourceService } from '../services/resourceService';
import { locationService } from '../services/locationService';
import '../styles/CreateRequest.css';

const CreateRequest = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [resources, setResources] = useState([]);
  const [locations, setLocations] = useState([]);
  const [formData, setFormData] = useState({
    resource_id: '',
    location_id: '',
    quantity_requested: 1,
    remarks: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDropdowns = async () => {
      try {
        const [res, locs] = await Promise.all([
          resourceService.getResources(),
          locationService.getLocations(),
        ]);
        setResources(res || []);
        setLocations(locs || []);
      } catch {
        setError('Failed to load resources or locations.');
      } finally {
        setLoading(false);
      }
    };
    loadDropdowns();
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const response = await requestService.createRequest(formData);
      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || 'Failed to submit request.');
      }
      setSuccess('Request submitted successfully!');
      setTimeout(() => navigate('/my-requests'), 1500);
    } catch (err) {
      setError(err.message || 'Submission failed.');
    }
  };

  if (loading) return <div className="create-request-container"><div className="loading-spinner">Loading...</div></div>;

  return (
    <div className="create-request-container">
      <Link to="/citizen" className="back-link">← Back to Dashboard</Link>
      <h2>Request Resources</h2>

      <div className="create-request-form">
        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message" style={{
          background: 'rgba(34,197,94,0.12)', color: '#22c55e',
          border: '1px solid rgba(34,197,94,0.25)',
          padding: '0.625rem 0.875rem', borderRadius: '8px', fontSize: '0.8rem', marginBottom: '1rem'
        }}>{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="resource_id">Resource</label>
            <select id="resource_id" name="resource_id" value={formData.resource_id} onChange={handleChange} required>
              <option value="">Select a resource</option>
              {resources.map(r => (
                <option key={r.id} value={r.id}>{r.name} ({r.quantity} {r.unit} available)</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="location_id">Location</label>
            <select id="location_id" name="location_id" value={formData.location_id} onChange={handleChange} required>
              <option value="">Select a location</option>
              {locations.map(l => (
                <option key={l.id} value={l.id}>{l.name} — {l.region}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="quantity_requested">Quantity</label>
            <input id="quantity_requested" name="quantity_requested" type="number" min="1"
                   value={formData.quantity_requested} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label htmlFor="remarks">Remarks (optional)</label>
            <textarea id="remarks" name="remarks" value={formData.remarks}
                      onChange={handleChange} placeholder="Describe your situation..." />
          </div>

          <button type="submit">Submit Request</button>
        </form>
      </div>
    </div>
  );
};

export default CreateRequest;

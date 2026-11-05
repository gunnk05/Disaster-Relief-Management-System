import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { resourceService } from '../services/resourceService';
import { locationService } from '../services/locationService';
import { shelterService } from '../services/shelterService';
import { requestService } from '../services/requestService';
import { userService } from '../services/userService';
import { auditService } from '../services/auditService';
import '../styles/AdminDashboard.css';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [activeTab, setActiveTab] = useState('requests');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState('add');
  const [editItem, setEditItem] = useState(null);
  const [locations, setLocations] = useState([]);

  const tabs = ['requests', 'resources', 'locations', 'shelters', 'users', 'audit'];

  const fetchData = async (tab) => {
    setLoading(true);
    setError('');
    setData([]);
    try {
      let result;
      switch (tab) {
        case 'requests': result = await requestService.getAdminRequests(); break;
        case 'resources':
          result = await resourceService.getResources();
          const locs = await locationService.getLocations();
          setLocations(Array.isArray(locs) ? locs : []);
          break;
        case 'locations': result = await locationService.getLocations(); break;
        case 'shelters':
          result = await shelterService.getShelters();
          const shelterLocs = await locationService.getLocations();
          setLocations(Array.isArray(shelterLocs) ? shelterLocs : []);
          break;
        case 'users': result = await userService.getAllUsers(); break;
        case 'audit': result = await auditService.getAuditLogs(); break;
        default: result = [];
      }
      setData(Array.isArray(result) ? result : []);
    } catch (err) {
      setError(err?.message || 'Failed to load data.');
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setSuccess('');
    setError('');
    fetchData(tab);
  };

  React.useEffect(() => { fetchData('requests'); }, []);

  const handleLogout = async () => { await logout(); navigate('/'); };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure?')) return;
    try {
      if (activeTab === 'resources') await resourceService.deleteResource(id);
      else if (activeTab === 'locations') await locationService.deleteLocation(id);
      else if (activeTab === 'shelters') await shelterService.deleteShelter(id);
      setSuccess('Deleted successfully.');
      fetchData(activeTab);
    } catch { setError('Delete failed.'); }
  };

  const handleStatusChange = async (requestId, status) => {
    try {
      await requestService.updateRequestStatus(requestId, status);
      setSuccess('Status updated.');
      fetchData('requests');
    } catch (err) { setError(err.message || 'Status update failed.'); }
  };

  const handleFormSubmit = async (formData) => {
    try {
      if (activeTab === 'resources') {
        if (modalMode === 'add') await resourceService.createResource(formData);
        else await resourceService.updateResource(editItem.id, formData);
      } else if (activeTab === 'locations') {
        if (modalMode === 'add') await locationService.createLocation(formData);
        else await locationService.updateLocation(editItem.id, formData);
      } else if (activeTab === 'shelters') {
        if (modalMode === 'add') await shelterService.createShelter(formData);
        else await shelterService.updateShelter(editItem.id, formData);
      }
      setSuccess(`${modalMode === 'add' ? 'Created' : 'Updated'} successfully.`);
      setShowModal(false);
      fetchData(activeTab);
    } catch { setError('Save failed.'); }
  };

  const openAdd = () => { setModalMode('add'); setEditItem(null); setShowModal(true); };
  const openEdit = (item) => { setModalMode('edit'); setEditItem(item); setShowModal(true); };

  return (
    <div className="admin-dashboard">
      <button className="logout-btn" onClick={handleLogout}>Logout</button>
      <h1>Admin Dashboard</h1>

      <div className="admin-tabs">
        {tabs.map(tab => (
          <button key={tab} className={activeTab === tab ? 'active' : ''} onClick={() => handleTabChange(tab)}>
            {tab.charAt(0).toUpperCase() + tab.slice(1)}
          </button>
        ))}
      </div>

      {success && <div className="success-message">{success}</div>}
      {error && <div className="error-banner">{error}</div>}

      <div className="admin-content">
        {['resources', 'locations', 'shelters'].includes(activeTab) && (
          <div className="admin-toolbar">
            <h2>{activeTab.charAt(0).toUpperCase() + activeTab.slice(1)}</h2>
            <button className="add-btn" onClick={openAdd}>+ Add</button>
          </div>
        )}

        {loading ? (
          <div className="loading-spinner">Loading...</div>
        ) : (
          <div className="table-wrapper">
            {activeTab === 'requests' && <RequestsTable data={data} onStatusChange={handleStatusChange} />}
            {activeTab === 'resources' && <ResourcesTable data={data} onEdit={openEdit} onDelete={handleDelete} />}
            {activeTab === 'locations' && <LocationsTable data={data} onEdit={openEdit} onDelete={handleDelete} />}
            {activeTab === 'shelters' && <SheltersTable data={data} onEdit={openEdit} onDelete={handleDelete} />}
            {activeTab === 'users' && <UsersTable data={data} />}
            {activeTab === 'audit' && <AuditTable data={data} />}
          </div>
        )}
      </div>

      {showModal && (
        <Modal
          tab={activeTab}
          mode={modalMode}
          item={editItem}
          locations={locations}
          onClose={() => setShowModal(false)}
          onSubmit={handleFormSubmit}
        />
      )}
    </div>
  );
};

/* ---------- Sub-components ---------- */

const RequestsTable = ({ data, onStatusChange }) => (
  <table>
    <thead><tr><th>ID</th><th>Citizen</th><th>Resource</th><th>Qty</th><th>Location</th><th>Status</th><th>Volunteer</th><th>Actions</th></tr></thead>
    <tbody>
      {data.map(r => (
        <tr key={r.request_id}>
          <td>{r.request_id}</td><td>{r.citizen}</td><td>{r.resource}</td>
          <td>{r.quantity_requested}</td><td>{r.location}</td>
          <td><span className={`status-badge status-${r.status}`}>{r.status}</span></td>
          <td>{r.volunteer || '—'}</td>
          <td className="actions-cell">
            {r.status === 'completed' && (
              <button className="verify-btn" onClick={() => onStatusChange(r.request_id, 'fulfilled')}>
                Fulfill
              </button>
            )}
            {r.status !== 'pending' && r.status !== 'fulfilled' && (
              <button className="edit-btn" onClick={() => onStatusChange(r.request_id, 'pending')}>
                Reset
              </button>
            )}
          </td>
        </tr>
      ))}
    </tbody>
  </table>
);

const ResourcesTable = ({ data, onEdit, onDelete }) => (
  <table>
    <thead><tr><th>ID</th><th>Name</th><th>Type</th><th>Qty</th><th>Unit</th><th>Location</th><th>Actions</th></tr></thead>
    <tbody>
      {data.map(r => (
        <tr key={r.id}><td>{r.id}</td><td>{r.name}</td><td>{r.type}</td><td>{r.quantity}</td><td>{r.unit}</td>
          <td>{r.location_name || '—'}</td>
          <td className="actions-cell">
            <button className="edit-btn" onClick={() => onEdit(r)}>Edit</button>
            <button className="delete-btn" onClick={() => onDelete(r.id)}>Delete</button>
          </td></tr>
      ))}
    </tbody>
  </table>
);

const LocationsTable = ({ data, onEdit, onDelete }) => (
  <table>
    <thead><tr><th>ID</th><th>Name</th><th>Region</th><th>Weather</th><th>Alert</th><th>Actions</th></tr></thead>
    <tbody>
      {data.map(l => (
        <tr key={l.id}><td>{l.id}</td><td>{l.name}</td><td>{l.region}</td>
          <td>{l.weather_condition || '—'}</td>
          <td><span className={`status-badge status-${l.weather_alert === 'none' ? 'completed' : l.weather_alert === 'red' ? 'pending' : 'assigned'}`}>{l.weather_alert}</span></td>
          <td className="actions-cell">
            <button className="edit-btn" onClick={() => onEdit(l)}>Edit</button>
            <button className="delete-btn" onClick={() => onDelete(l.id)}>Delete</button>
          </td></tr>
      ))}
    </tbody>
  </table>
);

const SheltersTable = ({ data, onEdit, onDelete }) => (
  <table>
    <thead><tr><th>ID</th><th>Name</th><th>Location</th><th>Capacity</th><th>Occupancy</th><th>Contact</th><th>Actions</th></tr></thead>
    <tbody>
      {data.map(s => (
        <tr key={s.id}><td>{s.id}</td><td>{s.name}</td><td>{s.location}</td>
          <td>{s.capacity}</td><td>{s.current_occupancy}</td><td>{s.contact_number || '—'}</td>
          <td className="actions-cell">
            <button className="edit-btn" onClick={() => onEdit(s)}>Edit</button>
            <button className="delete-btn" onClick={() => onDelete(s.id)}>Delete</button>
          </td></tr>
      ))}
    </tbody>
  </table>
);

const UsersTable = ({ data }) => (
  <table>
    <thead><tr><th>ID</th><th>Username</th><th>Email</th><th>Role</th><th>Contact</th></tr></thead>
    <tbody>
      {data.map(u => (
        <tr key={u.id}><td>{u.id}</td><td>{u.username}</td><td>{u.email}</td>
          <td><span className={`status-badge status-${u.role === 'admin' ? 'fulfilled' : u.role === 'volunteer' ? 'assigned' : 'completed'}`}>{u.role}</span></td>
          <td>{u.contact_number || '—'}</td></tr>
      ))}
    </tbody>
  </table>
);

const AuditTable = ({ data }) => {
  const list = Array.isArray(data) ? data : [];

  if (list.length === 0) {
    return <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>No audit logs recorded yet.</div>;
  }

  const formatTimestamp = (ts) => {
    if (!ts) return '—';
    try {
      const d = new Date(ts);
      return isNaN(d.getTime()) ? String(ts) : d.toLocaleString();
    } catch {
      return String(ts);
    }
  };

  const getPerformer = (performedBy) => {
    if (!performedBy) return '—';
    if (typeof performedBy === 'object') {
      return performedBy.username || performedBy.email || (performedBy.id ? `User #${performedBy.id}` : '—');
    }
    return String(performedBy);
  };

  return (
    <table>
      <thead><tr><th>ID</th><th>Action</th><th>Performed By</th><th>Timestamp</th></tr></thead>
      <tbody>
        {list.map((a, idx) => (
          <tr key={a.id || idx}>
            <td>{a.id ?? '—'}</td>
            <td>{a.action || '—'}</td>
            <td>{getPerformer(a.performed_by || a.performedBy)}</td>
            <td>{formatTimestamp(a.timestamp)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

const Modal = ({ tab, mode, item, locations, onClose, onSubmit }) => {
  const getInitialState = () => {
    if (mode === 'edit' && item) return { ...item };
    switch (tab) {
      case 'resources': return { name: '', type: 'food', quantity: 0, unit: '', location_id: '' };
      case 'locations': return { name: '', region: '', latitude: '', longitude: '', weather_condition: '', weather_alert: 'none' };
      case 'shelters': return { name: '', location_id: '', capacity: 0, current_occupancy: 0, contact_number: '' };
      default: return {};
    }
  };

  const [form, setForm] = React.useState(getInitialState);
  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>{mode === 'add' ? 'Add' : 'Edit'} {tab.slice(0, -1)}</h3>
        <form onSubmit={(e) => { e.preventDefault(); onSubmit(form); }}>
          {tab === 'resources' && (
            <>
              <div className="form-group"><label>Name</label><input name="name" value={form.name || ''} onChange={handleChange} required /></div>
              <div className="form-row">
                <div className="form-group"><label>Type</label>
                  <select name="type" value={form.type || ''} onChange={handleChange}>
                    <option value="food">Food</option><option value="water">Water</option>
                    <option value="medical">Medical</option><option value="clothing">Clothing</option>
                    <option value="other">Other</option>
                  </select>
                </div>
                <div className="form-group"><label>Quantity</label><input name="quantity" type="number" value={form.quantity || 0} onChange={handleChange} /></div>
              </div>
              <div className="form-row">
                <div className="form-group"><label>Unit</label><input name="unit" value={form.unit || ''} onChange={handleChange} /></div>
                <div className="form-group"><label>Location</label>
                  <select name="location_id" value={form.location_id || ''} onChange={handleChange}>
                    <option value="">Select</option>
                    {locations.map(l => <option key={l.id} value={l.id}>{l.name}</option>)}
                  </select>
                </div>
              </div>
            </>
          )}
          {tab === 'locations' && (
            <>
              <div className="form-row"><div className="form-group"><label>Name</label><input name="name" value={form.name || ''} onChange={handleChange} required /></div>
                <div className="form-group"><label>Region</label><input name="region" value={form.region || ''} onChange={handleChange} /></div></div>
              <div className="form-row"><div className="form-group"><label>Latitude</label><input name="latitude" type="number" step="0.000001" value={form.latitude || ''} onChange={handleChange} /></div>
                <div className="form-group"><label>Longitude</label><input name="longitude" type="number" step="0.000001" value={form.longitude || ''} onChange={handleChange} /></div></div>
              <div className="form-row"><div className="form-group"><label>Weather</label><input name="weather_condition" value={form.weather_condition || ''} onChange={handleChange} /></div>
                <div className="form-group"><label>Alert</label>
                  <select name="weather_alert" value={form.weather_alert || 'none'} onChange={handleChange}>
                    <option value="none">None</option><option value="yellow">Yellow</option>
                    <option value="orange">Orange</option><option value="red">Red</option>
                  </select></div></div>
            </>
          )}
          {tab === 'shelters' && (
            <>
              <div className="form-group"><label>Name</label><input name="name" value={form.name || ''} onChange={handleChange} required /></div>
              <div className="form-group"><label>Location</label>
                <select name="location_id" value={form.location_id || ''} onChange={handleChange} required>
                  <option value="">Select</option>
                  {locations.map(l => <option key={l.id} value={l.id}>{l.name}</option>)}
                </select></div>
              <div className="form-row"><div className="form-group"><label>Capacity</label><input name="capacity" type="number" value={form.capacity || 0} onChange={handleChange} /></div>
                <div className="form-group"><label>Occupancy</label><input name="current_occupancy" type="number" value={form.current_occupancy || 0} onChange={handleChange} /></div></div>
              <div className="form-group"><label>Contact</label><input name="contact_number" value={form.contact_number || ''} onChange={handleChange} /></div>
            </>
          )}
          <div className="modal-actions">
            <button type="button" className="cancel-btn" onClick={onClose}>Cancel</button>
            <button type="submit" className="save-btn">{mode === 'add' ? 'Create' : 'Save'}</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AdminDashboard;

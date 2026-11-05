import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { requestService } from '../services/requestService';
import '../styles/VolunteerDashboard.css';

const VolunteerDashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('pending');
  const [pendingRequests, setPendingRequests] = useState([]);
  const [myTasks, setMyTasks] = useState([]);
  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (activeTab === 'pending') loadPending();
    else if (activeTab === 'my-tasks') loadMyTasks();
    else if (activeTab === 'resources') loadResources();
  }, [activeTab]);

  const loadPending = async () => {
    setLoading(true);
    try { setPendingRequests(await requestService.getPendingRequests()); }
    catch { setError('Failed to load requests.'); }
    finally { setLoading(false); }
  };

  const loadMyTasks = async () => {
    setLoading(true);
    try { setMyTasks(await requestService.getMyTasks(user.id)); }
    catch { setError('Failed to load tasks.'); }
    finally { setLoading(false); }
  };

  const loadResources = async () => {
    setLoading(true);
    try { setResources(await requestService.getVolunteerResources()); }
    catch { setError('Failed to load resources.'); }
    finally { setLoading(false); }
  };

  const handleAssign = async (requestId) => {
    try {
      await requestService.assignTask(user.id, requestId);
      setSuccess('Task assigned!');
      loadPending();
    } catch (err) { setError(err.message); }
  };

  const handleComplete = async (requestId) => {
    try {
      await requestService.completeTask(requestId, user.id);
      setSuccess('Task completed! Awaiting admin verification.');
      loadMyTasks();
    } catch (err) { setError(err.message); }
  };

  const handleLogout = async () => { await logout(); navigate('/'); };

  return (
    <div className="volunteer-dashboard">
      <button className="logout-btn" onClick={handleLogout}>Logout</button>

      <div className="section-header">
        <h1 className="section-title" style={{ background: 'linear-gradient(135deg, #60a5fa, #a78bfa)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
          Volunteer Dashboard
        </h1>
        <div className="tab-buttons">
          <button className={activeTab === 'pending' ? 'active' : ''} onClick={() => { setActiveTab('pending'); setSuccess(''); setError(''); }}>Pending Requests</button>
          <button className={activeTab === 'my-tasks' ? 'active' : ''} onClick={() => { setActiveTab('my-tasks'); setSuccess(''); setError(''); }}>My Tasks</button>
          <button className={activeTab === 'resources' ? 'active' : ''} onClick={() => { setActiveTab('resources'); setSuccess(''); setError(''); }}>Resources</button>
          <button onClick={() => navigate('/profiles')}>Profile</button>
        </div>
      </div>

      {success && <div className="success-message" style={{ background: 'rgba(34,197,94,0.12)', color: '#22c55e', border: '1px solid rgba(34,197,94,0.25)', padding: '0.625rem 1rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.85rem' }}>{success}</div>}
      {error && <div className="error-message">{error}</div>}

      {loading ? <div className="loading-spinner">Loading...</div> : (
        <>
          {activeTab === 'pending' && (
            <div className="request-management">
              {pendingRequests.length === 0 ? (
                <div className="no-requests-message">No pending requests at this time.</div>
              ) : (
                <div className="card-grid">
                  {pendingRequests.map(r => (
                    <div key={r.request_id} className="card">
                      <div className="card-header">
                        <span className="status-badge status-pending">{r.status}</span>
                        <span className="request-time">{r.request_time ? new Date(r.request_time).toLocaleDateString() : ''}</span>
                      </div>
                      <p><b>Citizen:</b> {r.citizen}</p>
                      <p><b>Resource:</b> {r.resource}</p>
                      <p><b>Quantity:</b> {r.quantity_requested}</p>
                      <p><b>Location:</b> {r.location}</p>
                      {r.remarks && <p><b>Remarks:</b> {r.remarks}</p>}
                      <div className="card-buttons">
                        <button onClick={() => handleAssign(r.request_id)}>Accept Task</button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'my-tasks' && (
            <div className="request-management">
              {myTasks.length === 0 ? (
                <div className="no-requests-message">You haven't accepted any tasks yet.</div>
              ) : (
                <div className="card-grid">
                  {myTasks.map(t => (
                    <div key={t.id} className="card">
                      <div className="card-header">
                        <span className={`status-badge status-${t.status}`}>{t.status}</span>
                      </div>
                      <p><b>Citizen:</b> {t.citizen}</p>
                      <p><b>Resource:</b> {t.resource}</p>
                      <p><b>Quantity:</b> {t.quantity_requested}</p>
                      <p><b>Location:</b> {t.location}</p>
                      {t.remarks && <p><b>Remarks:</b> {t.remarks}</p>}
                      {t.status === 'assigned' && (
                        <div className="card-buttons">
                          <button className="complete-btn" onClick={() => handleComplete(t.request_id)}>Mark Complete</button>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'resources' && (
            <div className="resource-management">
              {resources.length === 0 ? (
                <div className="no-resources-message">No resources available.</div>
              ) : (
                <div className="resource-flex-container">
                  {resources.map(r => (
                    <div key={r.id} className="card resource-card">
                      <div className="resource-header">
                        <h3 style={{fontSize: '0.95rem'}}>{r.name}</h3>
                        <span className={`resource-type type-${r.type}`}>{r.type}</span>
                      </div>
                      <p><b>Quantity:</b> {r.quantity} {r.unit}</p>
                      <p><b>Location:</b> {r.location_name || '—'}</p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default VolunteerDashboard;

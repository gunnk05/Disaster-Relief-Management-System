import { API_URL, apiFetch } from '../utils/apiFetch';

export const requestService = {
  // Citizen
  async createRequest(data) {
    const response = await apiFetch(`${API_URL}/requests`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return response;
  },

  async getMyRequests() {
    const response = await apiFetch(`${API_URL}/requests/my`);
    return response.json();
  },

  // Volunteer
  async getPendingRequests() {
    const response = await apiFetch(`${API_URL}/volunteers/requests`);
    return response.json();
  },

  async assignTask(volunteerId, requestId) {
    const response = await apiFetch(`${API_URL}/volunteers/assign-task`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ volunteer_id: volunteerId, request_id: requestId }),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || data.error || 'Failed to assign task');
    return data;
  },

  async completeTask(requestId, volunteerId) {
    const response = await apiFetch(`${API_URL}/volunteers/complete-task`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ request_id: requestId, volunteer_id: volunteerId }),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || data.error || 'Failed to complete task');
    return data;
  },

  async getMyTasks(volunteerId) {
    const response = await apiFetch(`${API_URL}/volunteers/my-tasks?volunteer_id=${volunteerId}`);
    return response.json();
  },

  async getVolunteerResources() {
    const response = await apiFetch(`${API_URL}/volunteers/resources`);
    return response.json();
  },

  // Admin
  async getAdminRequests() {
    const response = await apiFetch(`${API_URL}/admin/requests`);
    return response.json();
  },

  async updateRequestStatus(requestId, status) {
    const response = await apiFetch(`${API_URL}/admin/requests/${requestId}/status`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status }),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || data.error || 'Failed to update status');
    return data;
  },
};

import { API_URL, apiFetch } from '../utils/apiFetch';

export const resourceService = {
  async getResources() {
    const response = await apiFetch(`${API_URL}/resources`);
    return response.json();
  },

  async createResource(data) {
    const response = await apiFetch(`${API_URL}/resources`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return response.json();
  },

  async updateResource(id, data) {
    const response = await apiFetch(`${API_URL}/resources/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return response.json();
  },

  async deleteResource(id) {
    const response = await apiFetch(`${API_URL}/resources/${id}`, { method: 'DELETE' });
    return response.json();
  },
};

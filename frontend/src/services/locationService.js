import { API_URL, apiFetch } from '../utils/apiFetch';

export const locationService = {
  async getLocations() {
    const response = await apiFetch(`${API_URL}/locations`);
    return response.json();
  },

  async createLocation(data) {
    const response = await apiFetch(`${API_URL}/locations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return response.json();
  },

  async updateLocation(id, data) {
    const response = await apiFetch(`${API_URL}/locations/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return response.json();
  },

  async deleteLocation(id) {
    const response = await apiFetch(`${API_URL}/locations/${id}`, { method: 'DELETE' });
    return response.json();
  },
};

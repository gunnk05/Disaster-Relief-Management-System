import { API_URL, apiFetch } from '../utils/apiFetch';

export const shelterService = {
  async getShelters() {
    const response = await apiFetch(`${API_URL}/shelters`);
    return response.json();
  },

  async createShelter(data) {
    const response = await apiFetch(`${API_URL}/shelters`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return response.json();
  },

  async updateShelter(id, data) {
    const response = await apiFetch(`${API_URL}/shelters/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return response.json();
  },

  async deleteShelter(id) {
    const response = await apiFetch(`${API_URL}/shelters/${id}`, { method: 'DELETE' });
    return response.json();
  },
};

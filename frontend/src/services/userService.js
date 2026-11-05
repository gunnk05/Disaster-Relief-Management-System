import { API_URL, apiFetch } from '../utils/apiFetch';

export const userService = {
  async getProfile(userId) {
    const response = await apiFetch(`${API_URL}/users/${userId}/profile`);
    return response.json();
  },

  async updateProfile(userId, data) {
    const response = await apiFetch(`${API_URL}/users/${userId}/profile`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return response.json();
  },

  async getAllUsers() {
    const response = await apiFetch(`${API_URL}/users`);
    return response.json();
  },
};

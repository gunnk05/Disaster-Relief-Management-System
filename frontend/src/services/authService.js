import { API_URL, apiFetch } from '../utils/apiFetch';

export const authService = {
  async login(email, password, role) {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password, role }),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || data.error || 'Login failed');
    return data;
  },

  async register({ username, email, password, role, contact_number }) {
    const response = await fetch(`${API_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password, role, contact_number }),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || data.error || 'Registration failed');
    return data;
  },

  async logout() {
    try {
      await apiFetch(`${API_URL}/auth/logout`, { method: 'POST' });
    } finally {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    }
  },
};

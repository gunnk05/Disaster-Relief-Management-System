/**
 * Centralized API fetch utility.
 *
 * Automatically attaches the JWT Bearer token from localStorage
 * and handles 401 responses by clearing the session.
 */
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export { API_URL };

export const apiFetch = async (url, options = {}) => {
  const token = localStorage.getItem('token');
  const headers = new Headers(options.headers || {});

  if (token) headers.set('Authorization', `Bearer ${token}`);

  const response = await fetch(url, { ...options, headers });

  if (response.status === 401) {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    if (window.location.pathname !== '/') window.location.assign('/');
  }

  return response;
};

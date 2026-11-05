import { API_URL, apiFetch } from '../utils/apiFetch';

export const auditService = {
  async getAuditLogs() {
    const response = await apiFetch(`${API_URL}/admin/audit-logs`);
    if (!response.ok) {
      throw new Error(`Failed to load audit logs: ${response.statusText}`);
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
  },

  async getResourceAuditLogs() {
    const response = await apiFetch(`${API_URL}/admin/resource-audit-logs`);
    if (!response.ok) {
      throw new Error(`Failed to load resource audit logs: ${response.statusText}`);
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
  },
};

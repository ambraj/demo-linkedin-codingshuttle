import api from './api';

export const connectionsService = {
  async getConnections() {
    const response = await api.get('/connections/core/first-degree');
    return response.data;
  },

  async sendConnectionRequest(userId) {
    const response = await api.post(`/connections/core/request/${userId}`);
    return response.data;
  },

  async acceptConnectionRequest(userId) {
    const response = await api.post(`/connections/core/accept/${userId}`);
    return response.data;
  },

  async rejectConnectionRequest(userId) {
    const response = await api.post(`/connections/core/reject/${userId}`);
    return response.data;
  },

  async getReceivedRequests() {
    const response = await api.get('/connections/core/received-requests');
    return response.data;
  },

  async getSentRequests() {
    const response = await api.get('/connections/core/sent-requests');
    return response.data;
  },

  async removeConnection(userId) {
    const response = await api.post(`/connections/core/remove-connection/${userId}`);
    return response.data;
  },

  // Note: Search endpoint not documented in backend-help.md
  async searchUsers(query) {
    const response = await api.get('/connections/core/search', {
      params: { query },
    });
    return response.data;
  },

  async getSuggestedConnections() {
    // Endpoint: /api/v1/connections/core/suggested-connections
    // Returns list of PersonDto (users suggested for connection)
    // Excludes: authenticated user, already connected users, users with pending requests
    const response = await api.get('/connections/core/suggested-connections');
    return response.data;
  },
};

import api from './api';

// Notification Service now exposes REST endpoints for retrieving notifications
// Endpoint: GET /api/v1/notifications/core/notifications
// Returns: 200 OK with list of NotificationDto (latest 20 notifications for authenticated user)
// Requires JWT authentication

export const notificationsService = {
  async getNotifications() {
    // Endpoint: /notification/core/notifications
    // Returns latest 20 notifications for authenticated user, ordered by creation time descending
    const response = await api.get('/notification/core/users/allNotifications');
    return response.data;
  },

  async markAsRead(notificationId) {
    // No REST endpoint available yet
    console.warn('Mark as read functionality not yet implemented');
    return { success: false };
  },

  async markAllAsRead() {
    // No REST endpoint available yet
    console.warn('Mark all as read functionality not yet implemented');
    return { success: false };
  },

  async getUnreadCount() {
    // No REST endpoint available yet
    console.warn('Get unread count functionality not yet implemented');
    return 0;
  },
};

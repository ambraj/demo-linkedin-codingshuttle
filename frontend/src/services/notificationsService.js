import api from './api';

// IMPORTANT: According to backend-help.md, the Notification Service is a Kafka consumer only.
// It does NOT expose any REST endpoints. All notifications are handled via Kafka events.
// This service returns mock data for now until a REST API is added to the backend.

export const notificationsService = {
  async getNotifications() {
    // Notification Service is event-driven (Kafka consumer only)
    // No REST endpoints available - returning empty array
    console.warn('Notification Service does not expose REST endpoints (Kafka consumer only)');
    return [];
  },

  async markAsRead(notificationId) {
    // No REST endpoint available
    console.warn('Notification Service does not expose REST endpoints (Kafka consumer only)');
    return { success: false };
  },

  async markAllAsRead() {
    // No REST endpoint available
    console.warn('Notification Service does not expose REST endpoints (Kafka consumer only)');
    return { success: false };
  },

  async getUnreadCount() {
    // No REST endpoint available
    console.warn('Notification Service does not expose REST endpoints (Kafka consumer only)');
    return 0;
  },
};

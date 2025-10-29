package com.codingshuttle.linkedin.notification_service.service;

import com.codingshuttle.linkedin.notification_service.entity.Notification;
import com.codingshuttle.linkedin.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendNotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotification(Long userId, String message) {
        log.info("Sending notification to user {}", userId);
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotificationsForUser(Long userId) {
        log.info("Fetching latest 20 notifications for user {}", userId);
        return notificationRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId);
    }

}

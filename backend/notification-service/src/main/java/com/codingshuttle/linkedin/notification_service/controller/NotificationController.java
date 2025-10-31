package com.codingshuttle.linkedin.notification_service.controller;

import com.codingshuttle.linkedin.notification_service.auth.UserContextHolder;
import com.codingshuttle.linkedin.notification_service.dto.NotificationDto;
import com.codingshuttle.linkedin.notification_service.entity.Notification;
import com.codingshuttle.linkedin.notification_service.service.SendNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final SendNotificationService sendNotificationService;
    private final ModelMapper modelMapper;

    @GetMapping("/users/allNotifications")
    public ResponseEntity<List<NotificationDto>> getAllNotificationsForUser() {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Received request to get all notifications for user: {}", userId);

        List<Notification> notifications = sendNotificationService.getAllNotificationsForUser(userId);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .toList();

        return ResponseEntity.ok(notificationDtos);
    }
}


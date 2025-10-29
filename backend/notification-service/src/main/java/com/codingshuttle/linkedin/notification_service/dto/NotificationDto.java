package com.codingshuttle.linkedin.notification_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private Long userId;
    private String message;
    private LocalDateTime createdAt;
}


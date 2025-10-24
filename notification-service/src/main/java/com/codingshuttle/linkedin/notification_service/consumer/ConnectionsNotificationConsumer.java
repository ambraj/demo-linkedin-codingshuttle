package com.codingshuttle.linkedin.notification_service.consumer;

import com.codingshuttle.linkedin.event.AcceptConnectionRequestEvent;
import com.codingshuttle.linkedin.event.SendConnectionRequestEvent;
import com.codingshuttle.linkedin.notification_service.service.SendNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionsNotificationConsumer {

    public static final String SEND_CONNECTION_REQUEST_TOPIC = "send-connection-request-topic";
    public static final String ACCEPT_CONNECTION_REQUEST_TOPIC = "accept-connection-request-topic";

    private final SendNotificationService sendNotificationService;

    @KafkaListener(topics = SEND_CONNECTION_REQUEST_TOPIC)
    public void sendConnectionRequestNotification(SendConnectionRequestEvent event) {
        log.info("Received send connection request event: {}", event);
        sendNotificationService.sendNotification(event.getReceiverId(),
                "You have received a connection request from user: " + event.getSenderId());
    }

    @KafkaListener(topics = ACCEPT_CONNECTION_REQUEST_TOPIC)
    public void acceptConnectionRequestNotification(AcceptConnectionRequestEvent event) {
        log.info("Received accept connection request event: {}", event);
        sendNotificationService.sendNotification(event.getSenderId(),
                "Your connection request has been accepted by user: " + event.getReceiverId());
    }
}

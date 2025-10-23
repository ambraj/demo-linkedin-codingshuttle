package com.codingshuttle.linkedin.notification_service.consumer;

import com.codingshuttle.linkedin.event.PostCreatedEvent;
import com.codingshuttle.linkedin.event.PostLikedEvent;
import com.codingshuttle.linkedin.notification_service.client.ConnectionsClient;
import com.codingshuttle.linkedin.notification_service.dto.PersonDto;
import com.codingshuttle.linkedin.notification_service.entity.Notification;
import com.codingshuttle.linkedin.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostNotificationConsumer {

    private final ConnectionsClient connectionsClient;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "post-created-topic")
    public void handlePostCreated(PostCreatedEvent event) {
        log.info("Received post created notification: Post {} was created by user {}",
                event.getPostId(), event.getCreatorId());

        List<PersonDto> connections = connectionsClient.getFirstDegreeConnections(event.getCreatorId());
        for (PersonDto connection : connections) {
            String message = String.format("Your connection %s has created a new post. Check it out!", event.getCreatorId());
            sendNotification(connection.getUserId(), message);
        }
    }

    @KafkaListener(topics = "post-liked-topic")
    public void handlePostLiked(PostLikedEvent event) {
        log.info("Received post liked notification: Post {} was liked by user {}",
                event.getPostId(), event.getLikedByUserId());

        List<PersonDto> connections = connectionsClient.getFirstDegreeConnections(event.getCreatorId());
        for (PersonDto connection : connections) {
            String message = String.format("Your connection %s has liked your post %s.", event.getLikedByUserId(), event.getPostId());
            sendNotification(connection.getUserId(), message);
        }
    }

    private void sendNotification(Long userId, String message) {
        log.info("Sending notification to user {}", userId);
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}

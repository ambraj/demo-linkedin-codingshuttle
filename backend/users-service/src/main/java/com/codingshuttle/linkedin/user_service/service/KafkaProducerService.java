package com.codingshuttle.linkedin.user_service.service;

import com.codingshuttle.linkedin.user_service.config.KafkaTopicConfig;
import com.codingshuttle.linkedin.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<Long, UserCreatedEvent> kafkaTemplate;

    public void publishUserCreatedEvent(UserCreatedEvent event) {
        log.info("Publishing UserCreatedEvent: {}", event);
        kafkaTemplate.send(KafkaTopicConfig.USER_CREATED_TOPIC, event.getUserId(), event);
    }
}


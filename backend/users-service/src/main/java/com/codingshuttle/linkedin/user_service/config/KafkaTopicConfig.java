package com.codingshuttle.linkedin.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String USER_CREATED_TOPIC = "user-created-topic";

    @Bean
    public NewTopic userCreatedTopic() {
        return new NewTopic(USER_CREATED_TOPIC, 3, (short) 1);
    }
}


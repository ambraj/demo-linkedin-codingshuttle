package com.codingshuttle.linkedin.connections_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String SEND_CONNECTION_REQUEST_TOPIC = "send-connection-request-topic";
    public static final String ACCEPT_CONNECTION_REQUEST_TOPIC = "accept-connection-request-topic";

    @Bean
    public NewTopic sendConnectionRequestTopic() {
        return new NewTopic(SEND_CONNECTION_REQUEST_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic acceptConnectionRequestTopic() {
        return new NewTopic(ACCEPT_CONNECTION_REQUEST_TOPIC, 3, (short) 1);
    }

}

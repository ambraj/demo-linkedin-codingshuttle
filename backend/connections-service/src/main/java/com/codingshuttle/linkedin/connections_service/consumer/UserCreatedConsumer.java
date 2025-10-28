package com.codingshuttle.linkedin.connections_service.consumer;

import com.codingshuttle.linkedin.connections_service.config.KafkaTopicConfig;
import com.codingshuttle.linkedin.connections_service.entity.Person;
import com.codingshuttle.linkedin.connections_service.repository.PersonRepository;
import com.codingshuttle.linkedin.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCreatedConsumer {

    private final PersonRepository personRepository;

    @KafkaListener(topics = KafkaTopicConfig.USER_CREATED_TOPIC, groupId = "connections-service-group")
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent: {}", event);

        try {
            // Check if Person already exists (idempotency)
            if (personRepository.findByUserId(event.getUserId()).isPresent()) {
                log.warn("Person node already exists for userId: {}. Skipping creation.", event.getUserId());
                return;
            }

            // Create new Person node in Neo4j
            Person person = new Person();
            person.setUserId(event.getUserId());
            person.setName(event.getName());

            Person savedPerson = personRepository.save(person);
            log.info("Successfully created Person node in Neo4j with userId: {} and neo4j id: {}",
                    savedPerson.getUserId(), savedPerson.getId());
        } catch (Exception e) {
            log.error("Error creating Person node for userId: {}", event.getUserId(), e);
            throw e;
        }
    }
}


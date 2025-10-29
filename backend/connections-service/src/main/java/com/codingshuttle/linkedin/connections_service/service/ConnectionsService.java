package com.codingshuttle.linkedin.connections_service.service;

import com.codingshuttle.linkedin.connections_service.auth.UserContextHolder;
import com.codingshuttle.linkedin.connections_service.config.KafkaTopicConfig;
import com.codingshuttle.linkedin.connections_service.dto.PersonDto;
import com.codingshuttle.linkedin.connections_service.entity.Person;
import com.codingshuttle.linkedin.connections_service.exception.BadRequestException;
import com.codingshuttle.linkedin.connections_service.exception.ResourceNotFoundException;
import com.codingshuttle.linkedin.event.AcceptConnectionRequestEvent;
import com.codingshuttle.linkedin.event.SendConnectionRequestEvent;
import com.codingshuttle.linkedin.connections_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {

    private final PersonRepository connectionsRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, SendConnectionRequestEvent> sendConnectionRequestKafkaTemplate;
    private final KafkaTemplate<Long, AcceptConnectionRequestEvent> acceptConnectionRequestKafkaTemplate;

    public List<PersonDto> getFirstDegreeConnections(Long userId) {
        log.info("Fetching first degree connections for user: {}", userId);
        List<Person> connections = connectionsRepository.getFirstDegreeConnections(userId);
        return connections.stream()
                .map(connection -> modelMapper.map(connection, PersonDto.class))
                .toList();
    }

    public boolean sendConnectionRequest(Long receiverId) {
        Long senderId = UserContextHolder.getCurrentUserId();
        log.info("Trying to send connection request to sender: {} -> receiver: {}", senderId, receiverId);

        if (Objects.equals(senderId, receiverId)) {
            log.error("Cannot send connection request to yourself: senderId={}, receiverId={}", senderId, receiverId);
            throw new BadRequestException("Cannot send connection request to yourself");
        }
        if (connectionsRepository.connectionRequestExists(senderId, receiverId)) {
            log.error("Connection request already exists: senderId={}, receiverId={}", senderId, receiverId);
            throw new BadRequestException("Connection request already exists");
        }
        if (connectionsRepository.alreadyConnected(senderId, receiverId)) {
            log.error("Users are already connected: senderId={}, receiverId={}", senderId, receiverId);
            throw new BadRequestException("Users are already connected");
        }
        connectionsRepository.addConnectionRequest(senderId, receiverId);
        log.info("Connection request sent successfully!");
        SendConnectionRequestEvent sendConnectionRequestEvent = SendConnectionRequestEvent.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        sendConnectionRequestKafkaTemplate.send(KafkaTopicConfig.SEND_CONNECTION_REQUEST_TOPIC, sendConnectionRequestEvent);
        return true;
    }

    public Boolean acceptConnectionRequest(Long senderId) {
        Long receiverId = UserContextHolder.getCurrentUserId();

        if (!connectionsRepository.connectionRequestExists(senderId, receiverId)) {
            log.error("Connection request does not exist: senderId={}, receiverId={}", senderId, receiverId);
            throw new ResourceNotFoundException("Connection request does not exist");
        }
        if (connectionsRepository.alreadyConnected(senderId, receiverId)) {
            log.error("Users are already connected: senderId={}, receiverId={}", senderId, receiverId);
            throw new BadRequestException("Users are already connected");
        }
        connectionsRepository.acceptConnectionRequest(senderId, receiverId);
        log.info("Connection request accepted successfully!");
        AcceptConnectionRequestEvent acceptConnectionRequestEvent =
                AcceptConnectionRequestEvent.builder().senderId(senderId).receiverId(receiverId).build();
        acceptConnectionRequestKafkaTemplate.send(KafkaTopicConfig.ACCEPT_CONNECTION_REQUEST_TOPIC, acceptConnectionRequestEvent);
        return true;
    }

    public Boolean rejectConnectionRequest(Long senderId) {
        Long receiverId = UserContextHolder.getCurrentUserId();

        if (!connectionsRepository.connectionRequestExists(senderId, receiverId)) {
            log.error("Connection request does not exist: senderId={}, receiverId={}", senderId, receiverId);
            throw new ResourceNotFoundException("Connection request does not exist");
        }
        connectionsRepository.rejectConnectionRequest(senderId, receiverId);
        log.info("Connection request rejected successfully!");
        return true;
    }

    public List<PersonDto> getReceivedConnectionRequests() {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Fetching pending connection requests for user: {}", userId);

        List<Person> pendingRequests = connectionsRepository.getPendingConnectionRequests(userId);
        return pendingRequests.stream()
                .map(person -> modelMapper.map(person, PersonDto.class))
                .toList();
    }

    public List<PersonDto> getSentConnectionRequests() {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Fetching sent connection requests for user: {}", userId);

        List<Person> sentRequests = connectionsRepository.getSentConnectionRequests(userId);
        return sentRequests.stream()
                .map(person -> modelMapper.map(person, PersonDto.class))
                .toList();
    }

    public Boolean removeConnection(Long userId) {
        Long currentUserId = UserContextHolder.getCurrentUserId();
        log.info("Trying to remove connection between user: {} and user: {}", currentUserId, userId);

        if (Objects.equals(currentUserId, userId)) {
            log.error("Cannot remove connection with yourself: currentUserId={}, userId={}", currentUserId, userId);
            throw new BadRequestException("Cannot remove connection with yourself");
        }
        if (!connectionsRepository.alreadyConnected(currentUserId, userId)) {
            log.error("Connection does not exist: currentUserId={}, userId={}", currentUserId, userId);
            throw new ResourceNotFoundException("Connection does not exist");
        }
        connectionsRepository.removeConnection(currentUserId, userId);
        log.info("Connection removed successfully!");
        return true;
    }

    public List<PersonDto> getSuggestedConnections() {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Fetching suggested connections for user: {}", userId);

        List<Person> suggestedConnections = connectionsRepository.getSuggestedConnections(userId);
        return suggestedConnections.stream()
                .map(person -> modelMapper.map(person, PersonDto.class))
                .toList();
    }
}

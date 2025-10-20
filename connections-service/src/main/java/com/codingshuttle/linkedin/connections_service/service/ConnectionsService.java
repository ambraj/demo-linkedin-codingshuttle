package com.codingshuttle.linkedin.connections_service.service;

import com.codingshuttle.linkedin.connections_service.entity.Person;
import com.codingshuttle.linkedin.connections_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {

    private final PersonRepository connectionsRepository;

    public List<Person> getFirstDegreeConnections(Long userId) {
        log.info("Fetching first degree connections for user: {}", userId);
        return connectionsRepository.getFirstDegreeConnections(userId);
    }
}

package com.codingshuttle.linkedin.connections_service.controller;

import com.codingshuttle.linkedin.connections_service.dto.PersonDto;
import com.codingshuttle.linkedin.connections_service.entity.Person;
import com.codingshuttle.linkedin.connections_service.service.ConnectionsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class ConnectionsController {

    private final ConnectionsService connectionsService;
    private final ModelMapper modelMapper;

    @GetMapping("/{userId}/first-degree")
    public ResponseEntity<List<PersonDto>> getFirstDegreeConnections(@PathVariable Long userId) {
        List<Person> connections = connectionsService.getFirstDegreeConnections(userId);
        List<PersonDto> connectionDtos = connections.stream()
                .map(connection -> modelMapper.map(connection, PersonDto.class))
                .toList();
        return ResponseEntity.ok(connectionDtos);
    }

}

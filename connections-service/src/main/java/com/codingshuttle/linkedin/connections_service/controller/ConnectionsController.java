package com.codingshuttle.linkedin.connections_service.controller;

import com.codingshuttle.linkedin.connections_service.dto.PersonDto;
import com.codingshuttle.linkedin.connections_service.service.ConnectionsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class ConnectionsController {

    private final ConnectionsService connectionsService;
    private final ModelMapper modelMapper;

    @GetMapping("/first-degree")
    public ResponseEntity<List<PersonDto>> getFirstDegreeConnections(@RequestHeader("X-User-Id") Long userId) {
        List<PersonDto> connections = connectionsService.getFirstDegreeConnections(userId);

        return ResponseEntity.ok(connections);
    }

}

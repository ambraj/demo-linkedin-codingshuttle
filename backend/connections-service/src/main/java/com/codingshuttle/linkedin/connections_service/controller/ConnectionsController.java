package com.codingshuttle.linkedin.connections_service.controller;

import com.codingshuttle.linkedin.connections_service.dto.PersonDto;
import com.codingshuttle.linkedin.connections_service.service.ConnectionsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/request/{userId}")
    public ResponseEntity<Boolean> sendConnectionRequest(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionsService.sendConnectionRequest(userId));
    }

    @PostMapping("/accept/{userId}")
    public ResponseEntity<Boolean> acceptConnectionRequest(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionsService.acceptConnectionRequest(userId));
    }

    @PostMapping("/reject/{userId}")
    public ResponseEntity<Boolean> rejectConnectionRequest(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionsService.rejectConnectionRequest(userId));
    }

    @GetMapping("/received-requests")
    public ResponseEntity<List<PersonDto>> getReceivedConnectionRequests() {
        List<PersonDto> pendingRequests = connectionsService.getReceivedConnectionRequests();
        return ResponseEntity.ok(pendingRequests);
    }

    @GetMapping("/sent-requests")
    public ResponseEntity<List<PersonDto>> getSentConnectionRequests() {
        List<PersonDto> sentRequests = connectionsService.getSentConnectionRequests();
        return ResponseEntity.ok(sentRequests);
    }

    @PostMapping("/remove-connection/{userId}")
    public ResponseEntity<Boolean> removeConnection(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionsService.removeConnection(userId));
    }

    @GetMapping("/suggested-connections")
    public ResponseEntity<List<PersonDto>> getSuggestedConnections() {
        List<PersonDto> suggestedConnections = connectionsService.getSuggestedConnections();
        return ResponseEntity.ok(suggestedConnections);
    }

}

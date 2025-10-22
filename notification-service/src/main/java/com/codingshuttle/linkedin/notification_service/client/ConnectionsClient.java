package com.codingshuttle.linkedin.notification_service.client;

import com.codingshuttle.linkedin.notification_service.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "connections-service", path = "/connections")
public interface ConnectionsClient {

    @GetMapping("/core/{userId}/first-degree")
    List<PersonDto> getFirstDegreeConnections(@PathVariable Long userId);
}

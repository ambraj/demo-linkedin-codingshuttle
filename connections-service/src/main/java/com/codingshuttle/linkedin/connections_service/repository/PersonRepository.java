package com.codingshuttle.linkedin.connections_service.repository;

import com.codingshuttle.linkedin.connections_service.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {

    List<Person> getFirstDegreeConnections(Long userId);

}

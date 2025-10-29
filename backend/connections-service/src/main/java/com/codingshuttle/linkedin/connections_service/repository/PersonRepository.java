package com.codingshuttle.linkedin.connections_service.repository;

import com.codingshuttle.linkedin.connections_service.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {

    Optional<Person> findByUserId(Long userId);

    @Query("MATCH (p1:Person)-[:CONNECTED_TO]-(p2:Person) " +
            "WHERE p1.userId = $userId " +
            "RETURN p2")
    List<Person> getFirstDegreeConnections(Long userId);

    @Query("MATCH (p1:Person)-[r:REQUESTED_TO]-(p2:Person) " +
            "WHERE p1.userId = $senderId AND p2.userId = $receiverId " +
            "RETURN count(r) > 0")
    boolean connectionRequestExists(Long senderId, Long receiverId);

    @Query("MATCH (p1:Person)-[r:CONNECTED_TO]-(p2:Person) " +
            "WHERE p1.userId = $senderId AND p2.userId = $receiverId " +
            "RETURN count(r) > 0")
    boolean alreadyConnected(Long senderId, Long receiverId);

    @Query("MATCH (p1:Person), (p2:Person) " +
            "WHERE p1.userId = $senderId AND p2.userId = $receiverId " +
            "CREATE (p1)-[:REQUESTED_TO]->(p2)")
    void addConnectionRequest(Long senderId, Long receiverId);

    @Query("MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person) " +
            "WHERE p1.userId = $senderId AND p2.userId = $receiverId " +
            "DELETE r " +
            "CREATE (p1)-[:CONNECTED_TO]->(p2)")
    void acceptConnectionRequest(Long senderId, Long receiverId);

    @Query("MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person) " +
            "WHERE p1.userId = $senderId AND p2.userId = $receiverId " +
            "DELETE r")
    void rejectConnectionRequest(Long senderId, Long receiverId);

    @Query("MATCH (p1:Person)-[:REQUESTED_TO]->(p2:Person) " +
            "WHERE p2.userId = $userId " +
            "RETURN p1")
    List<Person> getPendingConnectionRequests(Long userId);

    @Query("MATCH (p1:Person)-[:REQUESTED_TO]->(p2:Person) " +
            "WHERE p1.userId = $userId " +
            "RETURN p2")
    List<Person> getSentConnectionRequests(Long userId);

    @Query("MATCH (p1:Person)-[r:CONNECTED_TO]-(p2:Person) " +
            "WHERE p1.userId = $userId1 AND p2.userId = $userId2 " +
            "DELETE r")
    void removeConnection(Long userId1, Long userId2);

    @Query("MATCH (p1:Person {userId: $userId}) " +
            "MATCH (p2:Person) " +
            "WHERE p2.userId <> $userId " +
            "AND NOT (p1)-[:CONNECTED_TO]-(p2) " +
            "AND NOT (p1)-[:REQUESTED_TO]-(p2) " +
            "AND NOT (p2)-[:REQUESTED_TO]->(p1) " +
            "RETURN p2")
    List<Person> getSuggestedConnections(Long userId);
}

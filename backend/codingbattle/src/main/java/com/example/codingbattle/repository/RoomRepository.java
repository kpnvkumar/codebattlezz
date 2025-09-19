package com.example.codingbattle.repository;

import com.example.codingbattle.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {

    Optional<Room> findByRoomId(String roomId);

    boolean existsByRoomId(String roomId);

    List<Room> findByCreatedBy(String createdBy);

    List<Room> findByIsActiveTrue();

    @Query("{ 'createdAt': { '$gte': ?0 } }")
    List<Room> findRecentRooms(LocalDateTime since);

    @Query("{ 'roomId': ?0, 'isActive': true }")
    Optional<Room> findActiveRoomByRoomId(String roomId);

    void deleteByRoomId(String roomId);
}
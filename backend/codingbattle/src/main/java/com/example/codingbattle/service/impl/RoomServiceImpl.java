package com.example.codingbattle.service.impl;

import com.example.codingbattle.dto.CreateRoomRequest;
import com.example.codingbattle.exception.RoomNotFoundException;
import com.example.codingbattle.model.Room;
import com.example.codingbattle.repository.RoomRepository;
import com.example.codingbattle.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room createRoom(CreateRoomRequest request) {
        logger.info("Creating new room for user: {}", request.getCreatedBy());

        String roomId = generateUniqueRoomId();

        Room room = new Room();
        room.setRoomId(roomId);
        room.setQuestion(request.getQuestion());
        room.setTestCases(request.getTestCases());
        room.setCreatedBy(request.getCreatedBy());
        room.setDifficulty(request.getDifficulty());
        room.setActive(true);

        Room savedRoom = roomRepository.save(room);
        logger.info("Room created successfully with ID: {}", roomId);

        return savedRoom;
    }

    @Override
    public Room getRoomByRoomId(String roomId) {
        logger.debug("Fetching room with ID: {}", roomId);
        return roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    @Override
    public boolean validateRoomId(String roomId) {
        logger.debug("Validating room ID: {}", roomId);
        return roomRepository.existsByRoomId(roomId) &&
                roomRepository.findActiveRoomByRoomId(roomId).isPresent();
    }

    @Override
    public List<Room> getAllActiveRooms() {
        logger.debug("Fetching all active rooms");
        return roomRepository.findByIsActiveTrue();
    }

    @Override
    public Room deactivateRoom(String roomId) {
        logger.info("Deactivating room with ID: {}", roomId);
        Room room = getRoomByRoomId(roomId);
        room.setActive(false);
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(String roomId) {
        logger.info("Deleting room with ID: {}", roomId);
        if (!roomRepository.existsByRoomId(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        roomRepository.deleteByRoomId(roomId);
    }

    @Override
    public List<Room> getRoomsByCreator(String createdBy) {
        logger.debug("Fetching rooms created by: {}", createdBy);
        return roomRepository.findByCreatedBy(createdBy);
    }

    private String generateUniqueRoomId() {
        String roomId;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            roomId = generateShortId();
            attempts++;
            if (attempts > maxAttempts) {
                // Fallback to UUID if we can't generate a unique short ID
                roomId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                break;
            }
        } while (roomRepository.existsByRoomId(roomId));

        return roomId;
    }

    private String generateShortId() {
        // Generate a 6-character alphanumeric ID
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            result.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return result.toString();
    }
}

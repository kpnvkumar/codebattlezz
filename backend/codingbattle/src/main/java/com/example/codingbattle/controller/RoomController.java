package com.example.codingbattle.controller;

import com.example.codingbattle.dto.CreateRoomRequest;
import com.example.codingbattle.dto.JoinRoomRequest;
import com.example.codingbattle.dto.SuccessResponse;
import com.example.codingbattle.model.Room;
import com.example.codingbattle.service.RoomService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    private RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<Room>> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        logger.info("Creating room request from: {}", request.getCreatedBy());

        Room room = roomService.createRoom(request);
        SuccessResponse<Room> response = new SuccessResponse<>("Room created successfully", room);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/join")
    public ResponseEntity<SuccessResponse<Room>> joinRoom(@Valid @RequestBody JoinRoomRequest request) {
        logger.info("Join room request for room: {} by user: {}", request.getRoomId(), request.getParticipantName());

        if (!roomService.validateRoomId(request.getRoomId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SuccessResponse<>("Invalid room ID", null));
        }

        Room room = roomService.getRoomByRoomId(request.getRoomId());
        SuccessResponse<Room> response = new SuccessResponse<>("Successfully joined room", room);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<SuccessResponse<Room>> getRoomDetails(@PathVariable String roomId) {
        logger.info("Getting room details for: {}", roomId);

        Room room = roomService.getRoomByRoomId(roomId);
        SuccessResponse<Room> response = new SuccessResponse<>("Room details retrieved", room);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}/validate")
    public ResponseEntity<SuccessResponse<Map<String, Boolean>>> validateRoom(@PathVariable String roomId) {
        logger.info("Validating room: {}", roomId);

        boolean isValid = roomService.validateRoomId(roomId);
        Map<String, Boolean> result = Map.of("valid", isValid);

        SuccessResponse<Map<String, Boolean>> response = new SuccessResponse<>(
                isValid ? "Room is valid" : "Room is invalid",
                result
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<SuccessResponse<List<Room>>> getActiveRooms() {
        logger.info("Getting all active rooms");

        List<Room> rooms = roomService.getAllActiveRooms();
        SuccessResponse<List<Room>> response = new SuccessResponse<>("Active rooms retrieved", rooms);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-creator/{createdBy}")
    public ResponseEntity<SuccessResponse<List<Room>>> getRoomsByCreator(@PathVariable String createdBy) {
        logger.info("Getting rooms created by: {}", createdBy);

        List<Room> rooms = roomService.getRoomsByCreator(createdBy);
        SuccessResponse<List<Room>> response = new SuccessResponse<>("Rooms by creator retrieved", rooms);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{roomId}/deactivate")
    public ResponseEntity<SuccessResponse<Room>> deactivateRoom(@PathVariable String roomId) {
        logger.info("Deactivating room: {}", roomId);

        Room room = roomService.deactivateRoom(roomId);
        SuccessResponse<Room> response = new SuccessResponse<>("Room deactivated successfully", room);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<SuccessResponse<String>> deleteRoom(@PathVariable String roomId) {
        logger.info("Deleting room: {}", roomId);

        roomService.deleteRoom(roomId);
        SuccessResponse<String> response = new SuccessResponse<>("Room deleted successfully", roomId);

        return ResponseEntity.ok(response);
    }
}
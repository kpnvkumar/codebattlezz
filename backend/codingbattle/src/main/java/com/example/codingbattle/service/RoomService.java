package com.example.codingbattle.service;

import com.example.codingbattle.dto.CreateRoomRequest;
import com.example.codingbattle.model.Room;

import java.util.List;

public interface RoomService {
    Room createRoom(CreateRoomRequest request);
    Room getRoomByRoomId(String roomId);
    boolean validateRoomId(String roomId);
    List<Room> getAllActiveRooms();
    Room deactivateRoom(String roomId);
    void deleteRoom(String roomId);
    List<Room> getRoomsByCreator(String createdBy);
}

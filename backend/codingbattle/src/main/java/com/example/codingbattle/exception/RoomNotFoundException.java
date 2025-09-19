package com.example.codingbattle.exception;

public class RoomNotFoundException extends CodingBattleException {
    public RoomNotFoundException(String roomId) {
        super("Room not found with ID: " + roomId);
    }
}

package com.example.codingbattle.exception;

public class CodingBattleException extends RuntimeException {
    public CodingBattleException(String message) {
        super(message);
    }

    public CodingBattleException(String message, Throwable cause) {
        super(message, cause);
    }
}

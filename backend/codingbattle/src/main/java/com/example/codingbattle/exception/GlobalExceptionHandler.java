package com.example.codingbattle.exception;

import com.example.codingbattle.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoomNotFoundException(RoomNotFoundException ex, WebRequest request) {
        logger.error("Room not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "ROOM_NOT_FOUND",
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CodingBattleException.class)
    public ResponseEntity<ErrorResponse> handleCodingBattleException(CodingBattleException ex, WebRequest request) {
        logger.error("Coding battle error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "CODING_BATTLE_ERROR",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse error = new ErrorResponse(
                "Validation failed",
                errors.toString(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("Illegal argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "INVALID_ARGUMENT",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        ErrorResponse error = new ErrorResponse(
                "An unexpected error occurred",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
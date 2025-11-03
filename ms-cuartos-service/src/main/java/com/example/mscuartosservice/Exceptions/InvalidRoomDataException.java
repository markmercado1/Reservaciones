package com.example.mscuartosservice.Exceptions;

public class InvalidRoomDataException extends RuntimeException {
    public InvalidRoomDataException(String message) {
        super(message);
    }
}
package com.example.msreservaciones.Exceptions;

public class InvalidReservationDataException extends RuntimeException {
    public InvalidReservationDataException(String message) {
        super(message);
    }
}
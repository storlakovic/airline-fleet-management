package com.storlakovic.airlineoperationssimulator.aircraft.exceptions;

public class AircraftAlreadyExistsException extends RuntimeException {
    public AircraftAlreadyExistsException(String message) {
        super(message);
    }
}

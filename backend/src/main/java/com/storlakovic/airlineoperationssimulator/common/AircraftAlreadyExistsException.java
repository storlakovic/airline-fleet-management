package com.storlakovic.airlineoperationssimulator.common;

public class AircraftAlreadyExistsException extends RuntimeException {
    public AircraftAlreadyExistsException(String message) {
        super(message);
    }
}

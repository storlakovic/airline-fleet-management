package com.storlakovic.airlineoperationssimulator.common;

public class AircraftAlreadyAssignedException extends RuntimeException {
    public AircraftAlreadyAssignedException(String message) {
        super(message);
    }
}

package com.storlakovic.airlineoperationssimulator.aircraft.exceptions;

public class AircraftAlreadyAssignedException extends RuntimeException {
    public AircraftAlreadyAssignedException(String message) {
        super(message);
    }
}

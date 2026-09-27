package com.storlakovic.airlineoperationssimulator.flight.exceptions;

public class AircraftAssignmentNotAllowedException extends RuntimeException {
    public AircraftAssignmentNotAllowedException(String message) {
        super(message);
    }
}

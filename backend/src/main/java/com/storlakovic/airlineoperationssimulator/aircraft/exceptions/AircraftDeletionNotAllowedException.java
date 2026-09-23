package com.storlakovic.airlineoperationssimulator.aircraft.exceptions;

public class AircraftDeletionNotAllowedException extends RuntimeException {
    public AircraftDeletionNotAllowedException(String message) {
        super(message);
    }
}

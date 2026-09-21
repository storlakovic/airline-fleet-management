package com.storlakovic.airlineoperationssimulator.common;

public class AircraftDeletionNotAllowedException extends RuntimeException {
    public AircraftDeletionNotAllowedException(String message) {
        super(message);
    }
}

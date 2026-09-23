package com.storlakovic.airlineoperationssimulator.aircrafttype.exceptions;

public class AircraftTypeNotFoundException extends RuntimeException {
    public AircraftTypeNotFoundException(String message) {
        super(message);
    }
}

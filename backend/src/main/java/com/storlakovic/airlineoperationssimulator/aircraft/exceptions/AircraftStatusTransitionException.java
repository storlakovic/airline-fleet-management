package com.storlakovic.airlineoperationssimulator.aircraft.exceptions;

public class AircraftStatusTransitionException extends RuntimeException {
    public AircraftStatusTransitionException(String message) {
        super(message);
    }
}

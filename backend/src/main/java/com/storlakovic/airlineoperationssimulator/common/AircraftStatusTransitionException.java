package com.storlakovic.airlineoperationssimulator.common;

public class AircraftStatusTransitionException extends RuntimeException {
    public AircraftStatusTransitionException(String message) {
        super(message);
    }
}

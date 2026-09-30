package com.storlakovic.airlineoperationssimulator.flight.exceptions;

public class FlightUpdateNotAllowedException extends RuntimeException {
    public FlightUpdateNotAllowedException(String message) {
        super(message);
    }
}

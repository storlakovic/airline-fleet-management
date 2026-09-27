package com.storlakovic.airlineoperationssimulator.flight.exceptions;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(String message) {
        super(message);
    }
}

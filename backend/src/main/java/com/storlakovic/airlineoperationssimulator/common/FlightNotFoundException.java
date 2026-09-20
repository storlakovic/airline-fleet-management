package com.storlakovic.airlineoperationssimulator.common;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(String message) {
        super(message);
    }
}

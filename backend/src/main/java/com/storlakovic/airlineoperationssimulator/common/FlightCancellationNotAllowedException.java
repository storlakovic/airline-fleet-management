package com.storlakovic.airlineoperationssimulator.common;

public class FlightCancellationNotAllowedException extends RuntimeException {
    public FlightCancellationNotAllowedException(String message) {
        super(message);
    }
}

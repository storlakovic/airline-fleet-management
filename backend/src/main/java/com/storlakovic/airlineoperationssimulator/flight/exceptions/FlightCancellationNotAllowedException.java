package com.storlakovic.airlineoperationssimulator.flight.exceptions;

public class FlightCancellationNotAllowedException extends RuntimeException {
    public FlightCancellationNotAllowedException(String message) {
        super(message);
    }
}

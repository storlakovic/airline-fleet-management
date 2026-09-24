package com.storlakovic.airlineoperationssimulator.aircraft.exceptions;

public class AircraftNotOperationalException extends RuntimeException {
    public AircraftNotOperationalException(String message) {
        super(message);
    }
}

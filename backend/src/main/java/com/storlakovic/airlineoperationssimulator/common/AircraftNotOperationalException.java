package com.storlakovic.airlineoperationssimulator.common;

public class AircraftNotOperationalException extends RuntimeException {
    public AircraftNotOperationalException(String message) {
        super(message);
    }
}

package com.storlakovic.airlineoperationssimulator.common;

public class AirportNotFoundException extends RuntimeException {
    public AirportNotFoundException(String message) {
        super(message);
    }
}

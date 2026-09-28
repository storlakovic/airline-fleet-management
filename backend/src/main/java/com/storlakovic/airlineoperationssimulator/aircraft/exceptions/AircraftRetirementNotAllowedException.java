package com.storlakovic.airlineoperationssimulator.aircraft.exceptions;

public class AircraftRetirementNotAllowedException extends RuntimeException {
    public AircraftRetirementNotAllowedException(String message) {
        super(message);
    }
}

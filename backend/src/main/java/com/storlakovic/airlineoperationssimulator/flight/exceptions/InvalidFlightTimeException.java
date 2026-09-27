package com.storlakovic.airlineoperationssimulator.flight.exceptions;

public class InvalidFlightTimeException extends RuntimeException
{
    public InvalidFlightTimeException(String message) {
        super(message);
    }
}

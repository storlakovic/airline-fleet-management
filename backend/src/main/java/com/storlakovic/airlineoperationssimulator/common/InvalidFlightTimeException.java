package com.storlakovic.airlineoperationssimulator.common;

public class InvalidFlightTimeException extends RuntimeException
{
    public InvalidFlightTimeException(String message) {
        super(message);
    }
}

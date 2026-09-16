package com.storlakovic.airlineoperationssimulator.common;

public class RouteInvalidException extends RuntimeException {
    public RouteInvalidException(String message) {
        super(message);
    }
}

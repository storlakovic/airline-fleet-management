package com.storlakovic.airlineoperationssimulator.common;

public class RouteAlreadyExistsException extends RuntimeException {
    public RouteAlreadyExistsException(String message) {
        super(message);
    }
}

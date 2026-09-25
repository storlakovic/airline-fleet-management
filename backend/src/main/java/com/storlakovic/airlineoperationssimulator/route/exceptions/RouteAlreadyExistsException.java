package com.storlakovic.airlineoperationssimulator.route.exceptions;

public class RouteAlreadyExistsException extends RuntimeException {
    public RouteAlreadyExistsException(String message) {
        super(message);
    }
}

package com.storlakovic.airlineoperationssimulator.common;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String message) {
        super(message);
    }
}

package com.storlakovic.airlineoperationssimulator.route.dto;

import com.storlakovic.airlineoperationssimulator.route.Route;

public record RouteResponse(
        Long id,
        Long originAirportId,
        String originIcaoCode,
        Long destinationAirportId,
        String  destinationIcaoCode) {

    public static RouteResponse from(Route route) {
        return new RouteResponse(route.getId(), route.getOrigin().getId(), route.getOrigin().getIcaoCode(), route.getDestination().getId(), route.getDestination().getIcaoCode());
    }
}

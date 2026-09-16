package com.storlakovic.airlineoperationssimulator.route.dto;

public record RouteResponse(
        Long id,
        Long originAirportId,
        String originIcaoCode,
        Long destinationAirportId,
        String  destinationIcaoCode) {
}

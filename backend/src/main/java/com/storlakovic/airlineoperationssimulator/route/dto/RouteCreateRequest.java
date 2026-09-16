package com.storlakovic.airlineoperationssimulator.route.dto;

public class RouteCreateRequest {

    private final Long originAirportId;
    private final Long destinationAirportId;

    public RouteCreateRequest(Long originAirportId, Long destinationAirportId) {
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
    }

    public Long getOriginAirportId() {
        return originAirportId;
    }

    public Long getDestinationAirportId() {
        return destinationAirportId;
    }
}

package com.storlakovic.airlineoperationssimulator.flight.dto;

import java.time.OffsetDateTime;

public record FlightCreateRequest(String flightNumber, Long routeId, OffsetDateTime scheduledDepartureTime, OffsetDateTime scheduledArrivalTime) {

}

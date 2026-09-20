package com.storlakovic.airlineoperationssimulator.flight.dto;

import java.time.OffsetDateTime;

public record CreateFlightRequest(String flightNumber, Long routeId, OffsetDateTime scheduledDepartureTime, OffsetDateTime scheduledArrivalTime) {

}

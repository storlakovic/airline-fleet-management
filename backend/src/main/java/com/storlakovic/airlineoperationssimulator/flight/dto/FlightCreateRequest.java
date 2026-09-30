package com.storlakovic.airlineoperationssimulator.flight.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record FlightCreateRequest(@NotNull String flightNumber, @NotNull Long routeId, @NotNull OffsetDateTime scheduledDepartureTime, @NotNull OffsetDateTime scheduledArrivalTime) {

}

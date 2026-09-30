package com.storlakovic.airlineoperationssimulator.flight.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record FlightUpdateRequest(@NotNull OffsetDateTime scheduledDepartureTime,
                                 @NotNull OffsetDateTime scheduledArrivalTime) {

}

package com.storlakovic.airlineoperationssimulator.flight.dto;

import java.time.OffsetDateTime;

public record FlightUpdateRequest(OffsetDateTime scheduledDepartureTime,
                                 OffsetDateTime scheduledArrivalTime) {

}

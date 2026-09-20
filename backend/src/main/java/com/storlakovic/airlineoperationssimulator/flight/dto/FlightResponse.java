package com.storlakovic.airlineoperationssimulator.flight.dto;

import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;
import com.storlakovic.airlineoperationssimulator.route.dto.RouteResponse;

import java.time.OffsetDateTime;

public record FlightResponse(
        Long id,
        String flightNumber,
        RouteResponse route,
        OffsetDateTime scheduledDepartureTime,
        OffsetDateTime scheduledArrivalTime,
        FlightStatus status
) {}

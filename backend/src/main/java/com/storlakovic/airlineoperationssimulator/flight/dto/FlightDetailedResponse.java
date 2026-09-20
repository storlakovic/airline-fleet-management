package com.storlakovic.airlineoperationssimulator.flight.dto;

import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;

import java.time.OffsetDateTime;

public record FlightDetailedResponse(
        Long id,
        String flightNumber,
        String originIcaoCode,
        String originName,
        String destinationIcaoCode,
        String destinationName,
        OffsetDateTime scheduledDepartureTime,
        OffsetDateTime scheduledArrivalTime,
        OffsetDateTime actualDepartureTime,
        OffsetDateTime actualArrivalTime,
        FlightStatus status
) {}
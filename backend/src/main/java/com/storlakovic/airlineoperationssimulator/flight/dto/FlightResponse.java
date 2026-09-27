package com.storlakovic.airlineoperationssimulator.flight.dto;

import com.storlakovic.airlineoperationssimulator.aircraft.Aircraft;
import com.storlakovic.airlineoperationssimulator.flight.Flight;
import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;

import java.time.OffsetDateTime;

public record FlightResponse(
        Long id,
        String flightNumber,
        Long routeId,
        String originIcaoCode,
        String destinationIcaoCode,
        OffsetDateTime scheduledDepartureTime,
        OffsetDateTime scheduledArrivalTime,
        FlightStatus status,
        String aircraftIcaoCode,
        String aircraftRegistration
) {
    public static FlightResponse from(Flight flight) {
        Aircraft aircraft = flight.getAircraft();

        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getRoute().getId(),
                flight.getRoute().getOrigin().getIcaoCode(),
                flight.getRoute().getDestination().getIcaoCode(),
                flight.getScheduledDepartureTime(),
                flight.getScheduledArrivalTime(),
                flight.getStatus(),
                aircraft == null ? null : aircraft.getAircraftType().getIcaoCode(),
                aircraft == null ? null : aircraft.getRegistration()
        );
    }
}

package com.storlakovic.airlineoperationssimulator.flight.dto;

import com.storlakovic.airlineoperationssimulator.aircraft.Aircraft;
import com.storlakovic.airlineoperationssimulator.flight.Flight;
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
        FlightStatus status,
        String aircraftIcaoCode,
        String aircraftRegistration
) {
    public static FlightDetailedResponse from(Flight flight) {
        Aircraft aircraft = flight.getAircraft();
        return new FlightDetailedResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getRoute().getOrigin().getIcaoCode(),
                flight.getRoute().getOrigin().getName(),
                flight.getRoute().getDestination().getIcaoCode(),
                flight.getRoute().getDestination().getName(),
                flight.getScheduledDepartureTime(),
                flight.getScheduledArrivalTime(),
                flight.getActualDepartureTime(),
                flight.getActualArrivalTime(),
                flight.getStatus(),
                aircraft == null ? null : aircraft.getAircraftType().getIcaoCode(),
                aircraft == null ? null : aircraft.getRegistration()
        );
    }
}
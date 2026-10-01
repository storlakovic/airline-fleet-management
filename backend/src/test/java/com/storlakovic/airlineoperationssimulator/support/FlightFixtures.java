package com.storlakovic.airlineoperationssimulator.support;

import com.storlakovic.airlineoperationssimulator.aircraft.Aircraft;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import com.storlakovic.airlineoperationssimulator.airport.Airport;
import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;
import com.storlakovic.airlineoperationssimulator.flight.Flight;
import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;
import com.storlakovic.airlineoperationssimulator.route.Route;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;

/** Real domain objects with fixed times; reflection is limited to generated IDs. */
public final class FlightFixtures {
    public static final OffsetDateTime DEPARTURE = OffsetDateTime.parse("2026-10-01T10:00:00Z");
    public static final OffsetDateTime ARRIVAL = DEPARTURE.plusHours(3);

    private FlightFixtures() {}

    public static Route route() {
        Airport origin = new Airport("LOWW", "VIE", "Vienna Airport", "Vienna", "AT",
                48.11, 16.57, "large_airport", AirportStatus.OPERATIONAL);
        Airport destination = new Airport("KJFK", "JFK", "John F. Kennedy Airport", "New York", "US",
                40.64, -73.78, "large_airport", AirportStatus.OPERATIONAL);
        Route route = new Route(origin, destination);
        ReflectionTestUtils.setField(route, "id", 10L);
        return route;
    }

    public static Flight flight(FlightStatus status) {
        return flight(1L, status, DEPARTURE, ARRIVAL);
    }

    public static Flight flight(long id, FlightStatus status, OffsetDateTime departure, OffsetDateTime arrival) {
        Flight flight = new Flight("OS" + id, route(), departure, arrival);
        ReflectionTestUtils.setField(flight, "id", id);
        flight.setStatus(status);
        return flight;
    }

    public static Aircraft aircraft() {
        Aircraft aircraft = new Aircraft(new AircraftType("Airbus", "A320", "A320"), "OE-TEST");
        ReflectionTestUtils.setField(aircraft, "id", 5L);
        aircraft.changeStatus(AircraftStatus.IN_SERVICE);
        return aircraft;
    }
}

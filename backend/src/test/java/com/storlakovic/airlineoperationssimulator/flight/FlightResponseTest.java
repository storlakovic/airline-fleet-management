package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.flight.dto.FlightDetailedResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.storlakovic.airlineoperationssimulator.support.FlightFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class FlightResponseTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void mapsSummaryWithOptionalAircraft(boolean assigned) {
        Flight flight = flight(FlightStatus.SCHEDULED);
        if (assigned) flight.setAircraft(aircraft());
        assertThat(FlightResponse.from(flight)).isEqualTo(new FlightResponse(
                1L, "OS1", 10L, "LOWW", "KJFK", DEPARTURE, ARRIVAL, FlightStatus.SCHEDULED,
                assigned ? "A320" : null, assigned ? "OE-TEST" : null));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void mapsDetailsWithOptionalAircraftAndActualTimes(boolean completed) {
        Flight flight = flight(completed ? FlightStatus.LANDED : FlightStatus.UNKNOWN);
        if (completed) {
            flight.setAircraft(aircraft());
            flight.setActualDepartureTime(DEPARTURE.plusMinutes(5));
            flight.setActualArrivalTime(ARRIVAL.plusMinutes(8));
        }
        assertThat(FlightDetailedResponse.from(flight)).isEqualTo(new FlightDetailedResponse(
                1L, "OS1", "LOWW", "Vienna Airport", "KJFK", "John F. Kennedy Airport",
                DEPARTURE, ARRIVAL, completed ? DEPARTURE.plusMinutes(5) : null,
                completed ? ARRIVAL.plusMinutes(8) : null, completed ? FlightStatus.LANDED : FlightStatus.UNKNOWN,
                completed ? "A320" : null, completed ? "OE-TEST" : null));
    }
}

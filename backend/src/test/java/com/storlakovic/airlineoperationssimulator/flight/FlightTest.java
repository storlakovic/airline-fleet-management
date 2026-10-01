package com.storlakovic.airlineoperationssimulator.flight;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.storlakovic.airlineoperationssimulator.support.FlightFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class FlightTest {
    @ParameterizedTest
    @CsvSource({"UNKNOWN,false", "SCHEDULED,true", "BOARDING,true", "EN_ROUTE,true",
            "APPROACH,true", "LANDED,false", "DELAYED,true", "CANCELLED,false"})
    void classifiesEveryStatus(FlightStatus status, boolean active) {
        assertThat(flight(status).isActive()).isEqualTo(active);
    }

    @Test
    void newFlightStartsUnassignedWithUnknownStatusAndNoActualTimes() {
        Flight flight = new Flight("OS123", route(), DEPARTURE, ARRIVAL);
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.UNKNOWN);
        assertThat(flight.getAircraft()).isNull();
        assertThat(flight.getActualDepartureTime()).isNull();
        assertThat(flight.getActualArrivalTime()).isNull();
    }
}

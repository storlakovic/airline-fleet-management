package com.storlakovic.airlineoperationssimulator.simulation;

import com.storlakovic.airlineoperationssimulator.flight.Flight;
import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.storlakovic.airlineoperationssimulator.support.FlightFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class FlightProgressionTest {
    private final FlightProgression progression = new FlightProgression();

    @ParameterizedTest(name = "{0} at threshold {2}ns → {3}")
    @CsvSource({
            "SCHEDULED,-10,-1,SCHEDULED", "SCHEDULED,-10,0,BOARDING", "SCHEDULED,-10,1,BOARDING",
            "BOARDING,0,-1,BOARDING", "BOARDING,0,0,EN_ROUTE", "BOARDING,0,1,EN_ROUTE",
            "EN_ROUTE,170,-1,EN_ROUTE", "EN_ROUTE,170,0,APPROACH", "EN_ROUTE,170,1,APPROACH",
            "APPROACH,180,-1,APPROACH", "APPROACH,180,0,LANDED", "APPROACH,180,1,LANDED"
    })
    void progressesAtInclusiveTimeBoundaries(FlightStatus initial, int thresholdMinutes,
                                            long offsetNanos, FlightStatus expected) {
        Flight flight = flight(initial);
        OffsetDateTime actualDeparture = DEPARTURE.plusMinutes(1);
        if (initial == FlightStatus.EN_ROUTE || initial == FlightStatus.APPROACH) {
            flight.setActualDepartureTime(actualDeparture);
        }
        OffsetDateTime now = DEPARTURE.plusMinutes(thresholdMinutes).plusNanos(offsetNanos);

        assertThat(progression.progress(flight, now)).isSameAs(flight);
        assertThat(flight.getStatus()).isEqualTo(expected);
        if (initial == FlightStatus.BOARDING && expected == FlightStatus.EN_ROUTE) {
            assertThat(flight.getActualDepartureTime()).isEqualTo(now);
        } else if (initial == FlightStatus.EN_ROUTE || initial == FlightStatus.APPROACH) {
            assertThat(flight.getActualDepartureTime()).isEqualTo(actualDeparture);
        } else {
            assertThat(flight.getActualDepartureTime()).isNull();
        }
        assertThat(flight.getActualArrivalTime()).isEqualTo(expected == FlightStatus.LANDED ? now : null);
        assertThat(flight.getScheduledDepartureTime()).isEqualTo(DEPARTURE);
        assertThat(flight.getScheduledArrivalTime()).isEqualTo(ARRIVAL);
    }

    @ParameterizedTest
    @EnumSource(value = FlightStatus.class, names = {"UNKNOWN", "DELAYED", "CANCELLED", "LANDED"})
    void leavesNonProgressingStatusesAndRecordedTimesUnchanged(FlightStatus status) {
        Flight flight = flight(status);
        flight.setActualDepartureTime(DEPARTURE);
        flight.setActualArrivalTime(ARRIVAL);
        progression.progress(flight, ARRIVAL.plusDays(1));
        assertThat(flight.getStatus()).isEqualTo(status);
        assertThat(flight.getActualDepartureTime()).isEqualTo(DEPARTURE);
        assertThat(flight.getActualArrivalTime()).isEqualTo(ARRIVAL);
    }

    @ParameterizedTest
    @CsvSource({"SCHEDULED,BOARDING", "BOARDING,EN_ROUTE", "EN_ROUTE,APPROACH", "APPROACH,LANDED"})
    void overdueFlightsAdvanceOnlyOneStagePerTick(FlightStatus initial, FlightStatus expected) {
        Flight flight = flight(initial);
        progression.progress(flight, ARRIVAL.plusDays(1));
        assertThat(flight.getStatus()).isEqualTo(expected);
    }

    @Test
    void comparesInstantsAcrossOffsets() {
        Flight flight = flight(FlightStatus.BOARDING);
        OffsetDateTime now = DEPARTURE.withOffsetSameInstant(ZoneOffset.ofHours(-4));
        progression.progress(flight, now);
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.EN_ROUTE);
        assertThat(flight.getActualDepartureTime()).isEqualTo(now);
    }

    @Test
    void repeatedTicksPreserveActualTimesThroughoutCompleteLifecycle() {
        Flight flight = flight(FlightStatus.SCHEDULED);
        progression.progress(flight, DEPARTURE.minusMinutes(10));
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.BOARDING);
        progression.progress(flight, DEPARTURE);
        progression.progress(flight, DEPARTURE.plusMinutes(1));
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.EN_ROUTE);
        assertThat(flight.getActualDepartureTime()).isEqualTo(DEPARTURE);
        progression.progress(flight, ARRIVAL.minusMinutes(10));
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.APPROACH);
        progression.progress(flight, ARRIVAL);
        progression.progress(flight, ARRIVAL.plusHours(1));
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.LANDED);
        assertThat(flight.getActualArrivalTime()).isEqualTo(ARRIVAL);
        assertThat(flight.getActualDepartureTime()).isEqualTo(DEPARTURE);
    }
}

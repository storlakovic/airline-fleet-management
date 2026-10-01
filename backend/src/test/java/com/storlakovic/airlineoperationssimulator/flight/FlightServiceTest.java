package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.aircraft.Aircraft;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftRepository;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftAlreadyAssignedException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftNotFoundException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftNotOperationalException;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightCreateRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightUpdateRequest;
import com.storlakovic.airlineoperationssimulator.flight.exceptions.AircraftAssignmentNotAllowedException;
import com.storlakovic.airlineoperationssimulator.flight.exceptions.FlightCancellationNotAllowedException;
import com.storlakovic.airlineoperationssimulator.flight.exceptions.FlightNotFoundException;
import com.storlakovic.airlineoperationssimulator.flight.exceptions.FlightUpdateNotAllowedException;
import com.storlakovic.airlineoperationssimulator.flight.exceptions.InvalidFlightTimeException;
import com.storlakovic.airlineoperationssimulator.route.RouteRepository;
import com.storlakovic.airlineoperationssimulator.route.exceptions.RouteNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static com.storlakovic.airlineoperationssimulator.support.FlightFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {
    @Mock private FlightRepository flights;
    @Mock private RouteRepository routes;
    @Mock private AircraftRepository aircraftRepository;

    private FlightService service() {
        return new FlightService(flights, routes, aircraftRepository);
    }

    private void findFlight(Flight flight) {
        when(flights.findById(1L)).thenReturn(Optional.of(flight));
    }

    private void saveSuccessfully() {
        when(flights.save(any(Flight.class))).thenAnswer(call -> call.getArgument(0));
    }

    private void assertNotSaved() {
        verify(flights, never()).save(any());
    }

    @Nested
    class Create {
        @Test
        void createsUnknownFlightWithResolvedRouteAndNoAircraft() {
            var route = route();
            when(routes.findById(10L)).thenReturn(Optional.of(route));
            saveSuccessfully();

            FlightResponse response = service().createFlight(
                    new FlightCreateRequest("OS123", 10L, DEPARTURE, ARRIVAL));

            var captured = ArgumentCaptor.forClass(Flight.class);
            verify(flights).save(captured.capture());
            Flight created = captured.getValue();
            assertThat(created.getRoute()).isSameAs(route);
            assertThat(created.getFlightNumber()).isEqualTo("OS123");
            assertThat(created.getScheduledDepartureTime()).isEqualTo(DEPARTURE);
            assertThat(created.getScheduledArrivalTime()).isEqualTo(ARRIVAL);
            assertThat(created.getStatus()).isEqualTo(FlightStatus.UNKNOWN);
            assertThat(created.getAircraft()).isNull();
            assertThat(created.getActualDepartureTime()).isNull();
            assertThat(created.getActualArrivalTime()).isNull();
            assertThat(response.routeId()).isEqualTo(10L);
            assertThat(response.originIcaoCode()).isEqualTo("LOWW");
            assertThat(response.destinationIcaoCode()).isEqualTo("KJFK");
            verifyNoInteractions(aircraftRepository);
        }

        @ParameterizedTest
        @CsvSource({"true,false", "false,true", "true,true"})
        void rejectsMissingTimesBeforeAccessingRepositories(boolean missingDeparture, boolean missingArrival) {
            var request = new FlightCreateRequest("OS123", 10L,
                    missingDeparture ? null : DEPARTURE, missingArrival ? null : ARRIVAL);
            assertThatThrownBy(() -> service().createFlight(request))
                    .isInstanceOf(InvalidFlightTimeException.class)
                    .hasMessage("Scheduled departure and arrival times are required");
            verifyNoInteractions(flights, routes, aircraftRepository);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void rejectsEqualOrEarlierArrivalInstantsAcrossOffsets(int arrivalMinutes) {
            var arrival = DEPARTURE.plusMinutes(arrivalMinutes).withOffsetSameInstant(ZoneOffset.ofHours(-4));
            var request = new FlightCreateRequest("OS123", 10L, DEPARTURE, arrival);
            assertThatThrownBy(() -> service().createFlight(request))
                    .isInstanceOf(InvalidFlightTimeException.class);
            verifyNoInteractions(flights, routes, aircraftRepository);
        }

        @Test
        void acceptsEarlierLocalArrivalWhenItsInstantIsLater() {
            var arrival = DEPARTURE.plusHours(1).withOffsetSameInstant(ZoneOffset.ofHours(-4));
            when(routes.findById(10L)).thenReturn(Optional.of(route()));
            saveSuccessfully();
            var response = service().createFlight(new FlightCreateRequest("OS123", 10L, DEPARTURE, arrival));
            assertThat(response.scheduledArrivalTime()).isEqualTo(arrival);
        }

        @Test
        void rejectsMissingRouteWithoutSaving() {
            when(routes.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service().createFlight(new FlightCreateRequest("OS123", 99L, DEPARTURE, ARRIVAL)))
                    .isInstanceOf(RouteNotFoundException.class);
            verifyNoInteractions(flights, aircraftRepository);
        }
    }

    @Nested
    class Read {
        @Test
        void returnsDetailsIncludingAssignedAircraftAndActualTimes() {
            Flight flight = flight(FlightStatus.EN_ROUTE);
            flight.setAircraft(aircraft());
            flight.setActualDepartureTime(DEPARTURE.plusMinutes(2));
            findFlight(flight);
            var result = service().getFlight(1L);
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.flightNumber()).isEqualTo("OS1");
            assertThat(result.originName()).isEqualTo("Vienna Airport");
            assertThat(result.destinationName()).isEqualTo("John F. Kennedy Airport");
            assertThat(result.aircraftIcaoCode()).isEqualTo("A320");
            assertThat(result.actualDepartureTime()).isEqualTo(DEPARTURE.plusMinutes(2));
            assertNotSaved();
        }

        @Test
        void returnsAllFlightsInRepositoryOrder() {
            when(flights.findAll()).thenReturn(List.of(flight(FlightStatus.UNKNOWN),
                    flight(2L, FlightStatus.CANCELLED, DEPARTURE, ARRIVAL)));
            assertThat(service().getAllFlights()).extracting(FlightResponse::id).containsExactly(1L, 2L);
            assertNotSaved();
        }

        @Test
        void returnsEmptySchedule() {
            when(flights.findAll()).thenReturn(List.of());
            assertThat(service().getAllFlights()).isEmpty();
        }
    }

    @Nested
    class Update {
        @ParameterizedTest
        @EnumSource(value = FlightStatus.class, names = {"UNKNOWN", "SCHEDULED", "DELAYED"})
        void reschedulesAllowedStatusesWithoutChangingOtherFields(FlightStatus status) {
            Flight flight = flight(status);
            findFlight(flight);
            saveSuccessfully();
            var result = service().updateFlight(new FlightUpdateRequest(DEPARTURE.plusHours(4), ARRIVAL.plusHours(4)), 1L);
            assertThat(result.scheduledDepartureTime()).isEqualTo(DEPARTURE.plusHours(4));
            assertThat(result.scheduledArrivalTime()).isEqualTo(ARRIVAL.plusHours(4));
            assertThat(result.status()).isEqualTo(status);
            assertThat(result.flightNumber()).isEqualTo("OS1");
            verify(flights).save(flight);
            verify(flights, never()).findByAircraft_Id(any());
        }

        @ParameterizedTest
        @EnumSource(value = FlightStatus.class, names = {"UNKNOWN", "SCHEDULED", "DELAYED"}, mode = EnumSource.Mode.EXCLUDE)
        void rejectsOtherStatusesWithoutMutatingFlight(FlightStatus status) {
            Flight flight = flight(status);
            findFlight(flight);
            assertThatThrownBy(() -> service().updateFlight(new FlightUpdateRequest(DEPARTURE.plusHours(1), null), 1L))
                    .isInstanceOf(FlightUpdateNotAllowedException.class);
            assertThat(flight.getScheduledDepartureTime()).isEqualTo(DEPARTURE);
            assertThat(flight.getStatus()).isEqualTo(status);
            assertNotSaved();
        }

        @ParameterizedTest
        @CsvSource({"true,false", "false,true", "false,false"})
        void mergesPartialUpdatesWithExistingTimes(boolean changeDeparture, boolean changeArrival) {
            Flight flight = flight(FlightStatus.UNKNOWN);
            findFlight(flight);
            saveSuccessfully();
            var result = service().updateFlight(new FlightUpdateRequest(
                    changeDeparture ? DEPARTURE.plusMinutes(15) : null,
                    changeArrival ? ARRIVAL.plusMinutes(15) : null), 1L);
            assertThat(result.scheduledDepartureTime()).isEqualTo(changeDeparture ? DEPARTURE.plusMinutes(15) : DEPARTURE);
            assertThat(result.scheduledArrivalTime()).isEqualTo(changeArrival ? ARRIVAL.plusMinutes(15) : ARRIVAL);
        }

        @ParameterizedTest
        @CsvSource({"180,", "181,", ",0", ",-1", "60,60", "120,60"})
        void rejectsInvalidMergedTimesWithoutMutation(Integer departureMinutes, Integer arrivalMinutes) {
            Flight flight = flight(FlightStatus.SCHEDULED);
            findFlight(flight);
            var request = new FlightUpdateRequest(departureMinutes == null ? null : DEPARTURE.plusMinutes(departureMinutes),
                    arrivalMinutes == null ? null : DEPARTURE.plusMinutes(arrivalMinutes));
            assertThatThrownBy(() -> service().updateFlight(request, 1L)).isInstanceOf(InvalidFlightTimeException.class);
            assertThat(flight.getScheduledDepartureTime()).isEqualTo(DEPARTURE);
            assertThat(flight.getScheduledArrivalTime()).isEqualTo(ARRIVAL);
            assertNotSaved();
        }
    }

    @Nested
    class Cancellation {
        @ParameterizedTest
        @EnumSource(value = FlightStatus.class, names = {"UNKNOWN", "SCHEDULED", "DELAYED"})
        void cancelsAllowedStatusesAndRetainsAssignment(FlightStatus status) {
            Flight flight = flight(status);
            Aircraft aircraft = aircraft();
            flight.setAircraft(aircraft);
            findFlight(flight);
            saveSuccessfully();
            assertThat(service().cancelFlight(1L).status()).isEqualTo(FlightStatus.CANCELLED);
            assertThat(flight.getAircraft()).isSameAs(aircraft);
            assertThat(flight.getScheduledDepartureTime()).isEqualTo(DEPARTURE);
            verify(flights).save(flight);
        }

        @ParameterizedTest
        @EnumSource(value = FlightStatus.class, names = {"UNKNOWN", "SCHEDULED", "DELAYED"}, mode = EnumSource.Mode.EXCLUDE)
        void rejectsOtherStatusesWithoutMutation(FlightStatus status) {
            Flight flight = flight(status);
            findFlight(flight);
            assertThatThrownBy(() -> service().cancelFlight(1L)).isInstanceOf(FlightCancellationNotAllowedException.class);
            assertThat(flight.getStatus()).isEqualTo(status);
            assertNotSaved();
        }
    }

    @Nested
    class Assignment {
        @ParameterizedTest
        @EnumSource(value = FlightStatus.class, names = {"UNKNOWN", "SCHEDULED"})
        void assignsOperationalAircraftAndSchedulesFlight(FlightStatus status) {
            Flight flight = flight(status);
            Aircraft aircraft = aircraft();
            findFlight(flight);
            when(aircraftRepository.findById(5L)).thenReturn(Optional.of(aircraft));
            when(flights.findByAircraft_Id(5L)).thenReturn(List.of());
            saveSuccessfully();
            var result = service().assignAircraft(1L, 5L);
            assertThat(flight.getAircraft()).isSameAs(aircraft);
            assertThat(result.status()).isEqualTo(FlightStatus.SCHEDULED);
            assertThat(result.aircraftRegistration()).isEqualTo("OE-TEST");
            verify(flights).save(flight);
        }

        @ParameterizedTest
        @EnumSource(value = FlightStatus.class, names = {"UNKNOWN", "SCHEDULED"}, mode = EnumSource.Mode.EXCLUDE)
        void rejectsOtherFlightStatusesBeforeLookingUpAircraft(FlightStatus status) {
            Flight flight = flight(status);
            findFlight(flight);
            assertThatThrownBy(() -> service().assignAircraft(1L, 5L)).isInstanceOf(AircraftAssignmentNotAllowedException.class);
            assertThat(flight.getAircraft()).isNull();
            assertThat(flight.getStatus()).isEqualTo(status);
            verifyNoInteractions(aircraftRepository);
            assertNotSaved();
        }

        @ParameterizedTest
        @EnumSource(value = AircraftStatus.class, names = "IN_SERVICE", mode = EnumSource.Mode.EXCLUDE)
        void rejectsNonOperationalAircraft(AircraftStatus status) {
            Flight flight = flight(FlightStatus.UNKNOWN);
            Aircraft aircraft = aircraft();
            aircraft.changeStatus(status);
            findFlight(flight);
            when(aircraftRepository.findById(5L)).thenReturn(Optional.of(aircraft));
            assertThatThrownBy(() -> service().assignAircraft(1L, 5L)).isInstanceOf(AircraftNotOperationalException.class);
            assertThat(flight.getAircraft()).isNull();
            assertThat(flight.getStatus()).isEqualTo(FlightStatus.UNKNOWN);
            verify(flights, never()).findByAircraft_Id(any());
            assertNotSaved();
        }

        @Test
        void rejectsMissingAircraft() {
            findFlight(flight(FlightStatus.UNKNOWN));
            when(aircraftRepository.findById(5L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service().assignAircraft(1L, 5L)).isInstanceOf(AircraftNotFoundException.class);
            assertNotSaved();
        }

        @Test
        void replacesPreviousAircraftWhenNewAircraftIsAvailable() {
            Flight flight = flight(FlightStatus.SCHEDULED);
            flight.setAircraft(aircraft());
            Aircraft replacement = aircraft();
            findFlight(flight);
            when(aircraftRepository.findById(5L)).thenReturn(Optional.of(replacement));
            when(flights.findByAircraft_Id(5L)).thenReturn(List.of());
            saveSuccessfully();
            service().assignAircraft(1L, 5L);
            assertThat(flight.getAircraft()).isSameAs(replacement);
        }
    }

    /** Both assignment and rescheduling must obey the same half-open time intervals. */
    @Nested
    class AircraftConflicts {
        @ParameterizedTest(name = "update={0}, existing=[{1},{2}), overlap={3}")
        @CsvSource({
                "false,-60,60,true", "false,120,240,true", "false,30,120,true",
                "false,-60,240,true", "false,0,180,true", "false,-60,0,false",
                "false,180,240,false", "false,-120,-60,false", "false,240,300,false",
                "true,-60,60,true", "true,120,240,true", "true,30,120,true",
                "true,-60,240,true", "true,0,180,true", "true,-60,0,false",
                "true,180,240,false", "true,-120,-60,false", "true,240,300,false"
        })
        void checksOverlapShapesAndExactBoundaries(boolean update, int start, int end, boolean overlap) {
            Flight flight = flight(FlightStatus.SCHEDULED);
            Aircraft aircraft = aircraft();
            flight.setAircraft(aircraft);
            // Reschedule from a different original window, testing the NEW interval.
            if (update) {
                flight.setScheduledDepartureTime(DEPARTURE.minusDays(1));
                flight.setScheduledArrivalTime(ARRIVAL.minusDays(1));
            }
            var originalDeparture = flight.getScheduledDepartureTime();
            var originalArrival = flight.getScheduledArrivalTime();
            Flight other = flight(2L, FlightStatus.SCHEDULED,
                    DEPARTURE.plusMinutes(start).withOffsetSameInstant(ZoneOffset.ofHours(2)),
                    DEPARTURE.plusMinutes(end).withOffsetSameInstant(ZoneOffset.ofHours(-4)));
            findFlight(flight);
            when(flights.findByAircraft_Id(5L)).thenReturn(List.of(other));
            if (!update) when(aircraftRepository.findById(5L)).thenReturn(Optional.of(aircraft));

            if (overlap) {
                assertThatThrownBy(() -> execute(update)).isInstanceOf(AircraftAlreadyAssignedException.class);
                assertThat(flight.getScheduledDepartureTime()).isEqualTo(originalDeparture);
                assertThat(flight.getScheduledArrivalTime()).isEqualTo(originalArrival);
                assertThat(flight.getAircraft()).isSameAs(aircraft);
                assertNotSaved();
            } else {
                saveSuccessfully();
                assertThat(execute(update).status()).isEqualTo(FlightStatus.SCHEDULED);
                verify(flights).save(flight);
            }
        }

        @ParameterizedTest
        @ValueSource(booleans = {false, true})
        void ignoresOwnFlightAndCancelledConflicts(boolean update) {
            Flight flight = flight(FlightStatus.SCHEDULED);
            Aircraft aircraft = aircraft();
            flight.setAircraft(aircraft);
            findFlight(flight);
            when(flights.findByAircraft_Id(5L)).thenReturn(List.of(flight,
                    flight(2L, FlightStatus.CANCELLED, DEPARTURE, ARRIVAL)));
            if (!update) when(aircraftRepository.findById(5L)).thenReturn(Optional.of(aircraft));
            saveSuccessfully();
            assertThat(execute(update).id()).isEqualTo(1L);
            verify(flights).save(flight);
        }

        private FlightResponse execute(boolean update) {
            return update ? service().updateFlight(new FlightUpdateRequest(DEPARTURE, ARRIVAL), 1L)
                    : service().assignAircraft(1L, 5L);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"read", "update", "cancel", "assign"})
    void rejectsMissingFlightBeforeAnyMutation(String operation) {
        when(flights.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> {
            switch (operation) {
                case "read" -> service().getFlight(1L);
                case "update" -> service().updateFlight(new FlightUpdateRequest(null, null), 1L);
                case "cancel" -> service().cancelFlight(1L);
                case "assign" -> service().assignAircraft(1L, 5L);
                default -> throw new AssertionError(operation);
            }
        }).isInstanceOf(FlightNotFoundException.class).hasMessageContaining("1");
        assertNotSaved();
        verifyNoInteractions(routes, aircraftRepository);
    }
}

package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.aircraft.Aircraft;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftRepository;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftAlreadyAssignedException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftNotFoundException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftNotOperationalException;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import com.storlakovic.airlineoperationssimulator.airport.Airport;
import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;
import com.storlakovic.airlineoperationssimulator.common.*;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightCreateRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightDetailedResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightUpdateRequest;
import com.storlakovic.airlineoperationssimulator.route.Route;
import com.storlakovic.airlineoperationssimulator.route.exceptions.RouteNotFoundException;
import com.storlakovic.airlineoperationssimulator.route.RouteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    private final FlightRepository repository =
            mock(FlightRepository.class);

    private final RouteRepository routeRepository =
            mock(RouteRepository.class);

    private final AircraftRepository aircraftRepository =
            mock(AircraftRepository.class);

    private final FlightService service =
            new FlightService(repository, routeRepository, aircraftRepository);


    @Test
    void shouldCreateFlight() {
        Route route = route(
                10L,
                airport(1L, "LOWW"),
                airport(2L, "KJFK")
        );

        OffsetDateTime departure = OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.ofHours(2));
        OffsetDateTime arrival = OffsetDateTime.of(2026, 9, 20, 13, 30, 0, 0, ZoneOffset.ofHours(-4));

        FlightCreateRequest request = new FlightCreateRequest(
                "OS123",
                10L,
                departure,
                arrival
        );

        when(routeRepository.findById(10L))
                .thenReturn(Optional.of(route));

        when(repository.save(any(Flight.class)))
                .thenAnswer(invocation -> {
                    Flight flight = invocation.getArgument(0);
                    ReflectionTestUtils.setField(flight, "id", 1L);
                    return flight;
                });

        FlightResponse result = service.createFlight(request);

        assertThat(result.id())
                .isEqualTo(1L);

        assertThat(result.flightNumber())
                .isEqualTo("OS123");

        assertThat(result.scheduledDepartureTime())
                .isEqualTo(departure);

        assertThat(result.scheduledArrivalTime())
                .isEqualTo(arrival);

        assertThat(result.status())
                .isEqualTo(FlightStatus.UNKNOWN);

        assertThat(result.route().id())
                .isEqualTo(10L);

        assertThat(result.route().originAirportId())
                .isEqualTo(1L);

        assertThat(result.route().originIcaoCode())
                .isEqualTo("LOWW");

        assertThat(result.route().destinationAirportId())
                .isEqualTo(2L);

        assertThat(result.route().destinationIcaoCode())
                .isEqualTo("KJFK");

        verify(repository).save(any(Flight.class));
    }


    @Test
    void shouldThrowWhenRouteDoesNotExist() {
        FlightCreateRequest request = new FlightCreateRequest(
                "OS123",
                99L,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 30, 0, 0, ZoneOffset.UTC)
        );

        when(routeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createFlight(request)
        )
                .isInstanceOf(RouteNotFoundException.class)
                .hasMessage("Route with id: 99 does not exist.");

        verify(repository, never())
                .save(any(Flight.class));
    }


    @Test
    void shouldPassResolvedRouteToFlight() {
        Route route = route(
                10L,
                airport(1L, "LOWW"),
                airport(2L, "KJFK")
        );

        FlightCreateRequest request = new FlightCreateRequest(
                "OS123",
                10L,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 30, 0, 0, ZoneOffset.UTC)
        );

        when(routeRepository.findById(10L))
                .thenReturn(Optional.of(route));

        when(repository.save(any(Flight.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Flight> captor = ArgumentCaptor.forClass(Flight.class);

        service.createFlight(request);

        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getRoute())
                .isSameAs(route);

        assertThat(captor.getValue().getFlightNumber())
                .isEqualTo("OS123");
    }

    @Test
    void shouldReturnFlightById() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));

        OffsetDateTime departure = OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.ofHours(2));
        OffsetDateTime arrival = OffsetDateTime.of(2026, 9, 20, 13, 30, 0, 0, ZoneOffset.ofHours(-4));

        Flight flight = new Flight("OS123", route, departure, arrival);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(flight));

        FlightDetailedResponse result = service.getFlight(1L);

        assertThat(result.id())
                .isEqualTo(1L);

        assertThat(result.flightNumber())
                .isEqualTo("OS123");

        assertThat(result.originIcaoCode())
                .isEqualTo("LOWW");

        assertThat(result.destinationIcaoCode())
                .isEqualTo("KJFK");

        assertThat(result.scheduledDepartureTime())
                .isEqualTo(departure);

        assertThat(result.scheduledArrivalTime())
                .isEqualTo(arrival);

        assertThat(result.status())
                .isEqualTo(FlightStatus.UNKNOWN);
    }


    @Test
    void shouldThrowWhenFlightDoesNotExist() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getFlight(99L)
        )
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessage("Flight with id: 99 not found");
    }

    @Test
    void shouldThrowWhenDepartureIsAfterArrival() {
        FlightCreateRequest request = new FlightCreateRequest(
                "OS123",
                10L,
                OffsetDateTime.of(2026, 9, 20, 14, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC)
        );

        assertThatThrownBy(() ->
                service.createFlight(request)
        )
                .isInstanceOf(InvalidFlightTimeException.class)
                .hasMessage("Departure time must be before arrival time");

        verify(routeRepository, never()).findById(any());
        verify(repository, never()).save(any());
    }


    @Test
    void shouldThrowWhenDepartureEqualsArrival() {
        OffsetDateTime sameTime = OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC);

        FlightCreateRequest request = new FlightCreateRequest(
                "OS123",
                10L,
                sameTime,
                sameTime
        );

        assertThatThrownBy(() ->
                service.createFlight(request)
        )
                .isInstanceOf(InvalidFlightTimeException.class);

        verify(repository, never()).save(any());
    }


    @Test
    void shouldHandleDifferentOffsetsCorrectly() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));

        // 20:00+02:00 = 18:00 UTC, 15:00-04:00 = 19:00 UTC → departure is before arrival
        OffsetDateTime departure = OffsetDateTime.of(2026, 9, 20, 20, 0, 0, 0, ZoneOffset.ofHours(2));
        OffsetDateTime arrival = OffsetDateTime.of(2026, 9, 20, 15, 0, 0, 0, ZoneOffset.ofHours(-4));

        FlightCreateRequest request = new FlightCreateRequest("OS123", 10L, departure, arrival);

        when(routeRepository.findById(10L)).thenReturn(Optional.of(route));
        when(repository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FlightResponse result = service.createFlight(request);

        assertThat(result.scheduledDepartureTime()).isEqualTo(departure);
    }

    @Test
    void shouldReturnAllFlights() {
        Route route1 = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Route route2 = route(20L, airport(3L, "EDDF"), airport(4L, "LFPG"));

        Flight flight1 = new Flight(
                "OS123",
                route1,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC)
        );
        ReflectionTestUtils.setField(flight1, "id", 1L);

        Flight flight2 = new Flight(
                "LH456",
                route2,
                OffsetDateTime.of(2026, 9, 21, 8, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 21, 9, 30, 0, 0, ZoneOffset.UTC)
        );
        ReflectionTestUtils.setField(flight2, "id", 2L);

        when(repository.findAll())
                .thenReturn(List.of(flight1, flight2));

        List<FlightResponse> result = service.getAllFlights();

        assertThat(result).hasSize(2);

        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).flightNumber()).isEqualTo("OS123");
        assertThat(result.get(0).route().originIcaoCode()).isEqualTo("LOWW");
        assertThat(result.get(0).route().destinationIcaoCode()).isEqualTo("KJFK");

        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).flightNumber()).isEqualTo("LH456");
        assertThat(result.get(1).route().originIcaoCode()).isEqualTo("EDDF");
        assertThat(result.get(1).route().destinationIcaoCode()).isEqualTo("LFPG");
    }


    @Test
    void shouldReturnEmptyListWhenNoFlightsExist() {
        when(repository.findAll())
                .thenReturn(List.of());

        List<FlightResponse> result = service.getAllFlights();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateBothTimes() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));

        Flight flight = new Flight(
                "OS123", route,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC)
        );
        ReflectionTestUtils.setField(flight, "id", 1L);

        OffsetDateTime newDeparture = OffsetDateTime.of(2026, 9, 20, 14, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime newArrival = OffsetDateTime.of(2026, 9, 20, 17, 0, 0, 0, ZoneOffset.UTC);

        FlightUpdateRequest request = new FlightUpdateRequest(newDeparture, newArrival);

        when(repository.findById(1L)).thenReturn(Optional.of(flight));
        when(repository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        FlightResponse result = service.updateFlight(request, 1L);

        assertThat(result.scheduledDepartureTime()).isEqualTo(newDeparture);
        assertThat(result.scheduledArrivalTime()).isEqualTo(newArrival);
    }


    @Test
    void shouldUpdateOnlyDepartureAndKeepExistingArrival() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));

        OffsetDateTime originalArrival = OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC);

        Flight flight = new Flight(
                "OS123", route,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                originalArrival
        );
        ReflectionTestUtils.setField(flight, "id", 1L);

        OffsetDateTime newDeparture = OffsetDateTime.of(2026, 9, 20, 11, 0, 0, 0, ZoneOffset.UTC);

        FlightUpdateRequest request = new FlightUpdateRequest(newDeparture, null);

        when(repository.findById(1L)).thenReturn(Optional.of(flight));
        when(repository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        FlightResponse result = service.updateFlight(request, 1L);

        assertThat(result.scheduledDepartureTime()).isEqualTo(newDeparture);
        assertThat(result.scheduledArrivalTime()).isEqualTo(originalArrival); // ← genau der Fall, der vorhin kaputt war
    }


    @Test
    void shouldThrowWhenMergedTimesAreInvalid() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));

        Flight flight = new Flight(
                "OS123", route,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC)
        );
        ReflectionTestUtils.setField(flight, "id", 1L);

        // Nur departure wird geändert, auf einen Zeitpunkt NACH der bestehenden arrival-Zeit
        OffsetDateTime invalidDeparture = OffsetDateTime.of(2026, 9, 20, 14, 0, 0, 0, ZoneOffset.UTC);

        FlightUpdateRequest request = new FlightUpdateRequest(invalidDeparture, null);

        when(repository.findById(1L)).thenReturn(Optional.of(flight));

        assertThatThrownBy(() ->
                service.updateFlight(request, 1L)
        )
                .isInstanceOf(InvalidFlightTimeException.class)
                .hasMessage("Departure time must be before arrival time");

        verify(repository, never()).save(any());
    }


    @Test
    void shouldThrowWhenFlightDoesNotExistWhenUpdating() {
        FlightUpdateRequest request = new FlightUpdateRequest(
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC)
        );

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateFlight(request, 99L)
        )
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessage("Flight with id: 99 not found");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldCancelScheduledFlight() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight flight = flightWithStatus(route, FlightStatus.SCHEDULED);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findById(1L)).thenReturn(Optional.of(flight));
        when(repository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        FlightResponse result = service.cancelFlight(1L);

        assertThat(result.status()).isEqualTo(FlightStatus.CANCELLED);
        verify(repository).save(flight);
    }


    @Test
    void shouldCancelDelayedFlight() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight flight = flightWithStatus(route, FlightStatus.DELAYED);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findById(1L)).thenReturn(Optional.of(flight));
        when(repository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        FlightResponse result = service.cancelFlight(1L);

        assertThat(result.status()).isEqualTo(FlightStatus.CANCELLED);
    }


    @Test
    void shouldCancelUnknownStatusFlight() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight flight = flightWithStatus(route, FlightStatus.UNKNOWN);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findById(1L)).thenReturn(Optional.of(flight));
        when(repository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        FlightResponse result = service.cancelFlight(1L);

        assertThat(result.status()).isEqualTo(FlightStatus.CANCELLED);
    }


    @Test
    void shouldThrowWhenFlightAlreadyCancelled() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight flight = flightWithStatus(route, FlightStatus.CANCELLED);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findById(1L)).thenReturn(Optional.of(flight));

        assertThatThrownBy(() ->
                service.cancelFlight(1L)
        )
                .isInstanceOf(FlightCancellationNotAllowedException.class);

        verify(repository, never()).save(any());
    }


    @Test
    void shouldThrowWhenFlightAlreadyCompleted() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight flight = flightWithStatus(route, FlightStatus.LANDED);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findById(1L)).thenReturn(Optional.of(flight));

        assertThatThrownBy(() ->
                service.cancelFlight(1L)
        )
                .isInstanceOf(FlightCancellationNotAllowedException.class);

        verify(repository, never()).save(any());
    }


    @Test
    void shouldThrowWhenFlightDoesNotExistOnFlightCancellation() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cancelFlight(99L)
        )
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessage("Flight with id: 99 not found");

        verify(repository, never()).save(any());
    }


    @Test
    void shouldAssignAircraftToFlight() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight flight = flightWithStatus(route, FlightStatus.SCHEDULED);
        ReflectionTestUtils.setField(flight, "id", 1L);

        Aircraft aircraft = aircraft(5L, AircraftStatus.IN_SERVICE);

        when(repository.findById(1L))
                .thenReturn(Optional.of(flight));

        when(aircraftRepository.findById(5L))
                .thenReturn(Optional.of(aircraft));

        when(repository.save(any(Flight.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        FlightResponse result = service.assignAircraft(1L, 5L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(flight.getAircraft()).isSameAs(aircraft);

        verify(repository).save(flight);
    }


    @Test
    void shouldThrowWhenFlightDoesNotExistWhenAssigningAircraftToFlight() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.assignAircraft(99L, 5L)
        )
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessage("Flight with id: 99 not found");

        verify(aircraftRepository, never()).findById(any());
        verify(repository, never()).save(any());
    }


    @Test
    void shouldThrowWhenAircraftDoesNotExist() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight flight = flightWithStatus(route, FlightStatus.SCHEDULED);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(flight));

        when(aircraftRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.assignAircraft(1L, 99L)
        )
                .isInstanceOf(AircraftNotFoundException.class)
                .hasMessage("Aircraft with id 99 not found");

        verify(repository, never()).save(any());
    }


    @Test
    void shouldThrowWhenAircraftIsNotOperational() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight flight = flightWithStatus(route, FlightStatus.SCHEDULED);
        ReflectionTestUtils.setField(flight, "id", 1L);

        Aircraft aircraft = aircraft(5L, AircraftStatus.MAINTENANCE);

        when(repository.findById(1L))
                .thenReturn(Optional.of(flight));

        when(aircraftRepository.findById(5L))
                .thenReturn(Optional.of(aircraft));

        assertThatThrownBy(() ->
                service.assignAircraft(1L, 5L)
        )
                .isInstanceOf(AircraftNotOperationalException.class);

        verify(repository, never()).save(any());
        assertThat(flight.getAircraft()).isNull();
    }

    @Test
    void shouldThrowWhenAircraftHasOverlappingFlight() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight newFlight = flightWithTimes(route,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC));
        ReflectionTestUtils.setField(newFlight, "id", 1L);

        Flight existingFlight = flightWithTimes(route,
                OffsetDateTime.of(2026, 9, 20, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 15, 0, 0, 0, ZoneOffset.UTC));
        ReflectionTestUtils.setField(existingFlight, "status", FlightStatus.SCHEDULED);

        Aircraft aircraft = aircraft(5L, AircraftStatus.IN_SERVICE);

        when(repository.findById(1L)).thenReturn(Optional.of(newFlight));
        when(aircraftRepository.findById(5L)).thenReturn(Optional.of(aircraft));
        when(repository.findByAircraft_Id(5L)).thenReturn(List.of(existingFlight));

        assertThatThrownBy(() ->
                service.assignAircraft(1L, 5L)
        )
                .isInstanceOf(AircraftAlreadyAssignedException.class);

        verify(repository, never()).save(any());
    }


    @Test
    void shouldAllowAssignmentWhenOverlappingFlightIsCancelled() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight newFlight = flightWithTimes(route,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC));
        ReflectionTestUtils.setField(newFlight, "id", 1L);

        Flight cancelledFlight = flightWithTimes(route,
                OffsetDateTime.of(2026, 9, 20, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 15, 0, 0, 0, ZoneOffset.UTC));
        ReflectionTestUtils.setField(cancelledFlight, "status", FlightStatus.CANCELLED);

        Aircraft aircraft = aircraft(5L, AircraftStatus.IN_SERVICE);

        when(repository.findById(1L)).thenReturn(Optional.of(newFlight));
        when(aircraftRepository.findById(5L)).thenReturn(Optional.of(aircraft));
        when(repository.findByAircraft_Id(5L)).thenReturn(List.of(cancelledFlight));
        when(repository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        FlightResponse result = service.assignAircraft(1L, 5L);

        assertThat(result.id()).isEqualTo(1L);
        verify(repository).save(newFlight);
    }


    @Test
    void shouldAllowAssignmentWhenExistingFlightDoesNotOverlap() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));
        Flight newFlight = flightWithTimes(route,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC));
        ReflectionTestUtils.setField(newFlight, "id", 1L);

        Flight nonOverlapping = flightWithTimes(route,
                OffsetDateTime.of(2026, 9, 20, 14, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 16, 0, 0, 0, ZoneOffset.UTC));
        ReflectionTestUtils.setField(nonOverlapping, "status", FlightStatus.SCHEDULED);

        Aircraft aircraft = aircraft(5L, AircraftStatus.IN_SERVICE);

        when(repository.findById(1L)).thenReturn(Optional.of(newFlight));
        when(aircraftRepository.findById(5L)).thenReturn(Optional.of(aircraft));
        when(repository.findByAircraft_Id(5L)).thenReturn(List.of(nonOverlapping));
        when(repository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        FlightResponse result = service.assignAircraft(1L, 5L);

        assertThat(result.id()).isEqualTo(1L);
    }


    @Test
    void shouldMoveScheduledFlightToBoardingWithinWindow() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));

        Flight flight = flightWithStatus(route, FlightStatus.SCHEDULED);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.SCHEDULED), any(OffsetDateTime.class)))
                .thenReturn(List.of(flight));

        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.BOARDING), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        when(repository.findByStatusAndScheduledArrivalTimeBefore(
                eq(FlightStatus.EN_ROUTE), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        service.progressFlightStatuses();

        assertThat(flight.getStatus()).isEqualTo(FlightStatus.BOARDING);
        verify(repository).saveAll(List.of(flight));
    }


    @Test
    void shouldMoveBoardingFlightToEnRoute() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));

        Flight flight = flightWithStatus(route, FlightStatus.BOARDING);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.SCHEDULED), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.BOARDING), any(OffsetDateTime.class)))
                .thenReturn(List.of(flight));

        when(repository.findByStatusAndScheduledArrivalTimeBefore(
                eq(FlightStatus.EN_ROUTE), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        service.progressFlightStatuses();

        assertThat(flight.getStatus()).isEqualTo(FlightStatus.EN_ROUTE);
        assertThat(flight.getActualDepartureTime()).isNotNull();
        verify(repository).saveAll(List.of(flight));
    }


    @Test
    void shouldMoveEnRouteFlightToLanded() {
        Route route = route(10L, airport(1L, "LOWW"), airport(2L, "KJFK"));

        Flight flight = flightWithStatus(route, FlightStatus.EN_ROUTE);
        ReflectionTestUtils.setField(flight, "id", 1L);

        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.SCHEDULED), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.BOARDING), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        when(repository.findByStatusAndScheduledArrivalTimeBefore(
                eq(FlightStatus.EN_ROUTE), any(OffsetDateTime.class)))
                .thenReturn(List.of(flight));

        service.progressFlightStatuses();

        assertThat(flight.getStatus()).isEqualTo(FlightStatus.LANDED);
        assertThat(flight.getActualArrivalTime()).isNotNull();
        verify(repository).saveAll(List.of(flight));
    }


    @Test
    void shouldDoNothingWhenNoFlightsMatch() {
        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.SCHEDULED), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.BOARDING), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        when(repository.findByStatusAndScheduledArrivalTimeBefore(
                eq(FlightStatus.EN_ROUTE), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        service.progressFlightStatuses();

        verify(repository, times(3)).saveAll(List.of());
    }


    @Test
    void shouldQueryBoardingWindowTenMinutesAhead() {
        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.SCHEDULED), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        when(repository.findByStatusAndScheduledDepartureTimeBefore(
                eq(FlightStatus.BOARDING), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        when(repository.findByStatusAndScheduledArrivalTimeBefore(
                eq(FlightStatus.EN_ROUTE), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        OffsetDateTime before = OffsetDateTime.now();

        service.progressFlightStatuses();

        ArgumentCaptor<OffsetDateTime> captor = ArgumentCaptor.forClass(OffsetDateTime.class);
        verify(repository).findByStatusAndScheduledDepartureTimeBefore(eq(FlightStatus.SCHEDULED), captor.capture());

        OffsetDateTime capturedThreshold = captor.getValue();

        assertThat(capturedThreshold).isAfter(before.plusMinutes(9));
        assertThat(capturedThreshold).isBefore(before.plusMinutes(11));
    }


    private Flight flightWithTimes(Route route, OffsetDateTime departure, OffsetDateTime arrival) {
        return new Flight("OS123", route, departure, arrival);
    }

    private Aircraft aircraft(Long id, AircraftStatus status) {
        Aircraft aircraft = mock(Aircraft.class);
        AircraftType aircraftType = mock(AircraftType.class);
        when(aircraft.getId()).thenReturn(id);
        when(aircraft.getStatus()).thenReturn(status);
        when(aircraft.getAircraftType()).thenReturn(aircraftType);
        return aircraft;
    }

    private Flight flightWithStatus(Route route, FlightStatus status) {
        Flight flight = new Flight(
                "OS123", route,
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 20, 13, 0, 0, 0, ZoneOffset.UTC)
        );
        ReflectionTestUtils.setField(flight, "status", status);
        return flight;
    }

    private Route route(Long id, Airport origin, Airport destination) {
        Route route = new Route(origin, destination);
        ReflectionTestUtils.setField(route, "id", id);
        return route;
    }

    private Airport airport(Long id, String icaoCode) {
        Airport airport = new Airport(
                icaoCode, null, "Test Airport", "Test City", "AT", 48.0, 16.0, "large_airport", AirportStatus.OPERATIONAL
        );
        ReflectionTestUtils.setField(airport, "id", id);
        return airport;
    }
}
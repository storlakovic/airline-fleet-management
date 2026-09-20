package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.airport.Airport;
import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;
import com.storlakovic.airlineoperationssimulator.common.FlightNotFoundException;
import com.storlakovic.airlineoperationssimulator.common.InvalidFlightTimeException;
import com.storlakovic.airlineoperationssimulator.common.RouteNotFoundException;
import com.storlakovic.airlineoperationssimulator.flight.dto.CreateFlightRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightDetailedResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import com.storlakovic.airlineoperationssimulator.route.Route;
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

    private final FlightService service =
            new FlightService(repository, routeRepository);


    @Test
    void shouldCreateFlight() {
        Route route = route(
                10L,
                airport(1L, "LOWW"),
                airport(2L, "KJFK")
        );

        OffsetDateTime departure = OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.ofHours(2));
        OffsetDateTime arrival = OffsetDateTime.of(2026, 9, 20, 13, 30, 0, 0, ZoneOffset.ofHours(-4));

        CreateFlightRequest request = new CreateFlightRequest(
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
        CreateFlightRequest request = new CreateFlightRequest(
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

        CreateFlightRequest request = new CreateFlightRequest(
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
        CreateFlightRequest request = new CreateFlightRequest(
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

        CreateFlightRequest request = new CreateFlightRequest(
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

        CreateFlightRequest request = new CreateFlightRequest("OS123", 10L, departure, arrival);

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
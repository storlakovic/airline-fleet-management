package com.storlakovic.airlineoperationssimulator.route;

import com.storlakovic.airlineoperationssimulator.airport.Airport;
import com.storlakovic.airlineoperationssimulator.airport.AirportRepository;
import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;
import com.storlakovic.airlineoperationssimulator.common.AirportNotFoundException;
import com.storlakovic.airlineoperationssimulator.common.RouteAlreadyExistsException;
import com.storlakovic.airlineoperationssimulator.common.RouteInvalidException;
import com.storlakovic.airlineoperationssimulator.route.dto.RouteCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteServiceTest {

    private final RouteRepository routeRepository =
            mock(RouteRepository.class);

    private final AirportRepository airportRepository =
            mock(AirportRepository.class);

    private final RouteService service =
            new RouteService(routeRepository, airportRepository);


    @Test
    void shouldCreateRoute() {
        Airport origin = airport(1L, "LOWW");
        Airport destination = airport(2L, "KJFK");

        when(airportRepository.findById(1L))
                .thenReturn(Optional.of(origin));

        when(airportRepository.findById(2L))
                .thenReturn(Optional.of(destination));

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Route result = service.addRoute(
                new RouteCreateRequest(1L, 2L)
        );

        assertThat(result.getOrigin())
                .isSameAs(origin);

        assertThat(result.getDestination())
                .isSameAs(destination);

        verify(routeRepository)
                .save(any(Route.class));
    }


    @Test
    void shouldThrowWhenOriginAirportDoesNotExist() {
        when(airportRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.addRoute(
                        new RouteCreateRequest(1L, 2L)
                )
        ).isInstanceOf(AirportNotFoundException.class);

        verify(routeRepository, never())
                .save(any());
    }


    @Test
    void shouldThrowWhenDestinationAirportDoesNotExist() {
        Airport origin = airport(1L, "LOWW");

        when(airportRepository.findById(1L))
                .thenReturn(Optional.of(origin));

        when(airportRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.addRoute(
                        new RouteCreateRequest(1L, 2L)
                )
        ).isInstanceOf(AirportNotFoundException.class);

        verify(routeRepository, never())
                .save(any());
    }


    @Test
    void shouldRejectRouteWithSameOriginAndDestination() {
        assertThatThrownBy(() ->
                service.addRoute(
                        new RouteCreateRequest(1L, 1L)
                )
        ).isInstanceOf(RouteInvalidException.class);

        verify(routeRepository, never())
                .save(any());
    }


    @Test
    void shouldRejectDuplicateRoute() {
        Airport origin = airport(1L, "LOWW");
        Airport destination = airport(2L, "KJFK");

        when(airportRepository.findById(1L))
                .thenReturn(Optional.of(origin));

        when(airportRepository.findById(2L))
                .thenReturn(Optional.of(destination));

        when(routeRepository
                .existsByOrigin_IdAndDestination_Id(1L, 2L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.addRoute(
                        new RouteCreateRequest(1L, 2L)
                )
        ).isInstanceOf(RouteAlreadyExistsException.class);

        verify(routeRepository, never())
                .save(any());
    }


    @Test
    void shouldAllowReverseRoute() {
        Airport jfk = airport(1L, "KJFK");
        Airport vienna = airport(2L, "LOWW");

        when(airportRepository.findById(1L))
                .thenReturn(Optional.of(jfk));

        when(airportRepository.findById(2L))
                .thenReturn(Optional.of(vienna));

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Route result = service.addRoute(
                new RouteCreateRequest(1L, 2L)
        );

        assertThat(result.getOrigin())
                .isSameAs(jfk);

        assertThat(result.getDestination())
                .isSameAs(vienna);

        verify(routeRepository)
                .save(any(Route.class));
    }


    private Airport airport(Long id, String icaoCode) {
        Airport airport = new Airport(
                icaoCode,
                null,
                "Test Airport",
                null,
                "AT",
                48.0,
                16.0,
                "large_airport",
                AirportStatus.OPERATIONAL
        );

        ReflectionTestUtils.setField(
                airport,
                "id",
                id
        );

        return airport;
    }
}
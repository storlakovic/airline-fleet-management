package com.storlakovic.airlineoperationssimulator.aircraft;

import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftUpdateRequest;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftTypeRepository;
import com.storlakovic.airlineoperationssimulator.aircrafttype.exceptions.AircraftTypeNotFoundException;
import com.storlakovic.airlineoperationssimulator.airport.Airport;
import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftAlreadyExistsException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftRetirementNotAllowedException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftNotFoundException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftStatusTransitionException;

import com.storlakovic.airlineoperationssimulator.flight.Flight;
import com.storlakovic.airlineoperationssimulator.flight.FlightRepository;
import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;
import com.storlakovic.airlineoperationssimulator.route.Route;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AircraftServiceTest {

    private final AircraftRepository aircraftRepository =
            mock(AircraftRepository.class);

    private final AircraftTypeRepository aircraftTypeRepository =
            mock(AircraftTypeRepository.class);

    private final FlightRepository flightRepository =
            mock(FlightRepository.class);

    private final AircraftService service =
            new AircraftService(aircraftRepository, aircraftTypeRepository, flightRepository);


    @Test
    void shouldAddValidAircraft() {
        AircraftType aircraftType =
                createAircraftType(
                        1L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        AircraftCreateRequest request =
                new AircraftCreateRequest(1L, "OE-LBA");

        when(aircraftTypeRepository.findById(1L))
                .thenReturn(Optional.of(aircraftType));

        when(aircraftRepository.save(any(Aircraft.class)))
                .thenAnswer(invocation -> {
                    Aircraft aircraft = invocation.getArgument(0);

                    ReflectionTestUtils.setField(
                            aircraft,
                            "id",
                            10L
                    );

                    return aircraft;
                });

        AircraftResponse result =
                service.addAircraftToFleet(request);

        assertThat(result.id())
                .isEqualTo(10L);

        assertThat(result.registration())
                .isEqualTo("OE-LBA");

        assertThat(result.aircraftTypeId())
                .isEqualTo(1L);

        assertThat(result.status())
                .isEqualTo(AircraftStatus.IN_SERVICE);
    }


    @Test
    void shouldSetInitialStatusToInService() {
        AircraftType aircraftType =
                createAircraftType(
                        1L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        AircraftCreateRequest request =
                new AircraftCreateRequest(1L, "OE-LBB");

        when(aircraftTypeRepository.findById(1L))
                .thenReturn(Optional.of(aircraftType));

        when(aircraftRepository.save(any(Aircraft.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AircraftResponse result =
                service.addAircraftToFleet(request);

        assertThat(result.status())
                .isEqualTo(AircraftStatus.IN_SERVICE);
    }


    @Test
    void shouldThrowExceptionWhenAircraftTypeDoesNotExist() {
        AircraftCreateRequest request =
                new AircraftCreateRequest(999L, "OE-LBC");

        when(aircraftTypeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.addAircraftToFleet(request)
        ).isInstanceOf(AircraftTypeNotFoundException.class);

        verify(aircraftRepository, never())
                .save(any(Aircraft.class));
    }


    @Test
    void shouldThrowExceptionWhenAircraftAlreadyExists() {
        AircraftCreateRequest request =
                new AircraftCreateRequest(1L, "OE-LBA");

        when(aircraftRepository.existsAircraftByRegistration("OE-LBA"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.addAircraftToFleet(request)
        ).isInstanceOf(AircraftAlreadyExistsException.class);

        verify(aircraftRepository, never())
                .save(any(Aircraft.class));

        verifyNoInteractions(aircraftTypeRepository);
    }


    @Test
    void shouldReturnAllAircraft() {
        AircraftType aircraftType =
                createAircraftType(
                        5L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft first =
                createAircraft(
                        1L,
                        aircraftType,
                        "OE-LBA"
                );

        Aircraft second =
                createAircraft(
                        2L,
                        aircraftType,
                        "OE-LBB"
                );

        when(aircraftRepository.findAll())
                .thenReturn(List.of(first, second));

        List<AircraftResponse> result =
                service.getAll();

        assertThat(result).hasSize(2);
    }


    @Test
    void shouldMapAircraftToResponse() {
        AircraftType aircraftType =
                createAircraftType(
                        5L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft aircraft =
                createAircraft(
                        1L,
                        aircraftType,
                        "OE-LBA"
                );

        when(aircraftRepository.findAll())
                .thenReturn(List.of(aircraft));

        List<AircraftResponse> result =
                service.getAll();

        AircraftResponse response =
                result.getFirst();

        assertThat(response.id())
                .isEqualTo(1L);

        assertThat(response.registration())
                .isEqualTo("OE-LBA");

        assertThat(response.aircraftTypeId())
                .isEqualTo(5L);

        assertThat(response.status())
                .isEqualTo(AircraftStatus.IN_SERVICE);
    }


    @Test
    void shouldReturnEmptyListWhenNoAircraftExist() {
        when(aircraftRepository.findAll())
                .thenReturn(List.of());

        List<AircraftResponse> result =
                service.getAll();

        assertThat(result).isEmpty();
    }


    @Test
    void shouldMapMultipleAircraftRegistrations() {
        AircraftType aircraftType =
                createAircraftType(
                        5L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft first =
                createAircraft(
                        1L,
                        aircraftType,
                        "OE-LBA"
                );

        Aircraft second =
                createAircraft(
                        2L,
                        aircraftType,
                        "OE-LBB"
                );

        when(aircraftRepository.findAll())
                .thenReturn(List.of(first, second));

        List<AircraftResponse> result =
                service.getAll();

        assertThat(result)
                .extracting(AircraftResponse::registration)
                .containsExactly("OE-LBA", "OE-LBB");
    }


    @Test
    void shouldReturnAircraftResponse() {
        AircraftType aircraftType =
                createAircraftType(
                        5L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft aircraft =
                createAircraft(
                        1L,
                        aircraftType,
                        "OE-LBA"
                );

        when(aircraftRepository.findById(1L))
                .thenReturn(Optional.of(aircraft));

        AircraftResponse result =
                service.getAircraftById(1L);

        assertThat(result.id())
                .isEqualTo(1L);

        assertThat(result.registration())
                .isEqualTo("OE-LBA");

        assertThat(result.status())
                .isEqualTo(AircraftStatus.IN_SERVICE);

        assertThat(result.aircraftTypeId())
                .isEqualTo(5L);

        assertThat(result.manufacturer())
                .isEqualTo("AIRBUS");

        assertThat(result.aircraftTypeModel())
                .isEqualTo("Airbus A320");

        assertThat(result.icaoCode())
                .isEqualTo("A320");
    }


    @Test
    void shouldThrowExceptionWhenAircraftDoesNotExist() {
        when(aircraftRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getAircraftById(999L)
        ).isInstanceOf(AircraftNotFoundException.class);
    }

    @Test
    void shouldUpdateAircraftStatus() {
        AircraftType aircraftType =
                createAircraftType(
                        1L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft aircraft =
                createAircraft(
                        10L,
                        aircraftType,
                        "OE-LBA"
                );

        when(aircraftRepository.findById(10L))
                .thenReturn(Optional.of(aircraft));

        when(aircraftRepository.save(aircraft))
                .thenReturn(aircraft);

        AircraftResponse result =
                service.updateAircraft(
                        10L,
                        new AircraftUpdateRequest(AircraftStatus.MAINTENANCE)
                );

        assertThat(result.status())
                .isEqualTo(AircraftStatus.MAINTENANCE);

        verify(aircraftRepository).save(aircraft);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingAircraft() {
        when(aircraftRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateAircraft(
                        999L,
                        new  AircraftUpdateRequest(AircraftStatus.MAINTENANCE)
                )
        ).isInstanceOf(AircraftNotFoundException.class);

        verify(aircraftRepository, never())
                .save(any(Aircraft.class));
    }

    @Test
    void shouldRejectStatusChangeForRetiredAircraft() {
        AircraftType aircraftType = createAircraftType(
                1L,
                "AIRBUS",
                "Airbus A320",
                "A320"
        );

        Aircraft aircraft = createAircraft(
                10L,
                aircraftType,
                "OE-LBA"
        );

        ReflectionTestUtils.setField(
                aircraft,
                "status",
                AircraftStatus.RETIRED
        );

        when(aircraftRepository.findById(10L))
                .thenReturn(Optional.of(aircraft));

        assertThatThrownBy(() ->
                service.updateAircraft(
                        10L,
                        new AircraftUpdateRequest(AircraftStatus.MAINTENANCE)
                )
        )
                .isInstanceOf(AircraftStatusTransitionException.class);

        verify(aircraftRepository, never())
                .save(any(Aircraft.class));
    }

    @Test
    void shouldThrowWhenRetiringNonExistingAircraft() {
        when(aircraftRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.retireAircraft(99L)
        )
                .isInstanceOf(AircraftNotFoundException.class)
                .hasMessage("Aircraft with id 99 not found");

        verify(aircraftRepository, never())
                .save(any(Aircraft.class));

        verifyNoInteractions(flightRepository);
    }

    @Test
    void shouldRetireAircraftWithoutActiveFlights() {
        AircraftType aircraftType =
                createAircraftType(
                        1L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft aircraft =
                createAircraft(
                        10L,
                        aircraftType,
                        "OE-LBA"
                );

        when(aircraftRepository.findById(10L))
                .thenReturn(Optional.of(aircraft));

        when(flightRepository.findByAircraft_Id(10L))
                .thenReturn(List.of());

        when(aircraftRepository.save(aircraft))
                .thenReturn(aircraft);

        AircraftResponse result =
                service.retireAircraft(10L);

        assertThat(result.status())
                .isEqualTo(AircraftStatus.RETIRED);

        verify(aircraftRepository).save(aircraft);
    }

    @Test
    void shouldRejectRetirementWhenAircraftHasActiveFlight() {
        AircraftType aircraftType =
                createAircraftType(
                        1L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft aircraft =
                createAircraft(
                        10L,
                        aircraftType,
                        "OE-LBA"
                );

        Route route =
                route(
                        10L,
                        airport(1L, "LOWW"),
                        airport(2L, "KJFK")
                );

        Flight flight =
                flightWithStatus(
                        route,
                        FlightStatus.SCHEDULED
                );

        when(aircraftRepository.findById(10L))
                .thenReturn(Optional.of(aircraft));

        when(flightRepository.findByAircraft_Id(10L))
                .thenReturn(List.of(flight));

        assertThatThrownBy(() ->
                service.retireAircraft(10L)
        )
                .isInstanceOf(AircraftRetirementNotAllowedException.class);

        verify(aircraftRepository, never())
                .save(any(Aircraft.class));
    }

    @Test
    void shouldAllowRetirementWhenAircraftHasOnlyLandedFlights() {
        AircraftType aircraftType =
                createAircraftType(
                        1L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft aircraft =
                createAircraft(
                        10L,
                        aircraftType,
                        "OE-LBA"
                );

        Route route =
                route(
                        10L,
                        airport(1L, "LOWW"),
                        airport(2L, "KJFK")
                );

        Flight flight =
                flightWithStatus(
                        route,
                        FlightStatus.LANDED
                );

        when(aircraftRepository.findById(10L))
                .thenReturn(Optional.of(aircraft));

        when(flightRepository.findByAircraft_Id(10L))
                .thenReturn(List.of(flight));

        when(aircraftRepository.save(aircraft))
                .thenReturn(aircraft);

        AircraftResponse result =
                service.retireAircraft(10L);

        assertThat(result.status())
                .isEqualTo(AircraftStatus.RETIRED);

        verify(aircraftRepository).save(aircraft);
    }

    @Test
    void shouldRejectRetirementThroughRegularStatusUpdate() {
        AircraftType aircraftType =
                createAircraftType(
                        1L,
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        Aircraft aircraft =
                createAircraft(
                        10L,
                        aircraftType,
                        "OE-LBA"
                );

        when(aircraftRepository.findById(10L))
                .thenReturn(Optional.of(aircraft));

        assertThatThrownBy(() ->
                service.updateAircraft(
                        10L,
                        new AircraftUpdateRequest(AircraftStatus.RETIRED)
                )
        )
                .isInstanceOf(AircraftRetirementNotAllowedException.class);

        verify(aircraftRepository, never())
                .save(any(Aircraft.class));
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

    private Airport airport(Long id, String icaoCode) {
        Airport airport = new Airport(
                icaoCode, null, "Test Airport", "Test City", "AT", 48.0, 16.0, "large_airport", AirportStatus.OPERATIONAL
        );
        ReflectionTestUtils.setField(airport, "id", id);
        return airport;
    }

    private Route route(Long id, Airport origin, Airport destination) {
        Route route = new Route(origin, destination);
        ReflectionTestUtils.setField(route, "id", id);
        return route;
    }

    private AircraftType createAircraftType(
            Long id,
            String manufacturer,
            String model,
            String icaoCode
    ) {
        AircraftType aircraftType =
                new AircraftType(
                        manufacturer,
                        model,
                        icaoCode
                );

        ReflectionTestUtils.setField(
                aircraftType,
                "id",
                id
        );

        return aircraftType;
    }


    private Aircraft createAircraft(
            Long id,
            AircraftType aircraftType,
            String registration
    ) {
        Aircraft aircraft =
                new Aircraft(
                        aircraftType,
                        registration
                );

        ReflectionTestUtils.setField(
                aircraft,
                "id",
                id
        );

        return aircraft;
    }


}
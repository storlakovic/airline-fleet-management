package com.storlakovic.airlineoperationssimulator.aircraft;

import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftDetailsResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftUpdateRequest;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftTypeRepository;
import com.storlakovic.airlineoperationssimulator.common.AircraftAlreadyExistsException;
import com.storlakovic.airlineoperationssimulator.common.AircraftNotFoundException;
import com.storlakovic.airlineoperationssimulator.common.AircraftStatusTransitionException;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AircraftServiceTest {

    private final AircraftRepository repository =
            mock(AircraftRepository.class);

    private final AircraftTypeRepository aircraftTypeRepository =
            mock(AircraftTypeRepository.class);

    private final AircraftService service =
            new AircraftService(repository, aircraftTypeRepository);


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

        when(repository.save(any(Aircraft.class)))
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

        assertThat(result.getId())
                .isEqualTo(10L);

        assertThat(result.getRegistration())
                .isEqualTo("OE-LBA");

        assertThat(result.getAircraftTypeId())
                .isEqualTo(1L);

        assertThat(result.getStatus())
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

        when(repository.save(any(Aircraft.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AircraftResponse result =
                service.addAircraftToFleet(request);

        assertThat(result.getStatus())
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
        ).isInstanceOf(NoSuchElementException.class);

        verify(repository, never())
                .save(any(Aircraft.class));
    }


    @Test
    void shouldThrowExceptionWhenAircraftAlreadyExists() {
        AircraftCreateRequest request =
                new AircraftCreateRequest(1L, "OE-LBA");

        when(repository.existsAircraftByRegistration("OE-LBA"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.addAircraftToFleet(request)
        ).isInstanceOf(AircraftAlreadyExistsException.class);

        verify(repository, never())
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

        when(repository.findAll())
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

        when(repository.findAll())
                .thenReturn(List.of(aircraft));

        List<AircraftResponse> result =
                service.getAll();

        AircraftResponse response =
                result.getFirst();

        assertThat(response.getId())
                .isEqualTo(1L);

        assertThat(response.getRegistration())
                .isEqualTo("OE-LBA");

        assertThat(response.getAircraftTypeId())
                .isEqualTo(5L);

        assertThat(response.getStatus())
                .isEqualTo(AircraftStatus.IN_SERVICE);
    }


    @Test
    void shouldReturnEmptyListWhenNoAircraftExist() {
        when(repository.findAll())
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

        when(repository.findAll())
                .thenReturn(List.of(first, second));

        List<AircraftResponse> result =
                service.getAll();

        assertThat(result)
                .extracting(AircraftResponse::getRegistration)
                .containsExactly("OE-LBA", "OE-LBB");
    }


    @Test
    void shouldReturnDetailedAircraft() {
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

        when(repository.findById(1L))
                .thenReturn(Optional.of(aircraft));

        AircraftDetailsResponse result =
                service.getAircraftById(1L);

        assertThat(result.getId())
                .isEqualTo(1L);

        assertThat(result.getRegistration())
                .isEqualTo("OE-LBA");

        assertThat(result.getStatus())
                .isEqualTo(AircraftStatus.IN_SERVICE);

        assertThat(result.getAircraftTypeId())
                .isEqualTo(5L);

        assertThat(result.getManufacturer())
                .isEqualTo("AIRBUS");

        assertThat(result.getAircraftTypeModel())
                .isEqualTo("Airbus A320");

        assertThat(result.getIcaoCode())
                .isEqualTo("A320");
    }


    @Test
    void shouldThrowExceptionWhenAircraftDoesNotExist() {
        when(repository.findById(999L))
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

        when(repository.findById(10L))
                .thenReturn(Optional.of(aircraft));

        when(repository.save(aircraft))
                .thenReturn(aircraft);

        AircraftResponse result =
                service.updateAircraft(
                        10L,
                        new AircraftUpdateRequest(AircraftStatus.MAINTENANCE)
                );

        assertThat(result.getStatus())
                .isEqualTo(AircraftStatus.MAINTENANCE);

        verify(repository).save(aircraft);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingAircraft() {
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateAircraft(
                        999L,
                        new  AircraftUpdateRequest(AircraftStatus.MAINTENANCE)
                )
        ).isInstanceOf(AircraftNotFoundException.class);

        verify(repository, never())
                .save(any(Aircraft.class));
    }

    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
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

        ReflectionTestUtils.setField(
                aircraft,
                "status",
                AircraftStatus.RETIRED
        );

        when(repository.findById(10L))
                .thenReturn(Optional.of(aircraft));

        assertThatThrownBy(() ->
                service.updateAircraft(
                        10L,
                        new  AircraftUpdateRequest(AircraftStatus.RETIRED)
                )
        ).isInstanceOf(AircraftStatusTransitionException.class);

        verify(repository, never())
                .save(any(Aircraft.class));
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
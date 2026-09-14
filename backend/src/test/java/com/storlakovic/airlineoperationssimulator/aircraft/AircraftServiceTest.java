package com.storlakovic.airlineoperationssimulator.aircraft;

import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftTypeRepository;
import com.storlakovic.airlineoperationssimulator.common.AircraftAlreadyExistsException;
import org.junit.jupiter.api.Test;

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
        AircraftType requestType = mock(AircraftType.class);

        when(requestType.getId()).thenReturn(1L);

        AircraftType storedType =
                new AircraftType("AIRBUS", "Airbus A320", "A320");

        AircraftCreateRequest request =
                new AircraftCreateRequest(1L, "OE-LBA");

        when(aircraftTypeRepository.findById(1L))
                .thenReturn(Optional.of(storedType));

        when(repository.save(any(Aircraft.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Aircraft result = service.addAircraftToFleet(request);

        assertThat(result.getRegistration())
                .isEqualTo("OE-LBA");

        assertThat(result.getAircraftType())
                .isEqualTo(storedType);
    }


    @Test
    void shouldSetInitialStatusToInService() {
        AircraftType requestType = mock(AircraftType.class);

        when(requestType.getId()).thenReturn(1L);

        AircraftType storedType =
                new AircraftType("AIRBUS", "Airbus A320", "A320");

        AircraftCreateRequest request =
                new AircraftCreateRequest(1L, "OE-LBB");

        when(aircraftTypeRepository.findById(1L))
                .thenReturn(Optional.of(storedType));

        when(repository.save(any(Aircraft.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Aircraft result = service.addAircraftToFleet(request);

        assertThat(result.getStatus())
                .isEqualTo(AircraftStatus.IN_SERVICE);
    }


    @Test
    void shouldThrowExceptionWhenAircraftTypeDoesNotExist() {
        AircraftType requestType = mock(AircraftType.class);

        when(requestType.getId()).thenReturn(999L);

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
        AircraftType aircraftType = mock(AircraftType.class);
        when(aircraftType.getId()).thenReturn(1L);

        AircraftCreateRequest request =
                new AircraftCreateRequest(999L, "OE-LBA");

        when(repository.existsAircraftByRegistration("OE-LBA"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.addAircraftToFleet(request))
                .isInstanceOf(AircraftAlreadyExistsException.class);

        verify(repository, never()).save(any(Aircraft.class));
    }
}
package com.storlakovic.airlineoperationssimulator.simulation;

import com.storlakovic.airlineoperationssimulator.flight.Flight;
import com.storlakovic.airlineoperationssimulator.flight.FlightRepository;
import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static com.storlakovic.airlineoperationssimulator.support.FlightFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {
    @Mock private FlightRepository repository;
    @Mock private FlightProgression progression;

    @Test
    void progressesEveryFlightWithOneTimestampAndSavesOnlyChangedFlights() {
        Flight boarding = flight(FlightStatus.SCHEDULED);
        Flight unchanged = flight(2L, FlightStatus.CANCELLED, DEPARTURE, ARRIVAL);
        Flight landed = flight(3L, FlightStatus.APPROACH, DEPARTURE, ARRIVAL);
        when(repository.findAll()).thenReturn(List.of(boarding, unchanged, landed));
        when(progression.progress(eq(boarding), any())).thenAnswer(call -> {
            boarding.setStatus(FlightStatus.BOARDING);
            return boarding;
        });
        when(progression.progress(eq(unchanged), any())).thenReturn(unchanged);
        when(progression.progress(eq(landed), any())).thenAnswer(call -> {
            landed.setStatus(FlightStatus.LANDED);
            return landed;
        });

        new SimulationService(repository, progression).progressFlightStatuses();

        var timestamps = ArgumentCaptor.forClass(OffsetDateTime.class);
        verify(progression).progress(eq(boarding), timestamps.capture());
        verify(progression).progress(eq(unchanged), timestamps.capture());
        verify(progression).progress(eq(landed), timestamps.capture());
        assertThat(timestamps.getAllValues()).doesNotContainNull().containsOnly(timestamps.getValue());
        verify(repository).saveAll(List.of(boarding, landed));
        verifyNoMoreInteractions(progression);
    }

    @Test
    void emptyScheduleDoesNotInvokeProgression() {
        when(repository.findAll()).thenReturn(List.of());
        new SimulationService(repository, progression).progressFlightStatuses();
        verifyNoInteractions(progression);
        verify(repository).saveAll(List.of());
    }

    @Test
    void unchangedFlightsAreNotIncludedInSavedBatch() {
        Flight flight = flight(FlightStatus.CANCELLED);
        when(repository.findAll()).thenReturn(List.of(flight));
        when(progression.progress(eq(flight), any())).thenReturn(flight);
        new SimulationService(repository, progression).progressFlightStatuses();
        verify(repository).saveAll(List.of());
    }

    @Test
    void progressionFailureIsPropagatedWithoutSavingPartialBatch() {
        Flight flight = flight(FlightStatus.SCHEDULED);
        when(repository.findAll()).thenReturn(List.of(flight));
        var failure = new IllegalStateException("Progression failed");
        when(progression.progress(eq(flight), any())).thenThrow(failure);
        assertThatThrownBy(() -> new SimulationService(repository, progression).progressFlightStatuses())
                .isSameAs(failure);
        verify(repository, never()).saveAll(any());
    }
}

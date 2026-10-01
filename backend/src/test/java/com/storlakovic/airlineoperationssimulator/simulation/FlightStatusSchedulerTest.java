package com.storlakovic.airlineoperationssimulator.simulation;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class FlightStatusSchedulerTest {
    @Test
    void delegatesEachTickToSimulationService() {
        SimulationService service = mock(SimulationService.class);
        new FlightStatusScheduler(service).progressFlightStatuses();
        verify(service).progressFlightStatuses();
        verifyNoMoreInteractions(service);
    }
}

package com.storlakovic.airlineoperationssimulator.simulation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FlightStatusScheduler {
    private final SimulationService simulationService;

    public FlightStatusScheduler(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Scheduled(fixedDelay = 30000)
    public void progressFlightStatuses() {
        simulationService.progressFlightStatuses();
    }
}

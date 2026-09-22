package com.storlakovic.airlineoperationssimulator.flight;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FlightStatusScheduler {
    private final FlightService flightService;

    public FlightStatusScheduler(FlightService flightService) {
        this.flightService = flightService;
    }

    @Scheduled(fixedDelay = 30000)
    public void progressFlightStatuses() {
        flightService.progressFlightStatuses();
    }
}

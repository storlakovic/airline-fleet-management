package com.storlakovic.airlineoperationssimulator.simulation;

import com.storlakovic.airlineoperationssimulator.flight.Flight;
import com.storlakovic.airlineoperationssimulator.flight.FlightRepository;
import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;
import com.storlakovic.airlineoperationssimulator.simulation.clock.SimulationClock;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationService {
    private final FlightRepository flightRepository;
    private final FlightProgression flightProgression;

    public SimulationService(FlightRepository flightRepository, FlightProgression flightProgression) {
        this.flightRepository = flightRepository;
        this.flightProgression = flightProgression;
    }

    public void progressFlightStatuses() {
        OffsetDateTime now = SimulationClock.now();

        List<Flight> flights = flightRepository.findAll();
        List<Flight> updatedFlights = new ArrayList<>();

        for (Flight flight : flights) {
            FlightStatus oldStatus = flight.getStatus();

            Flight newFlight = flightProgression.progress(flight, now);

            if (newFlight.getStatus() != oldStatus) {
                updatedFlights.add(newFlight);
            }
        }
        flightRepository.saveAll(updatedFlights);
    }
}

package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.common.RouteNotFoundException;
import com.storlakovic.airlineoperationssimulator.flight.dto.CreateFlightRequest;
import com.storlakovic.airlineoperationssimulator.route.Route;
import com.storlakovic.airlineoperationssimulator.route.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightService {
    private final FlightRepository repository;
    private final RouteRepository routeRepository;

    public FlightService(FlightRepository repository,  RouteRepository routeRepository) {
        this.repository = repository;
        this.routeRepository = routeRepository;
    }

    public CreateFlightRequest createFlightRequest(CreateFlightRequest createFlightRequest) {
        Route route = routeRepository.findById(createFlightRequest.routeId()).orElseThrow(() -> new RouteNotFoundException("Route with id: " + createFlightRequest.routeId() + " does not exist."));
        Flight savedFlight = repository.save(new Flight(createFlightRequest.flightNumber(), route, createFlightRequest.scheduledDepartureTime(), createFlightRequest.scheduledArrivalTime()));
        return new CreateFlightRequest(savedFlight.getFlightNumber(), savedFlight.getRoute().getId(), savedFlight.getScheduledDepartureTime(), savedFlight.getScheduledArrivalTime());
    }
}

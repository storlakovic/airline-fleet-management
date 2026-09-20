package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.common.FlightNotFoundException;
import com.storlakovic.airlineoperationssimulator.common.RouteNotFoundException;
import com.storlakovic.airlineoperationssimulator.flight.dto.CreateFlightRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import com.storlakovic.airlineoperationssimulator.route.Route;
import com.storlakovic.airlineoperationssimulator.route.RouteRepository;
import com.storlakovic.airlineoperationssimulator.route.dto.RouteResponse;
import org.springframework.stereotype.Service;

@Service
public class FlightService {
    private final FlightRepository repository;
    private final RouteRepository routeRepository;

    public FlightService(FlightRepository repository,  RouteRepository routeRepository) {
        this.repository = repository;
        this.routeRepository = routeRepository;
    }

    public FlightResponse createFlight(CreateFlightRequest request) {
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new RouteNotFoundException(
                        "Route with id: " + request.routeId() + " does not exist."
                ));

        Flight savedFlight = repository.save(new Flight(
                request.flightNumber(),
                route,
                request.scheduledDepartureTime(),
                request.scheduledArrivalTime()
        ));

        return new FlightResponse(
                savedFlight.getId(),
                savedFlight.getFlightNumber(),
                new RouteResponse(
                        route.getId(),
                        route.getOrigin().getId(),
                        route.getOrigin().getIcaoCode(),
                        route.getDestination().getId(),
                        route.getDestination().getIcaoCode()
                ),
                savedFlight.getRoute().getOrigin().getIcaoCode(),
                savedFlight.getRoute().getDestination().getIcaoCode(),
                savedFlight.getScheduledDepartureTime(),
                savedFlight.getScheduledArrivalTime(),
                savedFlight.getStatus()
        );
    }

    public FlightResponse getFlight(Long id) {
        Flight flight = repository.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight with id: " + id + " not found"));
        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                new RouteResponse(
                    flight.getRoute().getId(),
                    flight.getRoute().getOrigin().getId(),
                    flight.getRoute().getOrigin().getIcaoCode(),
                    flight.getRoute().getDestination().getId(),
                    flight.getRoute().getDestination().getIcaoCode()
                ),
                flight.getRoute().getOrigin().getIcaoCode(),
                flight.getRoute().getDestination().getIcaoCode(),
                flight.getScheduledDepartureTime(),
                flight.getScheduledArrivalTime(),
                flight.getStatus()
        );
    }
}

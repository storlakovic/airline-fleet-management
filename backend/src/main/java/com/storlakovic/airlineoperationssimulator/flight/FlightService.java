package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.common.FlightNotFoundException;
import com.storlakovic.airlineoperationssimulator.common.InvalidFlightTimeException;
import com.storlakovic.airlineoperationssimulator.common.RouteNotFoundException;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightCreateRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightDetailedResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightUpdateRequest;
import com.storlakovic.airlineoperationssimulator.route.Route;
import com.storlakovic.airlineoperationssimulator.route.RouteRepository;
import com.storlakovic.airlineoperationssimulator.route.dto.RouteResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FlightService {
    private final FlightRepository repository;
    private final RouteRepository routeRepository;

    public FlightService(FlightRepository repository,  RouteRepository routeRepository) {
        this.repository = repository;
        this.routeRepository = routeRepository;
    }

    public FlightResponse createFlight(FlightCreateRequest request) {

        if (request.scheduledDepartureTime() != null && request.scheduledArrivalTime() != null
                && !request.scheduledDepartureTime().isBefore(request.scheduledArrivalTime())) {
            throw new InvalidFlightTimeException(
                    "Departure time must be before arrival time"
            );
        }

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

    public FlightDetailedResponse getFlight(Long id) {
        Flight flight = repository.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight with id: " + id + " not found"));
        return new FlightDetailedResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getRoute().getOrigin().getIcaoCode(),
                flight.getRoute().getOrigin().getName(),
                flight.getRoute().getDestination().getIcaoCode(),
                flight.getRoute().getDestination().getName(),
                flight.getScheduledDepartureTime(),
                flight.getScheduledArrivalTime(),
                flight.getActualDepartureTime(),
                flight.getActualArrivalTime(),
                flight.getStatus()
        );
    }

    public List<FlightResponse> getAllFlights() {
        List<Flight> flights = repository.findAll();
        return flights.stream().map(flight -> new FlightResponse(
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
        )).toList();
    }

    public FlightResponse updateFlight(FlightUpdateRequest request, Long id) {
        Flight flight = repository.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight with id: " + id + " not found"));

        OffsetDateTime newDeparture = request.scheduledDepartureTime() != null
                ? request.scheduledDepartureTime()
                : flight.getScheduledDepartureTime();

        OffsetDateTime newArrival = request.scheduledArrivalTime() != null
                ? request.scheduledArrivalTime()
                : flight.getScheduledArrivalTime();

        if (!newDeparture.isBefore(newArrival)) {
            throw new InvalidFlightTimeException("Departure time must be before arrival time");
        }

        flight.setScheduledDepartureTime(newDeparture);
        flight.setScheduledArrivalTime(newArrival);
        Flight updatedFlight = repository.save(flight);
        return new FlightResponse(
                updatedFlight.getId(),
                updatedFlight.getFlightNumber(),
                new RouteResponse(
                        updatedFlight.getRoute().getId(),
                        updatedFlight.getRoute().getOrigin().getId(),
                        updatedFlight.getRoute().getOrigin().getIcaoCode(),
                        updatedFlight.getRoute().getDestination().getId(),
                        updatedFlight.getRoute().getDestination().getIcaoCode()
                ),
                updatedFlight.getRoute().getOrigin().getIcaoCode(),
                updatedFlight.getRoute().getDestination().getIcaoCode(),
                updatedFlight.getScheduledDepartureTime(),
                updatedFlight.getScheduledArrivalTime(),
                updatedFlight.getStatus()
        );
    }
}

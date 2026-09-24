package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.aircraft.Aircraft;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftRepository;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftAlreadyAssignedException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftNotFoundException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftNotOperationalException;
import com.storlakovic.airlineoperationssimulator.common.*;
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
    private final AircraftRepository aircraftRepository;

    public FlightService(FlightRepository repository,  RouteRepository routeRepository,  AircraftRepository aircraftRepository) {
        this.repository = repository;
        this.routeRepository = routeRepository;
        this.aircraftRepository = aircraftRepository;
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
                savedFlight.getStatus(),
                savedFlight.getAircraft() == null ? null : savedFlight.getAircraft().getAircraftType().getIcaoCode(),
                savedFlight.getAircraft() == null ? null : savedFlight.getAircraft().getRegistration()
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
                flight.getStatus(),
                flight.getAircraft() == null ? null : flight.getAircraft().getAircraftType().getIcaoCode(),
                flight.getAircraft() == null ? null : flight.getAircraft().getRegistration()
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
                updatedFlight.getStatus(),
                updatedFlight.getAircraft() == null ? null : updatedFlight.getAircraft().getAircraftType().getIcaoCode(),
                updatedFlight.getAircraft() == null ? null : updatedFlight.getAircraft().getRegistration()
        );
    }

    public FlightResponse cancelFlight(Long id) {
        Flight flight = repository.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight with id: " + id + " not found"));
        boolean cancellable = flight.getStatus() == FlightStatus.UNKNOWN
                || flight.getStatus() == FlightStatus.SCHEDULED
                || flight.getStatus() == FlightStatus.DELAYED;

        if (!cancellable) {
            throw new FlightCancellationNotAllowedException(
                    "Flight with id: " + id + " cannot be cancelled from status " + flight.getStatus()
            );
        }
        flight.setStatus(FlightStatus.CANCELLED);
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
                updatedFlight.getStatus(),
                updatedFlight.getAircraft() == null ? null : updatedFlight.getAircraft().getAircraftType().getIcaoCode(),
                updatedFlight.getAircraft() == null ? null : updatedFlight.getAircraft().getRegistration()
        );
    }

    public FlightResponse assignAircraft(Long flightId, Long aircraftId) {
        Flight flight = repository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(
                        "Flight with id: " + flightId + " not found"
                ));

        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new AircraftNotFoundException(
                        "Aircraft with id " + aircraftId + " not found"
                ));

        List<Flight> existingFlights = repository.findByAircraft_Id(aircraftId);

        boolean hasOverlap = existingFlights.stream().filter(existing -> existing.getStatus() != FlightStatus.CANCELLED)
                .anyMatch(existing ->
                        flight.getScheduledDepartureTime().isBefore(existing.getScheduledArrivalTime())
                                && existing.getScheduledDepartureTime().isBefore(flight.getScheduledArrivalTime())
                );

        if (hasOverlap) {
            throw new AircraftAlreadyAssignedException(
                    "Aircraft is already assigned to an overlapping flight"
            );
        }

        if (aircraft.getStatus() != AircraftStatus.IN_SERVICE) {
            throw new AircraftNotOperationalException(
                    "Aircraft with id " + aircraftId + " is not operational and cannot be assigned"
            );
        }

        flight.setAircraft(aircraft);
        flight.setStatus(FlightStatus.SCHEDULED);

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
                updatedFlight.getStatus(),
                updatedFlight.getAircraft() == null ? null : updatedFlight.getAircraft().getAircraftType().getIcaoCode(),
                updatedFlight.getAircraft() == null ? null : updatedFlight.getAircraft().getRegistration()
        );
    }

    public void progressFlightStatuses() {
        OffsetDateTime now = OffsetDateTime.now();

        List<Flight> toBoard = repository.findByStatusAndScheduledDepartureTimeBefore(
                FlightStatus.SCHEDULED, now.plusMinutes(10)
        );
        toBoard.forEach(flight -> flight.setStatus(FlightStatus.BOARDING));
        repository.saveAll(toBoard);

        List<Flight> toDepart = repository.findByStatusAndScheduledDepartureTimeBefore(
                FlightStatus.BOARDING, now
        );
        toDepart.forEach(flight -> {
            flight.setStatus(FlightStatus.EN_ROUTE);
            flight.setActualDepartureTime(now);
        });
        repository.saveAll(toDepart);

        List<Flight> toLand = repository.findByStatusAndScheduledArrivalTimeBefore(
                FlightStatus.EN_ROUTE, now
        );
        toLand.forEach(flight -> {
            flight.setStatus(FlightStatus.LANDED);
            flight.setActualArrivalTime(now);
        });
        repository.saveAll(toLand);
    }
}

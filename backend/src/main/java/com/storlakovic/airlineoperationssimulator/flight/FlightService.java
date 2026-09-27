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
import com.storlakovic.airlineoperationssimulator.route.exceptions.RouteNotFoundException;
import com.storlakovic.airlineoperationssimulator.route.RouteRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final RouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;

    public FlightService(FlightRepository flightRepository,  RouteRepository routeRepository,  AircraftRepository aircraftRepository) {
        this.flightRepository = flightRepository;
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

        Flight savedFlight = flightRepository.save(new Flight(
                request.flightNumber(),
                route,
                request.scheduledDepartureTime(),
                request.scheduledArrivalTime()
        ));

        return FlightResponse.from(savedFlight);
    }

    public FlightDetailedResponse getFlight(Long id) {
        Flight flight = flightRepository.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight with id: " + id + " not found"));
        return FlightDetailedResponse.from(flight);
    }

    public List<FlightResponse> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream().map(FlightResponse::from).toList();
    }

    public FlightResponse updateFlight(FlightUpdateRequest request, Long id) {
        Flight flight = flightRepository.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight with id: " + id + " not found"));

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

        Flight updatedFlight = flightRepository.save(flight);

        return FlightResponse.from(updatedFlight);
    }

    public FlightResponse cancelFlight(Long id) {
        Flight flight = flightRepository.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight with id: " + id + " not found"));
        boolean cancellable = flight.getStatus() == FlightStatus.UNKNOWN
                || flight.getStatus() == FlightStatus.SCHEDULED
                || flight.getStatus() == FlightStatus.DELAYED;

        if (!cancellable) {
            throw new FlightCancellationNotAllowedException(
                    "Flight with id: " + id + " cannot be cancelled from status " + flight.getStatus()
            );
        }
        flight.setStatus(FlightStatus.CANCELLED);
        Flight updatedFlight = flightRepository.save(flight);

        return FlightResponse.from(updatedFlight);
    }

    public FlightResponse assignAircraft(Long flightId, Long aircraftId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(
                        "Flight with id: " + flightId + " not found"
                ));

        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new AircraftNotFoundException(
                        "Aircraft with id " + aircraftId + " not found"
                ));

        List<Flight> existingFlights = flightRepository.findByAircraft_Id(aircraftId);

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

        Flight updatedFlight = flightRepository.save(flight);

        return FlightResponse.from(updatedFlight);
    }

    public void progressFlightStatuses() {
        OffsetDateTime now = OffsetDateTime.now();

        List<Flight> toBoard = flightRepository.findByStatusAndScheduledDepartureTimeBefore(
                FlightStatus.SCHEDULED, now.plusMinutes(10)
        );
        toBoard.forEach(flight -> flight.setStatus(FlightStatus.BOARDING));
        flightRepository.saveAll(toBoard);

        List<Flight> toDepart = flightRepository.findByStatusAndScheduledDepartureTimeBefore(
                FlightStatus.BOARDING, now
        );
        toDepart.forEach(flight -> {
            flight.setStatus(FlightStatus.EN_ROUTE);
            flight.setActualDepartureTime(now);
        });
        flightRepository.saveAll(toDepart);

        List<Flight> toLand = flightRepository.findByStatusAndScheduledArrivalTimeBefore(
                FlightStatus.EN_ROUTE, now
        );
        toLand.forEach(flight -> {
            flight.setStatus(FlightStatus.LANDED);
            flight.setActualArrivalTime(now);
        });
        flightRepository.saveAll(toLand);
    }
}

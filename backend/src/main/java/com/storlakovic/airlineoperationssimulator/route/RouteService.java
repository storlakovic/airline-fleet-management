package com.storlakovic.airlineoperationssimulator.route;

import com.storlakovic.airlineoperationssimulator.airport.Airport;
import com.storlakovic.airlineoperationssimulator.airport.AirportRepository;
import com.storlakovic.airlineoperationssimulator.common.AirportNotFoundException;
import com.storlakovic.airlineoperationssimulator.common.RouteAlreadyExistsException;
import com.storlakovic.airlineoperationssimulator.common.RouteInvalidException;
import com.storlakovic.airlineoperationssimulator.route.dto.RouteCreateRequest;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final AirportRepository airportRepository;

    public RouteService(RouteRepository routeRepository,  AirportRepository airportRepository) {
        this.routeRepository = routeRepository;
        this.airportRepository = airportRepository;
    }

    public Route addRoute(RouteCreateRequest request) {
        if (request.getDestinationAirportId().equals(request.getOriginAirportId())) {
            throw new RouteInvalidException("Origin and destination must not be the same");
        }
        Airport origin = airportRepository.findById(request.getOriginAirportId()).orElseThrow(() -> new AirportNotFoundException("Could not find airport with id: " + request.getOriginAirportId()));
        Airport destination = airportRepository.findById(request.getDestinationAirportId()).orElseThrow(() -> new AirportNotFoundException("Could not find airport with id: " + request.getDestinationAirportId()));

        if(routeRepository.existsByOrigin_IdAndDestination_Id(request.getOriginAirportId(), request.getDestinationAirportId())) {
            throw new RouteAlreadyExistsException("Origin and destination must not be the same");
        }

        return routeRepository.save(new Route(origin,destination));
    }
}

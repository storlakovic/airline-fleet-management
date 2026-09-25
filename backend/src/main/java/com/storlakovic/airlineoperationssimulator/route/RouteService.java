package com.storlakovic.airlineoperationssimulator.route;

import com.storlakovic.airlineoperationssimulator.airport.Airport;
import com.storlakovic.airlineoperationssimulator.airport.AirportRepository;
import com.storlakovic.airlineoperationssimulator.airport.exceptions.AirportNotFoundException;
import com.storlakovic.airlineoperationssimulator.route.dto.RouteCreateRequest;
import com.storlakovic.airlineoperationssimulator.route.dto.RouteResponse;
import com.storlakovic.airlineoperationssimulator.route.exceptions.RouteAlreadyExistsException;
import com.storlakovic.airlineoperationssimulator.route.exceptions.RouteInvalidException;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final AirportRepository airportRepository;

    public RouteService(RouteRepository routeRepository,  AirportRepository airportRepository) {
        this.routeRepository = routeRepository;
        this.airportRepository = airportRepository;
    }

    public RouteResponse addRoute(RouteCreateRequest request) {
        if (request.destinationAirportId().equals(request.originAirportId())) {
            throw new RouteInvalidException("Origin and destination must not be the same");
        }
        Airport origin = airportRepository.findById(request.originAirportId()).orElseThrow(() -> new AirportNotFoundException("Could not find airport with id: " + request.originAirportId()));
        Airport destination = airportRepository.findById(request.destinationAirportId()).orElseThrow(() -> new AirportNotFoundException("Could not find airport with id: " + request.destinationAirportId()));

        if(routeRepository.existsByOrigin_IdAndDestination_Id(request.originAirportId(), request.destinationAirportId())) {
            throw new RouteAlreadyExistsException("Route already exists");
        }
        Route newRoute = routeRepository.save(new Route(origin,destination));

        return RouteResponse.from(newRoute);
    }
}

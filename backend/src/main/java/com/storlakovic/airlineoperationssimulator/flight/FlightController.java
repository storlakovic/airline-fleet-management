package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.flight.dto.CreateFlightRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/flight")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }


    @PostMapping
    public FlightResponse createFlightRequest(CreateFlightRequest createFlightRequest) {
        return flightService.createFlight(createFlightRequest);
    }
}

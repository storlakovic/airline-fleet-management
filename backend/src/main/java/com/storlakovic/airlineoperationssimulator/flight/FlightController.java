package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.flight.dto.CreateFlightRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightDetailedResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/flight")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping(path = "/create")
    public FlightResponse createFlight(CreateFlightRequest createFlightRequest) {
        return flightService.createFlight(createFlightRequest);
    }

    @GetMapping("/{id}")
    public FlightDetailedResponse getFlight(@RequestParam Long id) {
        return flightService.getFlight(id);
    }

    @GetMapping
    public List<FlightResponse> getAllFlights() {
        return flightService.getAllFlights();
    }
}

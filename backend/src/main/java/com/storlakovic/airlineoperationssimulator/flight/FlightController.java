package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.flight.dto.FlightCreateRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightDetailedResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightUpdateRequest;
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
    public FlightResponse createFlight(FlightCreateRequest createFlightRequest) {
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

    @PutMapping("/update/{id}")
    public FlightResponse updateFlight(@RequestBody FlightUpdateRequest flightUpdateRequest, @RequestParam Long id) {
        return flightService.updateFlight(flightUpdateRequest, id);
    }
}

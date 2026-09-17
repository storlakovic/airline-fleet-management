package com.storlakovic.airlineoperationssimulator.airport;

import com.storlakovic.airlineoperationssimulator.airport.dto.AirportResponse;
import com.storlakovic.airlineoperationssimulator.airport.dto.AirportUpdateRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/airport")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @PostMapping(path = "/import")
    public List<Airport> addAirports() {
        return airportService.importAirports();
    }

    @PutMapping(path = "/update/{id}")
    public AirportResponse updateAirport(@PathVariable Long id, @RequestBody AirportUpdateRequest request) {
        return airportService.updateAirport(id, request);
    }

}

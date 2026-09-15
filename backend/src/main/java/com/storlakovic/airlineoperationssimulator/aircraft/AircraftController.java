package com.storlakovic.airlineoperationssimulator.aircraft;

import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftDetailsResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aircraft")
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @PostMapping("register")
    public AircraftResponse registerAircraft(@RequestBody AircraftCreateRequest request) {
        return aircraftService.addAircraftToFleet(request);
    }

    @GetMapping
    public List<AircraftResponse> getAircraftOverview() {
        return aircraftService.getAll();
    }

    @GetMapping("detailed-overview")
    public AircraftDetailsResponse getDetailedOverview(@RequestParam Long aircraftTypeId) {
        return aircraftService.getAircraftById(aircraftTypeId);
    }
}

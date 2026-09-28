package com.storlakovic.airlineoperationssimulator.aircraft;

import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftUpdateRequest;
import jakarta.validation.Valid;
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
    public AircraftResponse registerAircraft(@Valid @RequestBody AircraftCreateRequest request) {
        return aircraftService.addAircraftToFleet(request);
    }

    @PutMapping("update/{id}")
    public AircraftResponse updateAircraft(@PathVariable Long id,
                                            @Valid @RequestBody AircraftUpdateRequest request) {
        return aircraftService.updateAircraft(id, request);
    }

    @GetMapping
    public List<AircraftResponse> getAircraftOverview() {
        return aircraftService.getAll();
    }

    @GetMapping("/{id}")
    public AircraftResponse getAircraftById(@PathVariable Long id) {
        return aircraftService.getAircraftById(id);
    }

    @PutMapping("retire/{id}")
    public AircraftResponse retireAircraft(@PathVariable Long id) {
        return aircraftService.retireAircraft(id);
    }
}

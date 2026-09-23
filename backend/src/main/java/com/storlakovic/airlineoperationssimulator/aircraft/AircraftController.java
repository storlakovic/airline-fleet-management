package com.storlakovic.airlineoperationssimulator.aircraft;

import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftDetailsResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftUpdateRequest;
import org.springframework.http.HttpStatus;
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

    @PutMapping("update/{id}")
    public AircraftResponse updateAircraft( @PathVariable Long id,
                                            @RequestBody AircraftUpdateRequest request) {
        return aircraftService.updateAircraft(id, request);
    }

    @GetMapping
    public List<AircraftResponse> getAircraftOverview() {
        return aircraftService.getAll();
    }

    @GetMapping("/{id}")
    public AircraftDetailsResponse getAircraftById(@PathVariable Long id) {
        return aircraftService.getAircraftById(id);
    }

    @DeleteMapping("remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);
    }
}

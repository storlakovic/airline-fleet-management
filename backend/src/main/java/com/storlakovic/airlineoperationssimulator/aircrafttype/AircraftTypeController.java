package com.storlakovic.airlineoperationssimulator.aircrafttype;

import com.storlakovic.airlineoperationssimulator.aircrafttype.dto.AircraftTypeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/aircraft-types")
public class AircraftTypeController {
    private final AircraftTypeService aircraftTypeService;

    AircraftTypeController(AircraftTypeService aircraftTypeService) {
        this.aircraftTypeService = aircraftTypeService;
    }

    @GetMapping
    List<AircraftTypeResponse> getAllAircraftTypes(){
        return aircraftTypeService.getAll();
    }

    @PostMapping("/import")
    List<AircraftType> importAllAircraftTypes(){
        return aircraftTypeService.importAircraftTypes();
    }
}

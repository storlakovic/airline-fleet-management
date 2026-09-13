package com.storlakovic.airlineoperationssimulator.aircrafttype;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/aircraft-types")
public class AircraftTypeController {
    private final AircraftTypeService aircraftTypeService;

    AircraftTypeController(AircraftTypeRepository repository,  AircraftTypeService aircraftTypeService) {
        this.aircraftTypeService = aircraftTypeService;
    }

    @GetMapping(path = "/import")
    List<AircraftType> importAllAircraftTypes() throws IOException {
        return aircraftTypeService.importAircraftTypes();
    }
}

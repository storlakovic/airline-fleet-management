package com.storlakovic.airlineoperationssimulator.simulation.clock;

import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockResponse;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/clock")
public class SimulationClockController {
    private final SimulationClockService simulationClockService;

    public SimulationClockController(SimulationClockService simulationClockService) {
        this.simulationClockService = simulationClockService;
    }

    @GetMapping(path = "/now")
    public ClockResponse getCurrentTime() {
        return simulationClockService.getCurrentTime();
    }

    @PostMapping(path = "/set")
    public ClockResponse setSimulatorTime(OffsetDateTime dateTime) {
        return simulationClockService.setSimulatorTime(dateTime);
    }

    @PutMapping(path = "/{multiplier}")
    public ClockResponse setSpeedMultiplier(@RequestParam Long multiplier) {
        return simulationClockService.setSpeedMultiplier(multiplier);
    }
}

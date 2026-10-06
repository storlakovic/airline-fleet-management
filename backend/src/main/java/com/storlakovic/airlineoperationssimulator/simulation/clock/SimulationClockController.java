package com.storlakovic.airlineoperationssimulator.simulation.clock;

import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockResponse;
import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation/clock")
public class SimulationClockController {
    private final SimulationClockService simulationClockService;

    public SimulationClockController(SimulationClockService simulationClockService) {
        this.simulationClockService = simulationClockService;
    }

    @GetMapping()
    public ClockResponse getCurrentTime() {
        return simulationClockService.getCurrentTime();
    }

    @PutMapping(path = "/time")
    public ClockResponse setSimulatorTime(@Valid @RequestBody ClockUpdateRequest clockUpdateRequest) {
        return simulationClockService.setSimulatorTime(clockUpdateRequest);
    }

    @PutMapping(path = "/speed/{multiplier}")
    public ClockResponse setSpeedMultiplier(@PathVariable Long multiplier) {
        return simulationClockService.setSpeedMultiplier(multiplier);
    }
}

























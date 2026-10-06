package com.storlakovic.airlineoperationssimulator.simulation.clock;

import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockResponse;
import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public class SimulationClockService {

    public ClockResponse getCurrentTime() {
        return ClockResponse.from(SimulationClock.now(), SimulationClock.speedMultiplier);
    }

    public ClockResponse setSimulatorTime(ClockUpdateRequest clockUpdateRequest) {
        SimulationClock.setTime(clockUpdateRequest.dateTime());
        return ClockResponse.from(SimulationClock.now(), SimulationClock.speedMultiplier);
    }

    public ClockResponse setSpeedMultiplier(Long multiplier) {
        SimulationClock.setMultiplier(multiplier);
        return ClockResponse.from(SimulationClock.now(), SimulationClock.speedMultiplier);
    }
}

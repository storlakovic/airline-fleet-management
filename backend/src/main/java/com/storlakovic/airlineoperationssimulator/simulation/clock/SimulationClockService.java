package com.storlakovic.airlineoperationssimulator.simulation.clock;

import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class SimulationClockService {

    public ClockResponse getCurrentTime() {
        return ClockResponse.from(SimulationClock.now(), SimulationClock.speedMultiplier);
    }

    public ClockResponse setSimulatorTime(OffsetDateTime dateTime) {
        SimulationClock.setTime(dateTime);
        return ClockResponse.from(SimulationClock.now(), SimulationClock.speedMultiplier);
    }

    public ClockResponse setSpeedMultiplier(Long multiplier) {
        SimulationClock.setMultiplier(multiplier);
        return ClockResponse.from(SimulationClock.now(), SimulationClock.speedMultiplier);
    }
}

package com.storlakovic.airlineoperationssimulator.simulation.clock;

import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockResponse;
import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SimulationClockServiceTest extends ClockTestSupport {
    private final SimulationClockService service = new SimulationClockService();

    @Test
    void readsActualSimulationTimeAndMultiplier() {
        SimulationClock.setMultiplier(4);
        realNow = realNow.plusSeconds(3);
        assertThat(service.getCurrentTime()).isEqualTo(new ClockResponse(SIMULATION_START.plusSeconds(12), 4L));
    }

    @Test
    void setsTimeAndReturnsUpdatedResponseWithExistingSpeed() {
        SimulationClock.setMultiplier(2);
        var requested = SIMULATION_START.plusYears(1);
        assertThat(service.setSimulatorTime(new ClockUpdateRequest(requested)))
                .isEqualTo(new ClockResponse(requested, 2L));
        assertThat(SimulationClock.now()).isEqualTo(requested);
    }

    @Test
    void changesSpeedWithoutJumpingAndUsesItForSubsequentReads() {
        realNow = realNow.plusSeconds(5);
        assertThat(service.setSpeedMultiplier(10L))
                .isEqualTo(new ClockResponse(SIMULATION_START.plusSeconds(5), 10L));
        realNow = realNow.plusSeconds(2);
        assertThat(service.getCurrentTime())
                .isEqualTo(new ClockResponse(SIMULATION_START.plusSeconds(25), 10L));
    }

    @Test
    void propagatesInvalidSpeedWithoutChangingClock() {
        assertThatThrownBy(() -> service.setSpeedMultiplier(0L)).isInstanceOf(IllegalArgumentException.class);
        assertThat(service.getCurrentTime()).isEqualTo(new ClockResponse(SIMULATION_START, 1L));
    }
}

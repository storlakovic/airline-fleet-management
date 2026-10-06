package com.storlakovic.airlineoperationssimulator.simulation.clock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class SimulationClockTest extends ClockTestSupport {
    @Test
    void returnsConfiguredTimeWithoutElapsedRealTime() {
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START);
        assertThat(SimulationClock.getSpeedMultiplier()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 60, 3600})
    void scalesElapsedTimeIncludingNanosecondsAndDayRollover(long multiplier) {
        SimulationClock.setMultiplier(multiplier);
        realNow = REAL_START.plusSeconds(65).plusNanos(123_456_789);
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START
                .plusSeconds(65 * multiplier).plusNanos(123_456_789 * multiplier));
        assertThat(SimulationClock.now().getOffset()).isEqualTo(SIMULATION_START.getOffset());
    }

    @Test
    void changingSpeedPreservesElapsedSimulationTime() {
        SimulationClock.setMultiplier(10);
        realNow = REAL_START.plusSeconds(5);
        SimulationClock.setMultiplier(2);
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START.plusSeconds(50));
        realNow = realNow.plusSeconds(3);
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START.plusSeconds(56));
        assertThat(SimulationClock.getSpeedMultiplier()).isEqualTo(2);
    }

    @Test
    void repeatedSpeedChangesAccumulateEachIntervalAtItsOwnSpeed() {
        realNow = realNow.plusSeconds(10);
        SimulationClock.setMultiplier(3);
        realNow = realNow.plusSeconds(4);
        SimulationClock.setMultiplier(3);
        realNow = realNow.plusSeconds(2);
        SimulationClock.setMultiplier(1);
        realNow = realNow.plusSeconds(5);
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START.plusSeconds(33));
    }

    @Test
    void settingTimeRebasesClockWithoutResettingSpeed() {
        SimulationClock.setMultiplier(5);
        realNow = realNow.plusHours(1);
        var replacement = SIMULATION_START.minusYears(2);
        SimulationClock.setTime(replacement);
        assertThat(SimulationClock.now()).isEqualTo(replacement);
        realNow = realNow.plusSeconds(2);
        assertThat(SimulationClock.now()).isEqualTo(replacement.plusSeconds(10));
        assertThat(SimulationClock.getSpeedMultiplier()).isEqualTo(5);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositiveSpeedWithoutChangingClock(long multiplier) {
        SimulationClock.setMultiplier(3);
        realNow = realNow.plusSeconds(4);
        assertThatThrownBy(() -> SimulationClock.setMultiplier(multiplier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Speed multiplier must be greater than 0");
        assertThat(SimulationClock.getSpeedMultiplier()).isEqualTo(3);
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START.plusSeconds(12));
        realNow = realNow.plusSeconds(2);
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START.plusSeconds(18));
    }

    @Test
    void readsDoNotAdvanceTimeOnTheirOwn() {
        realNow = realNow.plusSeconds(3);
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START.plusSeconds(3));
        assertThat(SimulationClock.now()).isEqualTo(SIMULATION_START.plusSeconds(3));
    }
}

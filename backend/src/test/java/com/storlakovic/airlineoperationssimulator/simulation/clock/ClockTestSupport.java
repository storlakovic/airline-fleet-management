package com.storlakovic.airlineoperationssimulator.simulation.clock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

/** Controls real time without sleeps and restores the shared static clock after each test. */
@ResourceLock("SimulationClock")
abstract class ClockTestSupport {
    protected static final OffsetDateTime REAL_START = OffsetDateTime.parse("2026-10-06T10:00:00Z");
    protected static final OffsetDateTime SIMULATION_START = OffsetDateTime.parse("2030-01-01T23:59:00+02:00");
    protected OffsetDateTime realNow;
    private MockedStatic<OffsetDateTime> time;
    private Object previousRealTime;
    private Object previousSimulationTime;
    private long previousMultiplier;

    @BeforeEach
    void controlTime() {
        previousMultiplier = SimulationClock.getSpeedMultiplier();
        previousRealTime = ReflectionTestUtils.getField(SimulationClock.class, "baseRealTime");
        previousSimulationTime = ReflectionTestUtils.getField(SimulationClock.class, "baseSimulationTime");
        realNow = REAL_START;
        time = mockStatic(OffsetDateTime.class, CALLS_REAL_METHODS);
        time.when(OffsetDateTime::now).thenAnswer(invocation -> realNow);
        SimulationClock.setMultiplier(1);
        SimulationClock.setTime(SIMULATION_START);
    }

    @AfterEach
    void restoreTime() {
        try {
            ReflectionTestUtils.setField(SimulationClock.class, "baseRealTime", previousRealTime);
            ReflectionTestUtils.setField(SimulationClock.class, "baseSimulationTime", previousSimulationTime);
            ReflectionTestUtils.setField(SimulationClock.class, "speedMultiplier", previousMultiplier);
        } finally {
            if (time != null) time.close();
        }
    }
}

package com.storlakovic.airlineoperationssimulator.simulation.clock;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
public class SimulationClock {

    private static OffsetDateTime baseRealTime = OffsetDateTime.now();
    private static OffsetDateTime baseSimulationTime = baseRealTime;
    public static long speedMultiplier = 1;

    public static void setTime(OffsetDateTime time) {
        baseRealTime = OffsetDateTime.now();
        baseSimulationTime = time;
    }

    public static OffsetDateTime now() {
        Duration elapsedRealTime =
                Duration.between(baseRealTime, OffsetDateTime.now());

        return baseSimulationTime.plus(
                elapsedRealTime.multipliedBy(speedMultiplier)
        );
    }

    private static void resetBaseTime() {
        baseSimulationTime = now();
        baseRealTime = OffsetDateTime.now();
    }

    public static void setMultiplier(long newMultiplier) {
        if (newMultiplier <= 0) {
            throw new IllegalArgumentException(
                    "Speed multiplier must be greater than 0"
            );
        }

        resetBaseTime();
        speedMultiplier = newMultiplier;
    }
}

package com.storlakovic.airlineoperationssimulator.simulation.clock.dto;

import java.time.OffsetDateTime;

public record ClockResponse(OffsetDateTime dateTime, Long multiplier) {

    public static ClockResponse from(OffsetDateTime dateTime, Long multiplier) {
        return new ClockResponse(dateTime, multiplier);
    }
}

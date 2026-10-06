package com.storlakovic.airlineoperationssimulator.simulation.clock.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ClockUpdateRequest(@NotNull OffsetDateTime dateTime) {
}

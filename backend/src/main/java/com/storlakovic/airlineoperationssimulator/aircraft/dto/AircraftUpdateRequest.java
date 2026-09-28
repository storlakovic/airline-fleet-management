package com.storlakovic.airlineoperationssimulator.aircraft.dto;

import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;
import jakarta.validation.constraints.NotNull;

public record AircraftUpdateRequest(@NotNull AircraftStatus status) {
}

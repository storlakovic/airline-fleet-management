package com.storlakovic.airlineoperationssimulator.airport.dto;

import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;
import jakarta.validation.constraints.NotNull;

public record AirportUpdateRequest(@NotNull AirportStatus status) {

}

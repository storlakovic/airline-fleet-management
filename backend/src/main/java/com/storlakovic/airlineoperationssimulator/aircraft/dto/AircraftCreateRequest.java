package com.storlakovic.airlineoperationssimulator.aircraft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AircraftCreateRequest(@NotNull Long aircraftTypeId, @NotBlank @Size(max = 10) String registration) {

}

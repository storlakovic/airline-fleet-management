package com.storlakovic.airlineoperationssimulator.aircraft.dto;

import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;

public record AircraftUpdateRequest(AircraftStatus status) {
}

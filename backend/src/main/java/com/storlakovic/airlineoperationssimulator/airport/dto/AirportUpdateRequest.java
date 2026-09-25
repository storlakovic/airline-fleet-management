package com.storlakovic.airlineoperationssimulator.airport.dto;

import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;

public record AirportUpdateRequest(AirportStatus status) {
}

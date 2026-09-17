package com.storlakovic.airlineoperationssimulator.airport.dto;

import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;

public class AirportUpdateRequest {
    private final AirportStatus status;

    public AirportUpdateRequest(AirportStatus status) {
        this.status = status;
    }

    public AirportStatus getStatus() {
        return status;
    }
}

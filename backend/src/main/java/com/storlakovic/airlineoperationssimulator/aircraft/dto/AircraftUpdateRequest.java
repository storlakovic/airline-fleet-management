package com.storlakovic.airlineoperationssimulator.aircraft.dto;

import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;

public class AircraftUpdateRequest {
    private final AircraftStatus status;

    public AircraftUpdateRequest(AircraftStatus status) {
        this.status = status;
    }
    public AircraftStatus getStatus() {
        return status;
    }
}

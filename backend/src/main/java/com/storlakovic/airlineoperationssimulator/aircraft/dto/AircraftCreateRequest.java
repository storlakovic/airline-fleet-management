package com.storlakovic.airlineoperationssimulator.aircraft.dto;

public class AircraftCreateRequest {

    private final Long aircraftTypeId;
    private final String registration;

    public AircraftCreateRequest(Long aircraftTypeId, String registration) {
        this.aircraftTypeId = aircraftTypeId;
        this.registration = registration;
    }

    public Long getAircraftTypeId() {
        return aircraftTypeId;
    }

    public String getRegistration() {
        return registration;
    }
}

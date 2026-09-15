package com.storlakovic.airlineoperationssimulator.aircraft.dto;

import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;

public class AircraftDetailsResponse {
    private final Long id;
    private final String registration;
    private final Long aircraftTypeId;
    private final String aircraftTypeModel;
    private final String icaoCode;
    private final String manufacturer;
    private final AircraftStatus status;

    public AircraftDetailsResponse(Long id, String registration, Long aircraftTypeId, String aircraftTypeModel, String icaoCode, String manufacturer, AircraftStatus status) {
        this.id = id;
        this.registration = registration;
        this.aircraftTypeId = aircraftTypeId;
        this.aircraftTypeModel = aircraftTypeModel;
        this.icaoCode = icaoCode;
        this.manufacturer = manufacturer;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getRegistration() {
        return registration;
    }

    public Long getAircraftTypeId() {
        return aircraftTypeId;
    }

    public String getAircraftTypeModel() {
        return aircraftTypeModel;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public AircraftStatus getStatus() {
        return status;
    }
}

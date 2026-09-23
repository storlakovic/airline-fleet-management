package com.storlakovic.airlineoperationssimulator.aircraft.dto;

import com.storlakovic.airlineoperationssimulator.aircraft.Aircraft;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;

public class AircraftResponse {
    private final Long id;
    private final String registration;
    private final Long aircraftTypeId;
    private final String aircraftTypeModel;
    private final String icaoCode;
    private final AircraftStatus status;

    public AircraftResponse(Long id, String registration, Long aircraftTypeId, String aircraftTypeModel, String icaoCode, AircraftStatus status) {
        this.id = id;
        this.registration = registration;
        this.aircraftTypeId = aircraftTypeId;
        this.aircraftTypeModel = aircraftTypeModel;
        this.icaoCode = icaoCode;
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

    public AircraftStatus getStatus() {
        return status;
    }

    public static AircraftResponse from(Aircraft aircraft) {
        return new AircraftResponse(
                aircraft.getId(),
                aircraft.getRegistration(),
                aircraft.getAircraftType().getId(),
                aircraft.getAircraftType().getModel(),
                aircraft.getAircraftType().getIcaoCode(),
                aircraft.getStatus()
        );
    }
}

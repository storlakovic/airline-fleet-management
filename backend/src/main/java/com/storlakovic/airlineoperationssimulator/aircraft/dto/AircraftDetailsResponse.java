package com.storlakovic.airlineoperationssimulator.aircraft.dto;

import com.storlakovic.airlineoperationssimulator.aircraft.Aircraft;
import com.storlakovic.airlineoperationssimulator.aircraft.AircraftStatus;

public record AircraftDetailsResponse(Long id, String registration, Long aircraftTypeId, String aircraftTypeModel,
                                      String icaoCode, String manufacturer, AircraftStatus status) {

    public static AircraftDetailsResponse from(Aircraft aircraft) {
        return new AircraftDetailsResponse(aircraft.getId(), aircraft.getRegistration(), aircraft.getAircraftType().getId(), aircraft.getAircraftType().getModel(), aircraft.getAircraftType().getIcaoCode(), aircraft.getAircraftType().getManufacturer(), aircraft.getStatus());
    }
}

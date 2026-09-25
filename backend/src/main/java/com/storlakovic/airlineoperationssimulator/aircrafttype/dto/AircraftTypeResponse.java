package com.storlakovic.airlineoperationssimulator.aircrafttype.dto;

import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;

public record AircraftTypeResponse(Long id, String manufacturer, String model, String icaoCode) {

    public static AircraftTypeResponse from(AircraftType aircraftType) {
        return new AircraftTypeResponse(
                aircraftType.getId(),
                aircraftType.getManufacturer(),
                aircraftType.getModel(),
                aircraftType.getIcaoCode()
        );
    }
}

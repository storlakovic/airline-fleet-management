package com.storlakovic.airlineoperationssimulator.aircrafttype.dto;

public class AircraftTypeResponse {
    private final Long id;
    private final String manufacturer;
    private final String model;
    private final String icaoCode;

    public AircraftTypeResponse(Long id, String manufacturer, String model, String icaoCode) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.icaoCode = icaoCode;
    }

    public Long getId() {
        return id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getIcaoCode() {
        return icaoCode;
    }
}

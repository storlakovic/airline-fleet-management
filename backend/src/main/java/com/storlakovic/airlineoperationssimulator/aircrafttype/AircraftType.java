package com.storlakovic.airlineoperationssimulator.aircrafttype;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "aircraft_type")
public class AircraftType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String manufacturer;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String model;

    @Column(nullable = false, unique = true, length = 4)
    @NotBlank
    private String icaoCode;

    protected AircraftType() {}

    public AircraftType(String manufacturer, String model, String icaoCode) {
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

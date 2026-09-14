package com.storlakovic.airlineoperationssimulator.aircraft;


import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "aircraft")
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 10)
    private String registration;

    @ManyToOne(optional = false)
    private AircraftType aircraftType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AircraftStatus status = AircraftStatus.IN_SERVICE;

    protected Aircraft() {}

    public Aircraft(AircraftType aircraftType, String registration) {
        this.registration = registration;
        this.aircraftType = aircraftType;
    }

    public Long getId() {
        return id;
    }

    public String getRegistration() {
        return registration;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public AircraftStatus getStatus() {
        return status;
    }
}

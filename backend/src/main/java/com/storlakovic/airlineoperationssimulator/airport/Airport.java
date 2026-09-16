package com.storlakovic.airlineoperationssimulator.airport;

import jakarta.persistence.*;

@Entity
@Table(name = "airport")
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 4)
    private String icaoCode;

    @Column(length = 3)
    private String iataCode;

    @Column(nullable = false)
    private String name;

    private String city;

    @Column(nullable = false, length = 2)
    private String countryCode;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AirportStatus status = AirportStatus.OPERATIONAL;

    public Airport(String icaoCode, String iataCode, String name, String city, String countryCode, Double latitude, Double longitude, String type, AirportStatus status) {
        this.icaoCode = icaoCode;
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airport)) return false;
        Airport airport = (Airport) o;
        return icaoCode != null && icaoCode.equals(airport.icaoCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Airport() {}

    public String getIcaoCode() {
        return icaoCode;
    }

    public String getIataCode() {
        return iataCode;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public AirportStatus getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }

}

package com.storlakovic.airlineoperationssimulator.flight;


import com.storlakovic.airlineoperationssimulator.route.Route;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

import static com.storlakovic.airlineoperationssimulator.common.constants.DEFAULT_AIRLINE;

@Entity
@Table(name = "flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String flightNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private OffsetDateTime scheduledDepartureTime;

    @Column(nullable = false)
    private OffsetDateTime scheduledArrivalTime;

    private OffsetDateTime actualDepartureTime;

    private OffsetDateTime actualArrivalTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.UNKNOWN;

    @Column(nullable = false)
    private String airline; // Emirates will be standard Airline for Flights

    public Flight() {}

    public Flight(String flightNumber, Route route, OffsetDateTime scheduledDepartureTime, OffsetDateTime scheduledArrivalTime) {
        this.flightNumber = flightNumber;
        this.route = route;
        this.scheduledDepartureTime = scheduledDepartureTime;
        this.scheduledArrivalTime = scheduledArrivalTime;
        this.airline = DEFAULT_AIRLINE;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public Route getRoute() {
        return route;
    }

    public OffsetDateTime getScheduledDepartureTime() {
        return scheduledDepartureTime;
    }

    public OffsetDateTime getScheduledArrivalTime() {
        return scheduledArrivalTime;
    }

    public OffsetDateTime getActualDepartureTime() {
        return actualDepartureTime;
    }

    public OffsetDateTime getActualArrivalTime() {
        return actualArrivalTime;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public String getAirline() {
        return airline;
    }

    public Long getId() {
        return id;
    }
}

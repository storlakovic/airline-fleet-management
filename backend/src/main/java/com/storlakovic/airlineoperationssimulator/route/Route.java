package com.storlakovic.airlineoperationssimulator.route;

import com.storlakovic.airlineoperationssimulator.airport.Airport;
import jakarta.persistence.*;

@Entity
@Table(name = "route", uniqueConstraints = @UniqueConstraint(
        name = "uc_route_origin_destination",
        columnNames = {"origin_id", "destination_id"}
))
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "origin_id", nullable = false)
    private Airport origin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private Airport destination;

    public Route(Airport origin, Airport destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Route() {}

    public Airport getOrigin() {
        return origin;
    }

    public Airport getDestination() {
        return destination;
    }

    public Long getId() {
        return id;
    }
}

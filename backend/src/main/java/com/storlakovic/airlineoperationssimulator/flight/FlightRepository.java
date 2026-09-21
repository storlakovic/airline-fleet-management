package com.storlakovic.airlineoperationssimulator.flight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByAircraft_Id(Long aircraftId);
}

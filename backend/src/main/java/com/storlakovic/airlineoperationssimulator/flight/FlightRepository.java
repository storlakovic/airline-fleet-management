package com.storlakovic.airlineoperationssimulator.flight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByAircraft_Id(Long aircraftId);
    List<Flight> findByStatusAndScheduledDepartureTimeBefore(FlightStatus status, OffsetDateTime time);
    List<Flight> findByStatusAndScheduledArrivalTimeBefore(FlightStatus status, OffsetDateTime time);
}

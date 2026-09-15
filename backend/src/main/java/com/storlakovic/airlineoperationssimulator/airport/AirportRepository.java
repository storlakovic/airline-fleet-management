package com.storlakovic.airlineoperationssimulator.airport;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    boolean existsByIcaoCode(String icaoCode);
}

package com.storlakovic.airlineoperationssimulator.aircraft;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    boolean existsAircraftByRegistration(String registration);
}

package com.storlakovic.airlineoperationssimulator.route;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    boolean existsByOrigin_IdAndDestination_Id(Long originId, Long destinationId);
}

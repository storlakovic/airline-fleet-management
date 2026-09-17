package com.storlakovic.airlineoperationssimulator.airport.dto;

import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;

public record AirportResponse (
     Long id,
     String icaoCode,
     String iataCode,
     String type,
     AirportStatus status
){}

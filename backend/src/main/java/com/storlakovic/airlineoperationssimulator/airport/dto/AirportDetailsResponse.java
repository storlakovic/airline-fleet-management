package com.storlakovic.airlineoperationssimulator.airport.dto;

import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;

public record AirportDetailsResponse(
    Long id,
    String icaoCode,
    String iataCode,
    String name,
    String city,
    String countryCode,
    Double latitude,
    Double longitude,
    String type,
    AirportStatus status
){}

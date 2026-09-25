package com.storlakovic.airlineoperationssimulator.airport.dto;

import com.storlakovic.airlineoperationssimulator.airport.Airport;
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
){
    public static AirportDetailsResponse from(Airport airport) {
        return new AirportDetailsResponse(airport.getId(), airport.getIcaoCode(), airport.getIataCode(), airport.getName(), airport.getCity(), airport.getCountryCode(), airport.getLatitude(), airport.getLongitude(), airport.getType(), airport.getStatus());
    }
}

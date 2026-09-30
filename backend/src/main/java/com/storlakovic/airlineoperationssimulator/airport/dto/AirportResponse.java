package com.storlakovic.airlineoperationssimulator.airport.dto;

import com.storlakovic.airlineoperationssimulator.airport.Airport;
import com.storlakovic.airlineoperationssimulator.airport.AirportStatus;

public record AirportResponse (
     Long id,
     String icaoCode,
     String iataCode,
     String name,
     String city,
     String countryCode,
     AirportStatus status,
     String type
){
    public static AirportResponse from(Airport airport){
        return new AirportResponse(airport.getId(), airport.getIcaoCode(), airport.getIataCode(), airport.getName(), airport.getCity(), airport.getCountryCode(), airport.getStatus(), airport.getType());
    }
}

package com.storlakovic.airlineoperationssimulator.simulation;


import com.storlakovic.airlineoperationssimulator.flight.Flight;
import com.storlakovic.airlineoperationssimulator.flight.FlightStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class FlightProgression {

    public Flight progress(Flight flight, OffsetDateTime dateTime) {

        boolean toBoard =
                !flight.getScheduledDepartureTime()
                        .isAfter(dateTime.plusMinutes(10));

        boolean toDepart =
                !flight.getScheduledDepartureTime()
                        .isAfter(dateTime);

        boolean toApproach =
                !flight.getScheduledArrivalTime()
                        .isAfter(dateTime.plusMinutes(10));

        boolean toLand =
                !flight.getScheduledArrivalTime()
                        .isAfter(dateTime);

        if (flight.getStatus() == FlightStatus.SCHEDULED && toBoard) {
            flight.setStatus(FlightStatus.BOARDING);
        } else if (flight.getStatus() == FlightStatus.BOARDING && toDepart) {
            flight.setActualDepartureTime(dateTime);
            flight.setStatus(FlightStatus.EN_ROUTE);
        } else if (flight.getStatus() == FlightStatus.EN_ROUTE && toApproach) {
            flight.setStatus(FlightStatus.APPROACH);
        } else if (flight.getStatus() == FlightStatus.APPROACH && toLand) {
            flight.setActualArrivalTime(dateTime);
            flight.setStatus(FlightStatus.LANDED);
        }

        return flight;
    }

}

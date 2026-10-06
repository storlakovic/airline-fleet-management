import type { FlightResponse } from '../../models/responses/FlightResponse.ts';
import type { AirportDetailsResponse } from '../../models/responses/AirportResponse.ts';

export function routeData(flights: FlightResponse[], airports: AirportDetailsResponse[]) {
  const byCode = new Map(airports.map((airport) => [airport.icaoCode, airport]));
  return flights.flatMap((flight) => {
    const origin = byCode.get(flight.originIcaoCode);
    const destination = byCode.get(flight.destinationIcaoCode);
    if (
      origin?.latitude == null ||
      origin.longitude == null ||
      destination?.latitude == null ||
      destination.longitude == null
    )
      return [];
    return [
      {
        startLat: origin.latitude,
        startLng: origin.longitude,
        endLat: destination.latitude,
        endLng: destination.longitude,
      },
    ];
  });
}

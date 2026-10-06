// Fixtures follow the backend JSON records; application code never imports these.
export const airportResponses = [
  {
    id: 1,
    icaoCode: 'LOWW',
    iataCode: 'VIE',
    name: 'Vienna Airport',
    city: 'Vienna',
    countryCode: 'AT',
    status: 'OPERATIONAL',
    type: 'large_airport',
    latitude: 48,
    longitude: 16,
  },
  {
    id: 2,
    icaoCode: 'KJFK',
    iataCode: 'JFK',
    name: 'John F. Kennedy Airport',
    city: 'New York',
    countryCode: 'US',
    status: 'OPERATIONAL',
    type: 'large_airport',
    latitude: 40,
    longitude: -73,
  },
];
export const airports = Object.fromEntries(
  airportResponses.map((airport) => [airport.icaoCode, airport]),
);
export const flightResponse = {
  id: 1,
  flightNumber: 'OS1',
  routeId: 10,
  originIcaoCode: 'LOWW',
  destinationIcaoCode: 'KJFK',
  scheduledDepartureTime: '2026-10-02T10:00:00Z',
  scheduledArrivalTime: '2026-10-02T13:00:00Z',
  status: 'SCHEDULED',
  aircraftIcaoCode: null,
  aircraftRegistration: null,
};

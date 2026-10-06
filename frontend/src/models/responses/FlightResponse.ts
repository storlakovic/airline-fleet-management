export type FlightStatus =
  | 'SCHEDULED'
  | 'BOARDING'
  | 'EN_ROUTE'
  | 'APPROACH'
  | 'LANDED'
  | 'DELAYED'
  | 'CANCELLED'
  | 'UNKNOWN';
export interface FlightResponse {
  id: number;
  flightNumber: string;
  routeId: number;
  originIcaoCode: string;
  destinationIcaoCode: string;
  scheduledDepartureTime: string;
  scheduledArrivalTime: string;
  status: FlightStatus;
  aircraftIcaoCode: string | null;
  aircraftRegistration: string | null;
}

export interface FlightDetailedResponse {
  id: number;
  flightNumber: string;
  originIcaoCode: string;
  originName: string;
  destinationIcaoCode: string;
  destinationName: string;
  scheduledDepartureTime: string;
  scheduledArrivalTime: string;
  actualDepartureTime: string | null;
  actualArrivalTime: string | null;
  status: FlightStatus;
  aircraftIcaoCode: string | null;
  aircraftRegistration: string | null;
}

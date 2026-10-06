export type AirportStatus = 'OPERATIONAL' | 'CLOSED';

export interface AirportResponse {
  id: number;
  icaoCode: string;
  iataCode: string | null;
  name: string;
  city: string | null;
  countryCode: string | null;
  status: AirportStatus;
  type: string;
}

export interface AirportDetailsResponse extends AirportResponse {
  latitude: number | null;
  longitude: number | null;
}

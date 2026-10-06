import type { FlightResponse } from '../../models/responses/FlightResponse.ts';
import { formatTime } from '../../utils/date.ts';

interface FlightTableProps {
  flights: FlightResponse[];
  emptyMessage: string;
}

export default function FlightTable({ flights, emptyMessage }: FlightTableProps) {
  return (
    <div className="table-scroll">
      <table>
        <thead>
          <tr>
            <th>FLIGHT</th>
            <th>ROUTE</th>
            <th>DEPARTURE</th>
            <th>ARRIVAL</th>
            <th>AIRCRAFT</th>
            <th>STATUS</th>
          </tr>
        </thead>
        <tbody>
          {flights.map((flight) => (
            <tr key={flight.id}>
              <td>
                <b>{flight.flightNumber}</b>
              </td>
              <td>
                {flight.originIcaoCode} → {flight.destinationIcaoCode}
              </td>
              <td className="mono">{formatTime(flight.scheduledDepartureTime)}</td>
              <td className="mono">{formatTime(flight.scheduledArrivalTime)}</td>
              <td>
                {flight.aircraftIcaoCode || 'Unassigned'}
                <small>{flight.aircraftRegistration || '—'}</small>
              </td>
              <td>
                <span className={`status ${flight.status}`}>
                  <i />
                  {flight.status.replaceAll('_', ' ')}
                </span>
              </td>
            </tr>
          ))}
          {!flights.length && (
            <tr>
              <td colSpan={6} className="empty">
                {emptyMessage}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

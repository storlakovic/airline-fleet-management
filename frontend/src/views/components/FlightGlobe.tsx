import type { FlightResponse } from '../../models/responses/FlightResponse.ts';
import type {
  AirportResponse,
  AirportDetailsResponse,
} from '../../models/responses/AirportResponse.ts';
import { useEffect, useRef, useState } from 'react';
import { createGlobe } from '../globe/globe.ts';

interface FlightGlobeProps {
  flights: FlightResponse[];
}

export default function FlightGlobe({ flights }: FlightGlobeProps) {
  const container = useRef<HTMLDivElement>(null);
  const globe = useRef<ReturnType<typeof createGlobe> | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!container.current) return;
    const instance = createGlobe(container.current);
    globe.current = instance;
    return () => {
      instance.destroy();
      globe.current = null;
    };
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    setError('');
    globe.current?.setFlights([], []);
    if (!flights.length) return () => controller.abort();

    async function loadAirports() {
      try {
        const response = await fetch('/api/airport', { signal: controller.signal });
        if (!response.ok) throw new Error();
        const airports: AirportResponse[] = await response.json();
        const codes = new Set(
          flights.flatMap((flight) => [flight.originIcaoCode, flight.destinationIcaoCode]),
        );
        const needed = airports.filter((airport) => codes.has(airport.icaoCode));
        const details: AirportDetailsResponse[] = [];
        for (const airport of needed) {
          const response = await fetch(`/api/airport/${airport.id}`, { signal: controller.signal });
          if (!response.ok) throw new Error();
          details.push(await response.json());
        }
        if (!controller.signal.aborted) {
          globe.current?.setFlights(flights, details);
          if (
            codes.size !== details.length ||
            details.some((airport) => airport.latitude == null || airport.longitude == null)
          ) {
            setError('Some routes have no airport coordinates. All flights are listed above.');
          }
        }
      } catch {
        if (!controller.signal.aborted) setError('Airport coordinates could not be loaded.');
      }
    }
    void loadAirports();
    return () => controller.abort();
  }, [flights]);

  return (
    <section className="panel network">
      <div className="panel-heading">
        <div>
          <h2>Global network</h2>
          <p>Drag to rotate. Scroll to zoom.</p>
        </div>
      </div>
      <div className="globe-stage">
        <div
          id="globe"
          ref={container}
          role="img"
          aria-label="Interactive globe showing flight routes. Drag to rotate and scroll to zoom."
        />
        {error && (
          <span className="map-error" role="status">
            {error}
          </span>
        )}
        <span className="globe-footnote">FLIGHT ROUTES · NOT LIVE POSITIONS</span>
      </div>
    </section>
  );
}

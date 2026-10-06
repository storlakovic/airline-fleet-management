import { useEffect, useRef, useState } from 'react';
import { createGlobe } from '../globe/globe.ts';

export default function FlightGlobe({ flights }) {
  const container = useRef(null);
  const globe = useRef(null);
  const [error, setError] = useState('');

  useEffect(() => {
    globe.current = createGlobe(container.current);
    return () => {
      globe.current.destroy();
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
        const airports = await response.json();
        const codes = new Set(
          flights.flatMap((flight) => [flight.originIcaoCode, flight.destinationIcaoCode]),
        );
        const needed = airports.filter((airport) => codes.has(airport.icaoCode));
        const details = [];
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

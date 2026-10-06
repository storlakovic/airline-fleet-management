import { useEffect, useState } from 'react';
import type { FlightResponse } from '../models/responses/FlightResponse.ts';

export function useHomeViewModel() {
  const [flights, setFlights] = useState<FlightResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const controller = new AbortController();
    fetch('/api/flight', { signal: controller.signal })
      .then((response) => {
        if (!response.ok) throw new Error('Flights could not be loaded.');
        return response.json();
      })
      .then((data: FlightResponse[]) => {
        if (!Array.isArray(data)) throw new Error('Invalid flight response.');
        if (!controller.signal.aborted) setFlights(data);
      })
      .catch((error: unknown) => {
        if (!controller.signal.aborted) {
          setError(error instanceof Error ? error.message : 'Flights could not be loaded.');
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });
    return () => controller.abort();
  }, []);

  return { flights, loading, error };
}

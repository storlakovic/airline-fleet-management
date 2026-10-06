import test from 'node:test';
import assert from 'node:assert/strict';
import { routeData } from '../src/views/globe/data.ts';
import { flightResponse, airportResponses } from './responses.js';

test('maps every flight to its airport coordinates without modifying responses', () => {
  const flights = [flightResponse, { ...flightResponse, id: 2, status: 'LANDED' }];
  const original = structuredClone(flights);
  assert.deepEqual(routeData(flights, airportResponses), [
    { startLat: 48, startLng: 16, endLat: 40, endLng: -73 },
    { startLat: 48, startLng: 16, endLat: 40, endLng: -73 },
  ]);
  assert.deepEqual(flights, original);
});

test('missing coordinates omit only the map route', () => {
  assert.deepEqual(routeData([flightResponse], []), []);
  assert.deepEqual(
    routeData(
      [flightResponse],
      airportResponses.map((airport) => ({ ...airport, latitude: null })),
    ),
    [],
  );
  assert.deepEqual(routeData([], airportResponses), []);
});

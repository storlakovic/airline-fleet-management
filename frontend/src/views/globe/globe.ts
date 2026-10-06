import type { MeshPhongMaterial } from 'three';
import type { FlightResponse } from '../../models/responses/FlightResponse.ts';
import type { AirportDetailsResponse } from '../../models/responses/AirportResponse.ts';
import Globe, { type GlobeInstance } from 'globe.gl';
import { routeData } from './data.ts';

const INITIAL_VIEW = { lat: 35, lng: 20, altitude: 1.4 };

export function createGlobe(container: HTMLElement) {
  let globe: GlobeInstance;
  try {
    globe = new Globe(container, { animateIn: false });
  } catch {
    container.textContent = 'The globe requires WebGL. Please enable hardware acceleration.';
    return {
      setFlights() {},
      destroy: () => container.replaceChildren(),
    };
  }

  const reducedMotion = matchMedia('(prefers-reduced-motion: reduce)').matches;
  const lifecycle = new AbortController();
  let routeSignature: string | undefined;
  let destroyed = false;

  globe
    .backgroundColor('#00000000')
    .atmosphereColor('#45748b')
    .atmosphereAltitude(0.12)
    .polygonCapColor(() => '#172c37')
    .polygonSideColor(() => '#00000000')
    .polygonStrokeColor(() => '#466573')
    .polygonAltitude(0.001)
    .polygonCapCurvatureResolution(2)
    .polygonLabel(() => '')
    .polygonsTransitionDuration(0)
    .arcColor(() => '#72e0b5')
    .arcAltitudeAutoScale(0.25)
    .arcStroke(0.2)
    .arcLabel(() => '')
    .arcsTransitionDuration(0)
    .pointOfView(INITIAL_VIEW);
  (globe.globeMaterial() as MeshPhongMaterial).color.set('#101f2a');
  globe.controls().autoRotate = !reducedMotion;
  globe.controls().autoRotateSpeed = 0.35;
  globe.controls().enablePan = false;

  fetch('/world.geojson', { signal: lifecycle.signal })
    .then((response) => {
      if (!response.ok) throw new Error('Country outlines unavailable');
      return response.json();
    })
    .then((data) => {
      if (destroyed) return;
      globe.polygonsData(data.features);
    })
    .catch(() => {
      if (destroyed) return;
      const notice = document.createElement('span');
      notice.className = 'map-error';
      notice.textContent = 'Country outlines unavailable';
      container.appendChild(notice);
    });

  function resize() {
    if (container.clientWidth && container.clientHeight) {
      globe.width(container.clientWidth).height(container.clientHeight);
    }
  }
  const observer = new ResizeObserver(resize);
  observer.observe(container);
  resize();

  document.addEventListener(
    'visibilitychange',
    () => {
      if (document.hidden) globe.pauseAnimation();
      else globe.resumeAnimation();
    },
    { signal: lifecycle.signal },
  );

  return {
    setFlights(flights: FlightResponse[], airports: AirportDetailsResponse[]) {
      const data = routeData(flights, airports);
      const signature = JSON.stringify(data);
      if (signature === routeSignature) return;
      routeSignature = signature;
      globe.arcsData(data);
    },
    destroy() {
      if (destroyed) return;
      destroyed = true;
      lifecycle.abort();
      observer.disconnect();
      globe._destructor();
      container.replaceChildren();
    },
  };
}

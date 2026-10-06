# Frontend

React mit TypeScript und TSX: eine Flugtabelle und eine interaktive Globe.gl-Erdkugel.

## Datenfluss

`useHomeViewModel.ts` lädt einmal beim Öffnen mit `fetch('/api/flight')` die Flüge
und liefert `flights`, `loading` und `error`. `HomeView.tsx` stellt diesen Zustand dar
und übergibt die unveränderte `FlightResponse[]` an `FlightTable` und `FlightGlobe`.
Das ViewModel ist ein einfacher React-Hook, ohne zusätzlichen Store oder Abonnementlogik.
Die Tabelle zeigt alle Flüge, einschließlich gelandeter und stornierter Flüge.
Es gibt keinen Filter, keine Suche, keinen Store und keinen Aktualisierungstimer.
Zum erneuten Laden die Seite aktualisieren.

Die Erdkugel benötigt zusätzlich Koordinaten. `FlightGlobe.tsx` lädt dafür
`/api/airport` und die Details `/api/airport/{id}` der beteiligten Flughäfen.
`globe/data.ts` übersetzt diese Responses in die Koordinaten für Globe.gl.
Fehlende Koordinaten betreffen nur die Karte; der Flug bleibt in der Tabelle.
Die Requests werden beim Entfernen der Komponenten abgebrochen.

```text
src/
├── main.tsx
├── models/responses/            # Typen entsprechend den Backend-DTOs
├── viewmodels/useHomeViewModel.ts # Direkter GET und Lade-/Fehlerzustand
├── views/
│   ├── HomeView.tsx             # Darstellung des ViewModel-Zustands
│   ├── components/
│   │   ├── FlightTable.tsx      # flights.map(...) als Tabelle
│   │   └── FlightGlobe.tsx      # Koordinaten laden und Karte anzeigen
│   └── globe/                  # Globe.gl-Konfiguration und Routenkoordinaten
├── utils/date.ts                # UTC-Zeitformatierung
└── styles/main.css
```

Keine Demodaten. Kein Backend bedeutet eine Fehlermeldung statt erfundener Flüge.
Die DTO-Typen bleiben in TypeScript, die React-Komponenten sind TSX.

## Starten und prüfen

```sh
npm install
npm run dev
npm test
npm run typecheck
npm run format:check
npm run build
```

Node.js 24 wird für die Tests benötigt. Vite leitet `/api/*` zum Backend auf
Port 8080 weiter. In Produktion muss der Webserver diesen Proxy bereitstellen.
Die Karte benötigt WebGL und zeigt Routen, keine Live-Flugpositionen.

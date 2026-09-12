

```mermaid
classDiagram
    class Aircraft
    class AircraftType
    class Airport
    class Route
    class Flight

    Aircraft "0..*" --> "1" AircraftType : has type

    Flight "0..*" --> "1" Route : follows
    Flight "0..*" --> "0..1" Aircraft : assigned aircraft

    Route "0..*" --> "1" Airport : origin
    Route "0..*" --> "1" Airport : destination
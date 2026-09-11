

```mermaid
classDiagram
    class Airline
    class Aircraft
    class AircraftType
    class Airport
    class Route
    class Flight

    Airline "1" --> "n" Aircraft : operates
    Airline "1" --> "n" Flight : operates

    Aircraft "n" --> "1" AircraftType : has type

    Flight "n" --> "1" Route : follows
    Flight "n" --> "1" Aircraft : assigned aircraft

    Route "n" --> "1" Airport : origin
    Route "n" --> "1" Airport : destination
# Domain Model

This document describes the core domain concepts of the Airline Operations Simulator, their purpose, responsibilities, and important business rules.

## Aircraft

Represents a specific physical aircraft.

Examples include individual aircraft identified by registrations such as `OE-LPA` or `D-ABCD`.

### Rules

* An aircraft must have exactly one aircraft type.
* An aircraft has a unique registration.
* An aircraft can be assigned to flights if it is operational and available.

## Aircraft Type

Represents the technical type or model of an aircraft.

Examples include:

* Boeing 777-300ER
* Boeing 787-9
* Airbus A320neo

### Rules

* An aircraft type can be used by many individual aircraft.
* An aircraft type defines technical characteristics shared by aircraft of that type.
* These characteristics may include range, capacity, cruise speed, and other operational limitations.

## Airport

Represents a physical airport from which flights can depart or at which flights can arrive.

### Rules

* An airport has a unique identifier such as an IATA or ICAO code.
* An airport can be the origin or destination of multiple routes and flights.
* An airport has an operational status representing its current availability for operations.

## Route

Represents a connection operated by an airline between an origin airport and a destination airport.

Example:

`VIE → JFK`

### Rules

* A route must have exactly one origin airport.
* A route must have exactly one destination airport.
* The origin and destination must be different airports.

## Flight

Represents a scheduled operation of an airline on a specific route at a specific time.

Example:

`OS87 – Vienna to New York – September 11, 2026`

### Rules

* A flight must be associated with exactly one route.
* A flight has a scheduled departure and arrival time.
* A flight may have an aircraft assigned to it.
* An aircraft assignment must satisfy the operational requirements of the flight.
* A flight has an operational state such as scheduled, boarding, departed, arrived, delayed, or cancelled.

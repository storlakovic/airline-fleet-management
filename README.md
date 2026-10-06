# Airline Operations Simulator

A simulation project for modeling the day-to-day operations of an airline.

The goal is to represent core airline operations such as aircraft management, airport and route management, flight scheduling, aircraft assignment, operational states, and conflict detection.

The project is also intended to demonstrate a structured software engineering process including domain modeling, architecture decisions, testing, documentation, and iterative development.

## Current Status

The initial backend MVP is complete.

The main domain features have been implemented and reviewed, and the original MVP codebase has gone through a dedicated refactoring phase to improve consistency, feature ownership, validation, and maintainability.

An initial simulation feature is also implemented and provides automatic flight status progression.

The next major development area is the user interface together with further expansion of the simulation system.

## MVP Scope

The first version focuses on the core airline operations domain:

- Aircraft types
- Aircraft fleet management
- Airports
- Routes
- Scheduled flights
- Aircraft assignment
- Basic flight states
- Detection of invalid or conflicting operations

More complex areas such as crew scheduling, passenger management, weather, maintenance planning, airport slots, and financial simulation are planned for later versions.

## Tech Stack

### Backend

- Java 25
- Spring Boot 4.1.x
- Maven
- Spring Web MVC
- Spring Data JPA
- Bean Validation

### Database

- PostgreSQL

### Infrastructure

- Docker for local database infrastructure

## Architecture

The application is organized primarily by business feature

## Operations UI

The frontend contains one React homepage with a flight timetable and a
Globe.gl route map. Flight data comes exclusively from the backend; 
error handling and tests.

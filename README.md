# Airline Operations Simulator

A simulation project for modeling the day-to-day operations of an airline.

The goal is to represent core airline operations such as aircraft management, airport and route management, flight scheduling, aircraft assignment, operational states, and conflict detection.

The project is also intended to demonstrate a structured software engineering process including domain modeling, architecture decisions, testing, documentation, and iterative development.

## Current Status

The project is currently in early implementation.

The product vision, domain model, initial class diagram, user stories, acceptance criteria, technical tasks, and core architecture decisions have been defined.

Backend development has started with a Spring Boot application and PostgreSQL as the primary database.

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

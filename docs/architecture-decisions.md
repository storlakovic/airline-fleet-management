# Architecture Decisions

## ADR-001: Use Java 25 and Spring Boot 4

### Status

Accepted

### Context

The project is a new greenfield application and should use a modern, supported Java and Spring stack.

### Decision

Use Java 25 LTS and Spring Boot 4.1.x.

### Consequences

* Modern Java features are available.
* The project uses the current Spring Boot generation.
* Dependencies must be compatible with Spring Boot 4.

---

## ADR-002: Build Tool

### Status

Accepted

### Context

The project requires a build tool for dependency management, compilation, testing, packaging, and running the standard Java build lifecycle.

Maven and Gradle were considered.

### Decision

Use Maven.

### Consequences

* Predictable and standardized build lifecycle.
* Conventional choice in the Java and Spring ecosystem.
* Easier to understand and maintain.
* Sufficient for the expected project size.
* Less build-script flexibility than Gradle.

---

## ADR-003: Database

### Status

Accepted

### Context

The application contains strongly related and time-dependent data such as flights, routes, aircraft assignments, and schedules.

PostgreSQL and MySQL were considered.

### Decision

Use PostgreSQL.

### Consequences

* PostgreSQL provides strong support for relational data and complex queries.
* Native range types and exclusion constraints are useful for detecting and preventing overlapping schedules.
* PostgreSQL is well supported by Spring Boot, JPA, Docker, and Testcontainers.
* Using PostgreSQL-specific features can reduce database portability.

## ADR-004: Package Structure

### Status

Accepted

### Context

The application consists of distinct business capabilities such as aircraft management, airport management, routes, and flights.

### Decision

Organize the application primarily by feature rather than by technical layer.

### Consequences

- Related code stays close together.
- The package structure reflects the business domain.
- Features can evolve more independently.

---

## ADR-005: Database Schema Migrations

### Status

Accepted

### Context

The database schema must evolve together with the application while remaining reproducible and version controlled.

### Decision

Use Flyway for database schema migrations.

Database schema changes are stored as versioned SQL migrations under `src/main/resources/db/migration`.

Hibernate is used for object-relational mapping and validates the database schema against the JPA model instead of automatically modifying it.

Migration SQL may be generated from the JPA model using development tooling, but generated migrations must be reviewed before they are committed and executed.

### Consequences

- Database schema changes are version controlled and reproducible.
- Flyway tracks which migrations have already been applied.
- Hibernate does not automatically modify the production schema.
- JPA entities and the database schema can be validated against each other.
- Generated migration scripts must still be reviewed for correctness.
- Schema changes require explicit migrations instead of relying on `ddl-auto=update`.

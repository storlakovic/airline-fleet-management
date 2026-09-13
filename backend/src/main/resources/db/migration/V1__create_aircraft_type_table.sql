CREATE SEQUENCE IF NOT EXISTS aircraft_type_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE aircraft_type
(
    id           BIGINT      NOT NULL,
    manufacturer VARCHAR(50) NOT NULL,
    model        VARCHAR(50) NOT NULL,
    icao_code    VARCHAR(4)  NOT NULL,
    CONSTRAINT pk_aircraft_type PRIMARY KEY (id)
);

ALTER TABLE aircraft_type
    ADD CONSTRAINT uc_aircraft_type_icaocode UNIQUE (icao_code);
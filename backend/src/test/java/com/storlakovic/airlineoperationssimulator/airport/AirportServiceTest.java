package com.storlakovic.airlineoperationssimulator.airport;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class AirportServiceTest {

    private final AirportRepository repository =
            mock(AirportRepository.class);

    private final AirportService service =
            new AirportService(repository);


    @Test
    void shouldReadAirports() throws Exception {
        Resource resource = csv("""
                icao_code,iata_code,name,municipality,iso_country,latitude_deg,longitude_deg,type
                LOWW,VIE,Vienna International Airport,Vienna,AT,48.1103,16.5697,large_airport
                LOWL,LNZ,Linz Airport,Linz,AT,48.2332,14.1875,medium_airport
                """);

        List<Airport> result =
                service.readAirports(resource);

        assertThat(result).hasSize(2);
    }


    @Test
    void shouldMapAirportCorrectly() throws Exception {
        Resource resource = csv("""
                icao_code,iata_code,name,municipality,iso_country,latitude_deg,longitude_deg,type
                LOWW,VIE,Vienna International Airport,Vienna,AT,48.1103,16.5697,large_airport
                """);

        Airport airport =
                service.readAirports(resource).getFirst();

        assertThat(airport.getIcaoCode())
                .isEqualTo("LOWW");

        assertThat(airport.getIataCode())
                .isEqualTo("VIE");

        assertThat(airport.getName())
                .isEqualTo("Vienna International Airport");

        assertThat(airport.getCity())
                .isEqualTo("Vienna");

        assertThat(airport.getCountryCode())
                .isEqualTo("AT");

        assertThat(airport.getLatitude())
                .isEqualTo(48.1103);

        assertThat(airport.getLongitude())
                .isEqualTo(16.5697);

        assertThat(airport.getType())
                .isEqualTo("large_airport");

        assertThat(airport.getStatus())
                .isEqualTo(AirportStatus.OPERATIONAL);
    }


    @Test
    void shouldAllowMissingOptionalFields() throws Exception {
        Resource resource = csv("""
                icao_code,iata_code,name,municipality,iso_country,latitude_deg,longitude_deg,type
                LOWW,,Vienna International Airport,,AT,48.1103,16.5697,large_airport
                """);

        Airport airport =
                service.readAirports(resource).getFirst();

        assertThat(airport.getIataCode())
                .isEmpty();

        assertThat(airport.getCity())
                .isEmpty();
    }


    @Test
    void shouldIgnoreAirportWhenRequiredFieldIsMissing() throws Exception {
        Resource resource = csv("""
                icao_code,iata_code,name,municipality,iso_country,latitude_deg,longitude_deg,type
                ,VIE,Vienna International Airport,Vienna,AT,48.1103,16.5697,large_airport
                LOWL,LNZ,Linz Airport,Linz,AT,48.2332,14.1875,medium_airport
                """);
        List<Airport> result =
                service.readAirports(resource);

        assertThat(result)
                .hasSize(1);

        assertThat(result.getFirst().getIcaoCode())
                .isEqualTo("LOWL");
    }


    @Test
    void shouldNotSaveExistingAirport() {
        Airport existing = createAirport("LOWW", "VIE", "Vienna International Airport", "Vienna");
        Airport newAirport = createAirport("LOWL", "LNZ", "Linz Airport", "Linz");

        when(repository.existsByIcaoCode("LOWW")).thenReturn(true);
        when(repository.existsByIcaoCode("LOWL")).thenReturn(false);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<Airport> result = service.saveNewAirports(List.of(existing, newAirport));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getIcaoCode()).isEqualTo("LOWL");
    }


    @Test
    void shouldSaveNewAirports() {
        Airport vienna =
                createAirport(
                        "LOWW",
                        "VIE",
                        "Vienna International Airport",
                        "Vienna"
                );

        Airport linz =
                createAirport(
                        "LOWL",
                        "LNZ",
                        "Linz Airport",
                        "Linz"
                );

        when(repository.findAll())
                .thenReturn(List.of());

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        List<Airport> result =
                service.saveNewAirports(
                        List.of(vienna, linz)
                );

        assertThat(result)
                .hasSize(2);

        verify(repository)
                .saveAll(List.of(vienna, linz));
    }


    private Airport createAirport(
            String icaoCode,
            String iataCode,
            String name,
            String city
    ) {
        return new Airport(
                icaoCode,
                iataCode,
                name,
                city,
                "AT",
                48.1103,
                16.5697,
                "large_airport",
                AirportStatus.OPERATIONAL
        );
    }


    private Resource csv(String content) {
        return new ByteArrayResource(
                content.getBytes(StandardCharsets.UTF_8)
        );
    }
}

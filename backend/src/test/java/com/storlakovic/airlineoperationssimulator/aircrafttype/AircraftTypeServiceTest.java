package com.storlakovic.airlineoperationssimulator.aircrafttype;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AircraftTypeServiceTest {

    private final AircraftTypeRepository repository =
            mock(AircraftTypeRepository.class);

    private final AircraftTypeService service =
            new AircraftTypeService(repository);

    @Test
    void shouldReadAircraftTypes() throws IOException {
        Resource resource = csv("""
                ICAO_Code,Manufacturer,Model_BADA
                A320,AIRBUS,Airbus A320
                B738,BOEING,Boeing 737-800
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AircraftType> result =
                service.readAircraftTypes(resource);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldMapAircraftTypeCorrectly() throws IOException {
        Resource resource = csv("""
                ICAO_Code,Manufacturer,Model_BADA
                A320,AIRBUS,Airbus A320
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AircraftType result =
                service.readAircraftTypes(resource).getFirst();

        assertThat(result.getIcaoCode()).isEqualTo("A320");
        assertThat(result.getManufacturer()).isEqualTo("AIRBUS");
        assertThat(result.getModel()).isEqualTo("Airbus A320");
    }

    @Test
    void shouldReturnEmptyListWhenOnlyHeaderExists() throws IOException {
        Resource resource = csv("""
                ICAO_Code,Manufacturer,Model_BADA
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AircraftType> result =
                service.readAircraftTypes(resource);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotImportHeaderAsAircraftType() throws IOException {
        Resource resource = csv("""
                ICAO_Code,Manufacturer,Model_BADA
                A320,AIRBUS,Airbus A320
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AircraftType> result =
                service.readAircraftTypes(resource);

        assertThat(result)
                .extracting(AircraftType::getIcaoCode)
                .doesNotContain("ICAO_Code");
    }

    @Test
    void shouldHandleQuotedComma() throws IOException {
        Resource resource = csv("""
                ICAO_Code,Manufacturer,Model_BADA
                B738,BOEING,"Boeing 737, Series 800"
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AircraftType result =
                service.readAircraftTypes(resource).getFirst();

        assertThat(result.getModel())
                .isEqualTo("Boeing 737, Series 800");
    }

    @Test
    void shouldIgnoreUnusedColumns() throws IOException {
        Resource resource = csv("""
                ICAO_Code,FAA_Designator,Manufacturer,Model_BADA,Num_Engines,MTOW_lb
                A20N,A20N,AIRBUS,Airbus A320 Neo,2,"174,165"
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AircraftType result =
                service.readAircraftTypes(resource).getFirst();

        assertThat(result.getIcaoCode()).isEqualTo("A20N");
        assertThat(result.getManufacturer()).isEqualTo("AIRBUS");
        assertThat(result.getModel()).isEqualTo("Airbus A320 Neo");
    }

    @Test
    void shouldAllowMissingHeaderNames() throws IOException {
        Resource resource = csv("""
                ICAO_Code,Manufacturer,Model_BADA,,,
                A21N,AIRBUS,Airbus A321 Neo,,,
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AircraftType> result =
                service.readAircraftTypes(resource);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getIcaoCode())
                .isEqualTo("A21N");
    }

    @Test
    void shouldThrowExceptionWhenIcaoColumnIsMissing() {
        Resource resource = csv("""
                Manufacturer,Model_BADA
                AIRBUS,Airbus A320
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() ->
                service.readAircraftTypes(resource)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenManufacturerColumnIsMissing() {
        Resource resource = csv("""
                ICAO_Code,Model_BADA
                A320,Airbus A320
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() ->
                service.readAircraftTypes(resource)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenModelColumnIsMissing() {
        Resource resource = csv("""
                ICAO_Code,Manufacturer
                A320,AIRBUS
                """);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() ->
                service.readAircraftTypes(resource)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldIgnoreDuplicateAircraftTypes() throws IOException {
        Resource resource = csv("""
            ICAO_Code,Manufacturer,Model_BADA
            A320,AIRBUS,Airbus A320
            B738,BOEING,Boeing 737-800
            """);

        AircraftType existingAircraftType =
                new AircraftType(
                        "AIRBUS",
                        "Airbus A320",
                        "A320"
                );

        when(repository.findAll())
                .thenReturn(List.of(existingAircraftType));

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AircraftType> aircraftTypes =
                service.readAircraftTypes(resource);

        List<AircraftType> result =
                service.saveNewAircraftTypes(aircraftTypes);

        assertThat(result).hasSize(1);

        assertThat(result.getFirst().getIcaoCode())
                .isEqualTo("B738");
    }

    private Resource csv(String content) {
        return new ByteArrayResource(
                content.getBytes(StandardCharsets.UTF_8)
        );
    }
}
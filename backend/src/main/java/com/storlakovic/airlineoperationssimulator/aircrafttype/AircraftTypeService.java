package com.storlakovic.airlineoperationssimulator.aircrafttype;

import com.storlakovic.airlineoperationssimulator.common.AircraftTypeImportException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AircraftTypeService {

    private final AircraftTypeRepository repository;

    public AircraftTypeService(AircraftTypeRepository repository) {
        this.repository = repository;
    }

    public List<AircraftType> importAircraftTypes() {
        try {
            List<AircraftType> aircraftTypes = loadAircraftTypes();
            return saveNewAircraftTypes(aircraftTypes);
        } catch (IOException | IllegalArgumentException e) {
            throw new AircraftTypeImportException("Failed to import aircraft types from FAA reference data", e);
        }
    }

    /**
     * Loads aircraft type reference data from the FAA CSV file
     * bundled with the application.
     */
    List<AircraftType> loadAircraftTypes() throws IOException {
        Resource resource =
                new ClassPathResource("data/aircraft_type_data.csv");

        return readAircraftTypes(resource);
    }

    List<AircraftType> readAircraftTypes(Resource resource) throws IOException {
        List<AircraftType> aircraftTypes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        resource.getInputStream(),
                        StandardCharsets.UTF_8
                )
        )) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .get()
                    .parse(reader);

            for (CSVRecord record : records) {
                AircraftType aircraftType = new AircraftType(
                        record.get("Manufacturer"),
                        record.get("Model_BADA"),
                        record.get("ICAO_Code")
                );
                if(!record.get("Manufacturer").isEmpty() && !record.get("Model_BADA").isEmpty() &&  !record.get("ICAO_Code").isEmpty()){
                    aircraftTypes.add(aircraftType);
                }
            }
        }

        return aircraftTypes;
    }

    List<AircraftType> saveNewAircraftTypes(List<AircraftType> aircraftTypes) {
        Set<String> existingCodes = repository.findAll()
                .stream()
                .map(AircraftType::getIcaoCode)
                .collect(Collectors.toSet());

        List<AircraftType> newAircraftTypes = aircraftTypes.stream()
                .filter(type -> !existingCodes.contains(type.getIcaoCode()))
                .toList();

        return repository.saveAll(newAircraftTypes);
    }

}

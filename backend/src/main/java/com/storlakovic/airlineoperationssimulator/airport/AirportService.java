package com.storlakovic.airlineoperationssimulator.airport;

import com.storlakovic.airlineoperationssimulator.airport.dto.AirportDetailsResponse;
import com.storlakovic.airlineoperationssimulator.airport.dto.AirportResponse;
import com.storlakovic.airlineoperationssimulator.airport.dto.AirportUpdateRequest;
import com.storlakovic.airlineoperationssimulator.common.AirportImportException;
import com.storlakovic.airlineoperationssimulator.common.AirportNotFoundException;
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

@Service
public class AirportService {

    private final AirportRepository repository;

    public AirportService(AirportRepository repository) {
        this.repository = repository;
    }


    public List<AirportResponse> importAirports() {
        try {
            List<Airport> airports = loadAirports();
            return saveNewAirports(airports);
        } catch (IOException | IllegalArgumentException e) {
            throw new AirportImportException("Failed to import airport reference data");
        }
    }


    /**
     * Loads airports reference data from the OurAirports CSV file
     * bundled with the application.
     */
    private List<Airport> loadAirports( )throws IOException {
        Resource resource =
                new ClassPathResource("data/airport_data.csv");

        return readAirports(resource);
    }

    List<Airport> readAirports(Resource resource) throws IOException {
        List<Airport> airports = new ArrayList<>();

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
                Airport airport = new Airport(
                        record.get("icao_code"),
                        record.get("iata_code"),
                        record.get("name"),
                        record.get("municipality"),
                        record.get("iso_country"),
                        Double.valueOf(record.get("latitude_deg")),
                        Double.valueOf(record.get("longitude_deg")),
                        record.get("type"),
                        AirportStatus.OPERATIONAL
                );
                if(!record.get("icao_code").isEmpty() && !record.get("name").isEmpty() && !record.get("iso_country").isEmpty() && !record.get("latitude_deg").isEmpty() && !record.get("longitude_deg").isEmpty() && !record.get("type").isEmpty()){
                    airports.add(airport);
                }

            }
        }

        return airports;
    }

    public List<AirportResponse> saveNewAirports(List<Airport> airports) {
        List<Airport> newAirports = airports.stream()
                .filter(a -> !repository.existsByIcaoCode(a.getIcaoCode()))
                .toList();
        return repository.saveAll(newAirports).stream().map(AirportResponse::from).toList();
    }

    public AirportResponse updateAirport(Long id, AirportUpdateRequest request) {
        Airport airport = repository.findById(id).orElseThrow(() -> new AirportNotFoundException("Airport with id: " + id + " does not exist"));

        airport.setStatus(request.getStatus());

        Airport newAirport = repository.save(airport);

        return AirportResponse.from(newAirport);
    }

    public AirportDetailsResponse getAirport(Long id) {
        Airport airport = repository.findById(id).orElseThrow(() -> new AirportNotFoundException("Airport with id: " + id + " does not exist"));
        return  AirportDetailsResponse.from(airport);
    }
}

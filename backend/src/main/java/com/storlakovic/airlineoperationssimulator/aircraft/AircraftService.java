package com.storlakovic.airlineoperationssimulator.aircraft;


import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftDetailsResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftUpdateRequest;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftTypeRepository;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftAlreadyExistsException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftDeletionNotAllowedException;
import com.storlakovic.airlineoperationssimulator.aircraft.exceptions.AircraftNotFoundException;
import com.storlakovic.airlineoperationssimulator.flight.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AircraftTypeRepository aircraftTypeRepository;
    private final FlightRepository flightRepository;

    public AircraftService(AircraftRepository aircraftRepository, AircraftTypeRepository aircraftTypeRepository, FlightRepository flightRepository) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftTypeRepository = aircraftTypeRepository;
        this.flightRepository = flightRepository;
    }


    public AircraftResponse addAircraftToFleet(AircraftCreateRequest request) {
        if (aircraftRepository.existsAircraftByRegistration((request.registration()))){
            throw new AircraftAlreadyExistsException(
                    "Aircraft with registration " + request.registration() + " already exists"
            );
        }

        AircraftType aircraftType = aircraftTypeRepository.findById(request.aircraftTypeId()).orElseThrow();

        Aircraft aircraft = new Aircraft(
                aircraftType,
                request.registration()
        );

        Aircraft savedAircraft = aircraftRepository.save(aircraft);

        return AircraftResponse.from(savedAircraft);
    }

    public List<AircraftResponse> getAll() {
        return aircraftRepository.findAll().stream().map(AircraftResponse::from).toList();
    }

    public AircraftDetailsResponse getAircraftById(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id).orElseThrow(() -> new AircraftNotFoundException(
                "Aircraft with id " + id + " not found"
        ));
        return AircraftDetailsResponse.from(aircraft);
    }

    public AircraftResponse updateAircraft(Long aircraftId, AircraftUpdateRequest request) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId).orElseThrow(() -> new AircraftNotFoundException("Aircraft with id " + aircraftId + " not found"));

        aircraft.changeStatus(request.status());

        Aircraft savedAircraft = aircraftRepository.save(aircraft);

        return AircraftResponse.from(savedAircraft);
    }

    public void deleteAircraft(Long id) {
        if(!aircraftRepository.existsById(id)){
            throw new AircraftNotFoundException("Aircraft with id " + id + " not found");
        }
        if(!flightRepository.findByAircraft_Id(id).isEmpty()){
            throw new AircraftDeletionNotAllowedException("Aircraft with id " + id + " cannot be deleted, because it is assigned to an active flight");
        }
        aircraftRepository.deleteById(id);
    }
}

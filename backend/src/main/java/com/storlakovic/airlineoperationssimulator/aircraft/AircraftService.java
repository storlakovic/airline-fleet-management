package com.storlakovic.airlineoperationssimulator.aircraft;


import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftDetailsResponse;
import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftResponse;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftTypeRepository;
import com.storlakovic.airlineoperationssimulator.common.AircraftAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AircraftTypeRepository aircraftTypeRepository;

    public AircraftService(AircraftRepository aircraftRepository, AircraftTypeRepository aircraftTypeRepository) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftTypeRepository = aircraftTypeRepository;
    }


    public AircraftResponse addAircraftToFleet(AircraftCreateRequest request) {
        if (aircraftRepository.existsAircraftByRegistration((request.getRegistration()))){
            throw new AircraftAlreadyExistsException(
                    "Aircraft with registration " + request.getRegistration() + " already exists"
            );
        }

        AircraftType aircraftType = aircraftTypeRepository.findById(request.getAircraftTypeId()).orElseThrow();

        Aircraft aircraft = new Aircraft(
                aircraftType,
                request.getRegistration()
        );

        Aircraft savedAircraft = aircraftRepository.save(aircraft);

        return new AircraftResponse(
                savedAircraft.getId(),
                savedAircraft.getRegistration(),
                savedAircraft.getAircraftType().getId(),
                savedAircraft.getAircraftType().getModel(),
                savedAircraft.getAircraftType().getIcaoCode(),
                savedAircraft.getStatus()
        );
    }

    public List<AircraftResponse> getAll() {
        return aircraftRepository.findAll().stream().map(aircraft -> new AircraftResponse(aircraft.getId(), aircraft.getRegistration(), aircraft.getAircraftType().getId(), aircraft.getAircraftType().getModel(), aircraft.getAircraftType().getIcaoCode(), aircraft.getStatus())).toList();
    }

    public AircraftDetailsResponse getAircraftById(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id).orElseThrow();
        return new AircraftDetailsResponse(aircraft.getId(), aircraft.getRegistration(), aircraft.getAircraftType().getId(), aircraft.getAircraftType().getModel(), aircraft.getAircraftType().getIcaoCode(), aircraft.getAircraftType().getManufacturer(), aircraft.getStatus()) ;
    }
}

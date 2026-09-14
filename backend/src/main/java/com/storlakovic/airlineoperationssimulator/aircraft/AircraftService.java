package com.storlakovic.airlineoperationssimulator.aircraft;


import com.storlakovic.airlineoperationssimulator.aircraft.dto.AircraftCreateRequest;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftType;
import com.storlakovic.airlineoperationssimulator.aircrafttype.AircraftTypeRepository;
import com.storlakovic.airlineoperationssimulator.common.AircraftAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AircraftTypeRepository aircraftTypeRepository;

    public AircraftService(AircraftRepository aircraftRepository, AircraftTypeRepository aircraftTypeRepository) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftTypeRepository = aircraftTypeRepository;
    }


    public Aircraft addAircraftToFleet(AircraftCreateRequest request) {
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

        return aircraftRepository.save(savedAircraft);
    }


}

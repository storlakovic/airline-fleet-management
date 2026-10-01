package com.storlakovic.airlineoperationssimulator.flight;

import com.storlakovic.airlineoperationssimulator.flight.dto.FlightCreateRequest;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightDetailedResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightResponse;
import com.storlakovic.airlineoperationssimulator.flight.dto.FlightUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.storlakovic.airlineoperationssimulator.support.FlightFixtures.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Standalone MVC tests: real binding and validation without Spring Boot or a database. */
class FlightControllerTest {
    private final FlightService service = mock(FlightService.class);
    private final FlightResponse response = FlightResponse.from(flight(FlightStatus.UNKNOWN));
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new FlightController(service)).build();
    }

    @Test
    void createsFlightFromJson() throws Exception {
        var request = new FlightCreateRequest("OS1", 10L, DEPARTURE, ARRIVAL);
        when(service.createFlight(request)).thenReturn(response);
        mvc.perform(post("/flight/create").contentType(MediaType.APPLICATION_JSON).content("""
                {"flightNumber":"OS1","routeId":10,
                 "scheduledDepartureTime":"2026-10-01T10:00:00Z",
                 "scheduledArrivalTime":"2026-10-01T13:00:00Z"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("OS1"))
                .andExpect(jsonPath("$.status").value("UNKNOWN"));
        verify(service).createFlight(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"flightNumber\":\" \",\"routeId\":10,\"scheduledDepartureTime\":\"2026-10-01T10:00:00Z\",\"scheduledArrivalTime\":\"2026-10-01T13:00:00Z\"}",
            "{\"flightNumber\":\"OS1\",\"scheduledDepartureTime\":\"2026-10-01T10:00:00Z\",\"scheduledArrivalTime\":\"2026-10-01T13:00:00Z\"}",
            "{\"flightNumber\":\"OS1\",\"routeId\":10,\"scheduledArrivalTime\":\"2026-10-01T13:00:00Z\"}",
            "{\"flightNumber\":\"OS1\",\"routeId\":10,\"scheduledDepartureTime\":\"2026-10-01T10:00:00Z\"}",
            "{\"flightNumber\":\"OS1\",\"routeId\":10,\"scheduledDepartureTime\":\"invalid\"}",
            "not-json"
    })
    void rejectsInvalidCreateBodiesBeforeCallingService(String body) throws Exception {
        mvc.perform(post("/flight/create").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void returnsFlightDetails() throws Exception {
        when(service.getFlight(1L)).thenReturn(FlightDetailedResponse.from(flight(FlightStatus.UNKNOWN)));
        mvc.perform(get("/flight/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originName").value("Vienna Airport"));
        verify(service).getFlight(1L);
    }

    @Test
    void returnsFlightList() throws Exception {
        when(service.getAllFlights()).thenReturn(List.of(response));
        mvc.perform(get("/flight"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
        verify(service).getAllFlights();
    }

    @Test
    void bindsPartialUpdateAndFlightId() throws Exception {
        var request = new FlightUpdateRequest(null, ARRIVAL);
        when(service.updateFlight(request, 7L)).thenReturn(response);
        mvc.perform(put("/flight/update/7").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scheduledArrivalTime\":\"2026-10-01T13:00:00Z\"}"))
                .andExpect(status().isOk());
        verify(service).updateFlight(request, 7L);
    }

    @Test
    void bindsCancellationId() throws Exception {
        when(service.cancelFlight(7L)).thenReturn(response);
        mvc.perform(put("/flight/cancel/7")).andExpect(status().isOk());
        verify(service).cancelFlight(7L);
    }

    @Test
    void bindsFlightAndAircraftIdsInCorrectOrder() throws Exception {
        when(service.assignAircraft(7L, 42L)).thenReturn(response);
        mvc.perform(put("/flight/assign-aircraft/7/42")).andExpect(status().isOk());
        verify(service).assignAircraft(7L, 42L);
    }

    @Test
    void rejectsInvalidPathId() throws Exception {
        mvc.perform(get("/flight/not-a-number")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}

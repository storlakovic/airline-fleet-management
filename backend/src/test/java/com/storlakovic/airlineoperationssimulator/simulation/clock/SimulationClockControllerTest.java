package com.storlakovic.airlineoperationssimulator.simulation.clock;

import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockResponse;
import com.storlakovic.airlineoperationssimulator.simulation.clock.dto.ClockUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Real request binding and validation, without a server or database. */
class SimulationClockControllerTest {
    private final SimulationClockService service = mock(SimulationClockService.class);
    private final OffsetDateTime time = OffsetDateTime.parse("2030-01-01T12:30:45+02:00");
    private final ClockResponse response = new ClockResponse(time, 60L);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new SimulationClockController(service)).build();
    }

    @Test
    void returnsCurrentTimeAndSpeed() throws Exception {
        when(service.getCurrentTime()).thenReturn(response);
        mvc.perform(get("/simulation/clock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateTime").value(time.toString()))
                .andExpect(jsonPath("$.multiplier").value(60));
        verify(service).getCurrentTime();
    }

    @Test
    void bindsTimeIncludingOffsetAndReturnsServiceResponse() throws Exception {
        when(service.setSimulatorTime(any())).thenReturn(response);
        mvc.perform(put("/simulation/clock/time").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateTime\":\"2030-01-01T12:30:45+02:00\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.multiplier").value(60));
        var request = ArgumentCaptor.forClass(ClockUpdateRequest.class);
        verify(service).setSimulatorTime(request.capture());
        assertThat(request.getValue().dateTime().toInstant()).isEqualTo(time.toInstant());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"dateTime\":null}", "{\"dateTime\":\"invalid\"}", "{\"dateTime\":\"2030-01-01T12:30:00\"}", "not-json", ""})
    void rejectsMissingOrMalformedTimeBeforeCallingService(String body) throws Exception {
        mvc.perform(put("/simulation/clock/time").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void bindsSpeedAndReturnsUpdatedResponse() throws Exception {
        when(service.setSpeedMultiplier(60L)).thenReturn(response);
        mvc.perform(put("/simulation/clock/speed/60"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.multiplier").value(60));
        verify(service).setSpeedMultiplier(60L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"fast", "1.5", "9223372036854775808"})
    void rejectsInvalidLongBeforeCallingService(String multiplier) throws Exception {
        mvc.perform(put("/simulation/clock/speed/" + multiplier)).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}

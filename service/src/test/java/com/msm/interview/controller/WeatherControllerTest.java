package com.msm.interview.controller;

import com.msm.interview.model.TemperatureResponse;
import com.msm.interview.service.TemperatureRetrievalService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

    @Mock
    private TemperatureRetrievalService temperatureRetrievalService;

    @InjectMocks
    private WeatherController weatherController;

    @Test
    public void fetchWarmestTemperature(){
        Map<String,String> headers = createHeaders();
        Set<TemperatureResponse> expectedResults = new HashSet<>();
        expectedResults.add(new TemperatureResponse("123",24.0D,"C"));
        expectedResults.add(new TemperatureResponse("33",12.0D,"C"));
        expectedResults.add(new TemperatureResponse("777",17.0D,"C"));

        when(temperatureRetrievalService.fetchTemperatures(headers)).thenReturn(expectedResults);

        ResponseEntity<WeatherResponse> warmestTemperatureResponse = weatherController.getTemperatures(createHeaders());

        assertThat(warmestTemperatureResponse.getStatusCode()).isEqualTo(OK);
        assertThat(warmestTemperatureResponse.getBody()).isEqualTo(new WeatherResponse(24.0D, "C"));

    }

    private Map<String, String> createHeaders(){
        return new HashMap<>();
    }

}
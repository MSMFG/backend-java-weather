package com.msm.interview.service;


import com.msm.interview.model.TemperatureResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class TemperatureRetrievalServiceTest {


    @Mock
    private RestTemplate restTemplate;


    private static final String URL = "someURL";
    private static final String URL_WITH_PATH = URL + "/v1/temperature";

    private TemperatureRetrievalService temperatureRetrievalService;



    @BeforeEach
    void setUp() {
        openMocks(this);
        this.temperatureRetrievalService = new TemperatureRetrievalService(restTemplate, URL);
    }

    @Test
    public void fetchTemperatureFiltersOnlyCentigrade()  {

        Map<String, Object> responseAsMap = new HashMap<>();
        responseAsMap.put("temperatures", mockedTemperatureList());
        when(restTemplate.getForEntity(eq(URL_WITH_PATH), eq(Map.class))).thenReturn(new ResponseEntity<Map>(responseAsMap, HttpStatus.OK));

        Set<TemperatureResponse> response = temperatureRetrievalService.fetchTemperatures(Map.of("unit","c"));

        assertThat(response.size()).isEqualTo(2);
        assertThat(response).contains(new TemperatureResponse("123", 45.0D, "C"));
        assertThat(response).contains(new TemperatureResponse("777", 25.0D, "C"));

    }

    private List<Map<String, Object>> mockedTemperatureList() {
        return Arrays.asList(
                Map.of("id", "123", "temperature", 45.0D, "unit", "C"),
                Map.of("id", "444", "temperature", 200.0D, "unit", "F"),
                Map.of("id", "777", "temperature", 25.0D, "unit", "C")
        );
    }

}
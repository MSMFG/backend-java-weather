package com.msm.interview.service;

import com.msm.interview.model.TemperatureResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TemperatureRetrievalService {


    private RestTemplate restTemplate;

    private final String temperatureEndpoint;

    private static final Logger LOGGER = Logger.getLogger(TemperatureRetrievalService.class.getName());

    @Autowired
    public TemperatureRetrievalService(RestTemplate restTemplate,
                                       @Value("${temperature.retrieval.service.url}") String serviceUrl) {
        this.restTemplate = restTemplate;
        this.temperatureEndpoint = serviceUrl + "/v1/temperature";

    }

    public Set<TemperatureResponse> fetchTemperatures(Map<String, String> queryParameters) {
        LOGGER.info("fetching temperatures...");

        ResponseEntity<Map> response = restTemplate.getForEntity(this.temperatureEndpoint, Map.class);
        Map<String, Object> payload = response.getBody();

        List<Map<String, Object>> temperatures = (List<Map<String, Object>>) payload.get("temperatures");

        Set<TemperatureResponse> cityIdAgainstTemperature = getTemperatureResponses(temperatures);

        if (queryParameters.isEmpty() || "C".equalsIgnoreCase(queryParameters.get("unit"))) {
            return filterTemperatureResponseForCelsius(cityIdAgainstTemperature);
        }

        return cityIdAgainstTemperature;
    }

    private Set<TemperatureResponse> getTemperatureResponses(List<Map<String, Object>> temperatures) {
        Set<TemperatureResponse> cityIdAgainstTemperature = new HashSet<>();

        for (Map<String, Object> temperature : temperatures) {
            String id = (String) temperature.get("id");
            Double temp = (Double) temperature.get("temperature");
            String unit = (String) temperature.get("unit");

            cityIdAgainstTemperature.add(new TemperatureResponse(id, temp, unit));
        }

        return cityIdAgainstTemperature;
    }

    private Set<TemperatureResponse> filterTemperatureResponseForCelsius(Set<TemperatureResponse> cityIdAgainstTemperature) {
        final Set<TemperatureResponse> temperatureResponseToReturn = new HashSet<>();

        for (TemperatureResponse temperatureResponse : cityIdAgainstTemperature) {
            if ("c".equalsIgnoreCase(temperatureResponse.unit())) {
                temperatureResponseToReturn.add(temperatureResponse);
            }
        }

        return temperatureResponseToReturn;
    }
}

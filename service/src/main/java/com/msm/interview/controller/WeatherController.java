package com.msm.interview.controller;

import com.msm.interview.model.TemperatureResponse;
import com.msm.interview.service.TemperatureRetrievalService;
import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/weather")
public class WeatherController {

    private final TemperatureRetrievalService temperatureRetrievalService;

    public WeatherController(TemperatureRetrievalService temperatureRetrievalService) {
        this.temperatureRetrievalService = temperatureRetrievalService;
    }

    @GetMapping(path = "/warmest")
    public ResponseEntity<WeatherResponse> getTemperatures(@RequestParam Map<String, String> queryParameters) {

        Set<TemperatureResponse> cityIdAgainstTemperature = temperatureRetrievalService.fetchTemperatures(queryParameters);

        double maxTemperature = getMaxTemperature(cityIdAgainstTemperature);

        return ResponseEntity.ok(new WeatherResponse(maxTemperature, "C"));
    }

    private double getMaxTemperature(Set<TemperatureResponse> cityIdAgainstTemperature) {

        double maxSoFar = -999;
        String maxCity = null;

        for (TemperatureResponse temperatureResponse : cityIdAgainstTemperature) {
            if (temperatureResponse.temperature() > maxSoFar) {
                maxSoFar = temperatureResponse.temperature();
                maxCity = temperatureResponse.id();
            }
        }

        return maxSoFar;
    }
}

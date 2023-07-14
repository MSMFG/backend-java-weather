package com.msm.interview.integration;


import com.github.tomakehurst.wiremock.client.WireMock;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.Double.valueOf;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@AutoConfigureWireMock(port = 0)
@TestInstance(PER_CLASS)
@SpringBootTest( webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {
        "temperature.retrieval.service.url=http://localhost:${wiremock.server.port}"
})
@ActiveProfiles("test")
public class End2EndTest {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @LocalServerPort
    protected int port;

    @Value("classpath:temperature.json")
    private Resource temperatureResponse;

    @AfterEach
    public void cleanup() {
        WireMock.reset();
    }

    protected String getTestUrl(final String requestPath) {
        return String.format("http://localhost:%s/%s", port, requestPath);
    }

    @Test
    void highestTemperatureReturned() throws IOException {
        stubFor(
            get(urlPathEqualTo("/v1/temperature"))
                .willReturn(okJson(Files.readString(Path.of(temperatureResponse.getURI()))))
        );

        ResponseEntity<Map> response = testRestTemplate.exchange(
                getTestUrl("/v1/weather/warmest?unit=c"),
                HttpMethod.GET,
                null,
                Map.class
        );

        assertThat(response.getBody().get("temperature")).isEqualTo(valueOf(21.20920908097071D));
        assertThat(response.getBody().get("unit")).isEqualTo("C");
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void withNoQueryParamsTheUnitBehaviourDefaultsToCentigrade() throws IOException {
        stubFor(
                get(urlPathEqualTo("/v1/temperature"))
                        .willReturn(okJson(Files.readString(Path.of(temperatureResponse.getURI()))))
        );

        ResponseEntity<Map> response = testRestTemplate.exchange(
                getTestUrl("/v1/weather/warmest"),
                HttpMethod.GET,
                null,
                Map.class
        );

        assertThat(response.getBody().get("temperature")).isEqualTo(valueOf(21.20920908097071D));
        assertThat(response.getBody().get("unit")).isEqualTo("C");
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
}




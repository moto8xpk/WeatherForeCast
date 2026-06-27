package org.openweather.service;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openweather.connector.OpenWeatherMapClient;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.domain.RetryableWeatherException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Covers the transient-vs-client error discrimination that drives the @Retry policy (PRD §F8). */
class WeatherGatewayTest {

    private WeatherGateway gateway;
    private OpenWeatherMapClient connector;

    @BeforeEach
    void setUp() {
        gateway = new WeatherGateway();
        connector = mock(OpenWeatherMapClient.class);
        gateway.openWeatherMapClient = connector;
        gateway.apiKey = "key";
        gateway.lang = "en";
    }

    @Test
    void currentByCity_happyPath_returnsResponse() {
        OpenWeatherMapResponse resp = new OpenWeatherMapResponse();
        when(connector.getCurrentWeatherById(1, "key", "metric", "en")).thenReturn(resp);

        assertSame(resp, gateway.currentByCity(1, "metric"));
    }

    @Test
    void currentByCity_5xx_isWrappedRetryable() {
        when(connector.getCurrentWeatherById(anyInt(), anyString(), anyString(), anyString()))
                .thenThrow(new WebApplicationException(Response.status(503).build()));

        assertThrows(RetryableWeatherException.class, () -> gateway.currentByCity(1, "metric"));
    }

    @Test
    void currentByCity_4xx_isNotRetried() {
        WebApplicationException clientError = new WebApplicationException(Response.status(404).build());
        when(connector.getCurrentWeatherById(anyInt(), anyString(), anyString(), anyString()))
                .thenThrow(clientError);

        WebApplicationException thrown =
                assertThrows(WebApplicationException.class, () -> gateway.currentByCity(1, "metric"));
        assertEquals(404, thrown.getResponse().getStatus());
    }

    @Test
    void forecastByCity_5xx_isWrappedRetryable() {
        when(connector.getForecast(anyInt(), anyString(), anyString(), anyString()))
                .thenThrow(new WebApplicationException(Response.status(500).build()));

        assertThrows(RetryableWeatherException.class, () -> gateway.forecastByCity(1, "metric"));
    }
}

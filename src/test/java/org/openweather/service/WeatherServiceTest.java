package org.openweather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openweather.connector.OpenWeatherMapClient;
import org.openweather.domain.OpenWeatherMapResponse;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    private WeatherService service;
    private OpenWeatherMapClient connector;

    @BeforeEach
    void setUp() {
        service = new WeatherService();
        connector = mock(OpenWeatherMapClient.class);
        service.openWeatherMapClient = connector;
        service.apiKey = "key";
        service.defaultLocation = "Ho Chi Minh City,vn";
        service.lang = "en";
    }

    @Test
    void getCurWeather_withCity_usesGivenCity() {
        OpenWeatherMapResponse resp = new OpenWeatherMapResponse();
        when(connector.getCurrentWeather("Hanoi", "key", "en")).thenReturn(resp);

        assertSame(resp, service.getCurWeather("Hanoi"));
        verify(connector).getCurrentWeather("Hanoi", "key", "en");
    }

    @Test
    void getCurWeather_withNullCity_fallsBackToDefaultLocation() {
        OpenWeatherMapResponse resp = new OpenWeatherMapResponse();
        when(connector.getCurrentWeather("Ho Chi Minh City,vn", "key", "en")).thenReturn(resp);

        assertSame(resp, service.getCurWeather(null));
        verify(connector).getCurrentWeather("Ho Chi Minh City,vn", "key", "en");
    }
}

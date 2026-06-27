package org.openweather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openweather.connector.OpenWeatherMapClient;
import org.openweather.domain.City;
import org.openweather.domain.ForecastResponse;
import org.openweather.domain.InvalidUnitsException;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.domain.UnknownCityException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    private WeatherService service;
    private OpenWeatherMapClient connector;
    private WeatherGateway gateway;

    @BeforeEach
    void setUp() {
        service = new WeatherService();
        connector = mock(OpenWeatherMapClient.class);
        gateway = mock(WeatherGateway.class);
        service.openWeatherMapClient = connector;
        service.weatherGateway = gateway;
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

    @Test
    void getCurrentWeatherById_defaultsUnitsToMetric_andDelegatesToGateway() {
        OpenWeatherMapResponse resp = new OpenWeatherMapResponse();
        when(gateway.currentByCity(City.HCM.getCityId(), "metric")).thenReturn(resp);

        assertSame(resp, service.getCurrentWeatherById(City.HCM.getCityId(), null));
        verify(gateway).currentByCity(City.HCM.getCityId(), "metric");
    }

    @Test
    void getForecastById_passesImperialThrough() {
        ForecastResponse resp = new ForecastResponse();
        when(gateway.forecastByCity(City.HA_NOI.getCityId(), "imperial")).thenReturn(resp);

        assertSame(resp, service.getForecastById(City.HA_NOI.getCityId(), "Imperial"));
        verify(gateway).forecastByCity(City.HA_NOI.getCityId(), "imperial");
    }

    @Test
    void getCurrentWeatherById_unknownCity_throws() {
        assertThrows(UnknownCityException.class, () -> service.getCurrentWeatherById(999, "metric"));
        verifyNoInteractions(gateway);
    }

    @Test
    void getCurrentWeatherById_invalidUnits_throws() {
        assertThrows(InvalidUnitsException.class,
                () -> service.getCurrentWeatherById(City.HCM.getCityId(), "kelvin"));
        verifyNoInteractions(gateway);
    }
}

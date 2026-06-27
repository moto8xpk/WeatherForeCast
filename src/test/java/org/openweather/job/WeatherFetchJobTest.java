package org.openweather.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.service.WeatherDataService;
import org.openweather.service.WeatherService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WeatherFetchJobTest {

    private WeatherFetchJob job;
    private WeatherService weatherService;
    private WeatherDataService weatherDataService;

    @BeforeEach
    void setUp() {
        job = new WeatherFetchJob();
        weatherService = mock(WeatherService.class);
        weatherDataService = mock(WeatherDataService.class);
        job.weatherService = weatherService;
        job.weatherDataService = weatherDataService;
    }

    @Test
    void fetchAndStoreWeatherData_savesEveryCity() {
        when(weatherService.getCurrentWeatherById(anyInt(), anyString())).thenReturn(new OpenWeatherMapResponse());

        job.fetchAndStoreWeatherData();

        // 3 cities -> 3 saves (async, so wait via verify timeout)
        verify(weatherDataService, timeout(3000).times(3)).saveWeatherData(any(OpenWeatherMapResponse.class));
    }

    @Test
    void fetchAndStoreWeatherData_whenFetchFails_doesNotSave() {
        when(weatherService.getCurrentWeatherById(anyInt(), anyString()))
                .thenThrow(new RuntimeException("API down"));

        job.fetchAndStoreWeatherData();

        verify(weatherDataService, after(500).never()).saveWeatherData(any());
    }
}

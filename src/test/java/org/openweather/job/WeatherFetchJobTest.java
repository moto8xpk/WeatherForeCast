package org.openweather.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.service.WeatherDataService;
import org.openweather.service.WeatherService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    void warmCacheOnStartup_whenDisabled_doesNothing() {
        job.preloadEnabled = false;

        job.warmCacheOnStartup(null);

        verify(weatherService, after(300).never()).getCurrentWeatherById(anyInt(), anyString());
        verify(weatherService, never()).getForecastById(anyInt(), anyString());
    }

    @Test
    void warmCacheOnStartup_whenEnabled_warmsCurrentAndForecastForEveryCity() {
        job.preloadEnabled = true;
        when(weatherService.getCurrentWeatherById(anyInt(), anyString()))
                .thenReturn(new OpenWeatherMapResponse());

        job.warmCacheOnStartup(null);

        // 3 cities -> current + forecast each (async, wait via verify timeout)
        verify(weatherService, timeout(3000).times(3)).getCurrentWeatherById(anyInt(), eq("metric"));
        verify(weatherService, timeout(3000).times(3)).getForecastById(anyInt(), eq("metric"));
    }

    @Test
    void warmCacheOnStartup_whenServiceFails_doesNotThrow() {
        job.preloadEnabled = true;
        when(weatherService.getCurrentWeatherById(anyInt(), anyString()))
                .thenThrow(new RuntimeException("API down"));

        // Failure is swallowed (logged) so startup is never blocked.
        job.warmCacheOnStartup(null);

        verify(weatherService, timeout(3000).atLeastOnce()).getCurrentWeatherById(anyInt(), anyString());
    }
}

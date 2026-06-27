package org.openweather.job;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.openweather.domain.City;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.service.WeatherDataService;
import org.openweather.service.WeatherService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
public class WeatherFetchJob {

    @Inject
    WeatherService weatherService;

    @Inject
    WeatherDataService weatherDataService;

    @ConfigProperty(name = "app.weather.preload.enabled", defaultValue = "true")
    boolean preloadEnabled;

    /**
     * Warm the in-memory cache for all 3 cities at startup so the first client request is instant
     * (PRD §F7). Runs the cached current-weather + forecast fetches in parallel.
     */
    void warmCacheOnStartup(@Observes StartupEvent event) {
        if (!preloadEnabled) {
            return;
        }
        log.info("Warming weather cache for {} cities", City.values().length);
        List<CompletableFuture<Void>> futures = Arrays.stream(City.values())
                .map(city -> CompletableFuture.runAsync(() -> {
                    weatherService.getCurrentWeatherById(city.getCityId(), WeatherService.METRIC);
                    weatherService.getForecastById(city.getCityId(), WeatherService.METRIC);
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.warn("Cache warm-up failed (will fill lazily on first request)", ex);
                    return null;
                });
    }

    @Scheduled(cron = "* 0 * * * ?")
    public void fetchAndStoreWeatherData() {
        List<CompletableFuture<OpenWeatherMapResponse>> futures = Arrays.stream(City.values())
                .map(city -> CompletableFuture.supplyAsync(
                        () -> weatherService.getCurrentWeatherById(city.getCityId(), WeatherService.METRIC)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {
                    List<OpenWeatherMapResponse> weatherDataList = futures.stream()
                            .map(CompletableFuture::join)
                            .toList();

                    for (OpenWeatherMapResponse weatherData : weatherDataList) {
                        log.info("weather data: {}", weatherData);
                        weatherDataService.saveWeatherData(weatherData);
                    }
                })
                .exceptionally(ex -> {
                    log.error("Failed to fetch/store weather data", ex);
                    return null;
                });
    }
}

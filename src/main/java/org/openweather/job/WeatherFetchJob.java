package org.openweather.job;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.mapper.OpenWeatherMapResponseMapper;
import org.openweather.mapper.WeatherDomainMapper;
import org.openweather.model.OpenWeatherMapResponseEntity;
import org.openweather.repository.WeatherDomainEntityRepository;
import org.openweather.service.WeatherDataService;
import org.openweather.service.WeatherDomainService;
import org.openweather.service.WeatherService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class WeatherFetchJob {

    @Inject
    WeatherService weatherService;

    @Inject
    WeatherDataService weatherDataService;

    @Inject
    WeatherDomainService weatherDomainService;

//    @Scheduled(cron = "* 0 * * * ?")
@Scheduled(cron = "0 * * * * ?")
    public void fetchAndStoreWeatherData() {
        List<String> cities = List.of("Ho Chi Minh City", "Da Nang", "Hanoi");

        List<CompletableFuture<OpenWeatherMapResponse>> futures = cities.stream()
                .map(city -> CompletableFuture.supplyAsync(() -> weatherService.getCurWeather(city)))
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
                    log.error(ex.getMessage());
                    return null;
                });
    }
}

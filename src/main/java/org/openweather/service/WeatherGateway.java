package org.openweather.service;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.openweather.connector.OpenWeatherMapClient;
import org.openweather.domain.ForecastResponse;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.domain.RetryableWeatherException;

import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

/**
 * Network boundary to OpenWeather for the id-based endpoints. Lives in its own bean so the
 * {@code @CacheResult} (F6) and {@code @Retry} (F8) interceptors fire on cross-bean calls from
 * {@link WeatherService}. Results are keyed by {@code (cityId, units)}; both args must already be
 * validated/normalized by the caller so cache keys stay canonical.
 */
@ApplicationScoped
public class WeatherGateway {

    @ConfigProperty(name = "quarkus.rest-client.weather-openapi.apikey")
    String apiKey;

    @ConfigProperty(name = "quarkus.rest-client.weather-openapi.lang", defaultValue = "en")
    String lang;

    final OpenWeatherMapClient openWeatherMapClient;

    @Inject
    public WeatherGateway(OpenWeatherMapClient openWeatherMapClient) {
        this.openWeatherMapClient = openWeatherMapClient;
    }

    @CacheResult(cacheName = "weather-current")
    @Retry(maxRetries = 2, delay = 1500, delayUnit = ChronoUnit.MILLIS, retryOn = RetryableWeatherException.class)
    public OpenWeatherMapResponse currentByCity(int cityId, String units) {
        return execute(() -> openWeatherMapClient.getCurrentWeatherById(cityId, apiKey, units, lang));
    }

    @CacheResult(cacheName = "weather-forecast")
    @Retry(maxRetries = 2, delay = 1500, delayUnit = ChronoUnit.MILLIS, retryOn = RetryableWeatherException.class)
    public ForecastResponse forecastByCity(int cityId, String units) {
        return execute(() -> openWeatherMapClient.getForecast(cityId, apiKey, units, lang));
    }

    /**
     * Runs an OpenWeather call and classifies failures for the {@code @Retry} policy (PRD §F8):
     * network errors and 5xx are wrapped as retryable; 4xx are rethrown unchanged so they are NOT retried.
     */
    private <T> T execute(Supplier<T> call) {
        try {
            return call.get();
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() >= 500) {
                throw new RetryableWeatherException(e);
            }
            throw e;
        } catch (ProcessingException e) {
            throw new RetryableWeatherException(e);
        }
    }
}

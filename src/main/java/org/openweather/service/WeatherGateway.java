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

    @Inject
    OpenWeatherMapClient openWeatherMapClient;

    @CacheResult(cacheName = "weather-current")
    @Retry(maxRetries = 2, delay = 1500, delayUnit = ChronoUnit.MILLIS, retryOn = RetryableWeatherException.class)
    public OpenWeatherMapResponse currentByCity(int cityId, String units) {
        try {
            return openWeatherMapClient.getCurrentWeatherById(cityId, apiKey, units, lang);
        } catch (WebApplicationException e) {
            throw wrapIfTransient(e);
        } catch (ProcessingException e) {
            throw new RetryableWeatherException(e);
        }
    }

    @CacheResult(cacheName = "weather-forecast")
    @Retry(maxRetries = 2, delay = 1500, delayUnit = ChronoUnit.MILLIS, retryOn = RetryableWeatherException.class)
    public ForecastResponse forecastByCity(int cityId, String units) {
        try {
            return openWeatherMapClient.getForecast(cityId, apiKey, units, lang);
        } catch (WebApplicationException e) {
            throw wrapIfTransient(e);
        } catch (ProcessingException e) {
            throw new RetryableWeatherException(e);
        }
    }

    /**
     * 5xx → retryable; 4xx → rethrow unchanged so it is NOT retried (PRD §F8).
     */
    private RuntimeException wrapIfTransient(WebApplicationException e) {
        int status = e.getResponse() != null ? e.getResponse().getStatus() : 0;
        if (status >= 500 || status == 0) {
            return new RetryableWeatherException(e);
        }
        return e;
    }
}

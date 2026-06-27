package org.openweather.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.openweather.connector.OpenWeatherMapClient;
import org.openweather.domain.City;
import org.openweather.domain.ForecastResponse;
import org.openweather.domain.InvalidUnitsException;
import org.openweather.domain.OpenWeatherMapResponse;

@ApplicationScoped
public class WeatherService {

    static final String METRIC = "metric";
    static final String IMPERIAL = "imperial";

    @ConfigProperty(name="quarkus.rest-client.weather-openapi.apikey")
    String apiKey;

    @ConfigProperty(name="quarkus.rest-client.weather-openapi.default-location")
    String defaultLocation;

    @ConfigProperty(name="quarkus.rest-client.weather-openapi.lang", defaultValue = "en")
    String lang;

    @Inject
    OpenWeatherMapClient openWeatherMapClient;

    @Inject
    WeatherGateway weatherGateway;

    public OpenWeatherMapResponse getCurWeather(String city) {
        String selectedCity = city != null ? city : defaultLocation;
        return openWeatherMapClient.getCurrentWeather(selectedCity, apiKey, lang);
    }

    /**
     * Current weather for one of the 3 fixed cities (PRD §F2/F4). Validates the city id (404 if
     * unknown) and units (400 if not metric/imperial); the actual fetch is cached + retried.
     */
    public OpenWeatherMapResponse getCurrentWeatherById(int cityId, String units) {
        City.fromId(cityId);
        return weatherGateway.currentByCity(cityId, normalizeUnits(units));
    }

    /**
     * 5-day / 3-hour forecast for one of the 3 fixed cities (PRD §F3/F4).
     */
    public ForecastResponse getForecastById(int cityId, String units) {
        City.fromId(cityId);
        return weatherGateway.forecastByCity(cityId, normalizeUnits(units));
    }

    /**
     * Defaults to metric (°C) when blank; rejects anything other than metric/imperial so the client
     * can convert °C↔°F without an extra API call (PRD §F4).
     */
    static String normalizeUnits(String units) {
        if (units == null || units.isBlank()) {
            return METRIC;
        }
        String u = units.trim().toLowerCase();
        if (!u.equals(METRIC) && !u.equals(IMPERIAL)) {
            throw new InvalidUnitsException(units);
        }
        return u;
    }
}

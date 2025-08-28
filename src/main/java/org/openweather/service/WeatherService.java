package org.openweather.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.openweather.connector.OpenWeatherMapClient;
import org.openweather.domain.OpenWeatherMapResponse;

@ApplicationScoped
public class WeatherService {

    @ConfigProperty(name="quarkus.rest-client.weather-openapi.apikey")
    String apiKey;

    @ConfigProperty(name="quarkus.rest-client.weather-openapi.default-location")
    String defaultLocation;

    private static final String LANG = "en";

    @Inject
    OpenWeatherMapClient openWeatherMapClient;

    public OpenWeatherMapResponse getCurWeather(String city) {
        String selectedCity = city != null ? city : defaultLocation;
        return openWeatherMapClient.getCurrentWeather(selectedCity, apiKey, LANG);
    }
}

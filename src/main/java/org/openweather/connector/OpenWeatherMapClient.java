package org.openweather.connector;

import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.openweather.domain.ForecastResponse;
import org.openweather.domain.OpenWeatherMapResponse;

@Default
@RegisterRestClient(configKey = "weather-openapi")
public interface OpenWeatherMapClient {

    @GET
    @Path("/weather")
    OpenWeatherMapResponse getCurrentWeather(
            @QueryParam("q") String city,
            @QueryParam("APPID") String apiKey,
            @QueryParam("lang") String lang
    );

    /**
     * Current weather by OpenWeather city id (PRD §F2/F4). {@code units} is {@code metric} or
     * {@code imperial}.
     */
    @GET
    @Path("/weather")
    OpenWeatherMapResponse getCurrentWeatherById(
            @QueryParam("id") int cityId,
            @QueryParam("APPID") String apiKey,
            @QueryParam("units") String units,
            @QueryParam("lang") String lang
    );

    /**
     * 5-day / 3-hour forecast by OpenWeather city id (PRD §F3/F4).
     */
    @GET
    @Path("/forecast")
    ForecastResponse getForecast(
            @QueryParam("id") int cityId,
            @QueryParam("APPID") String apiKey,
            @QueryParam("units") String units,
            @QueryParam("lang") String lang
    );
}

package org.openweather.connector;

import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
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
}

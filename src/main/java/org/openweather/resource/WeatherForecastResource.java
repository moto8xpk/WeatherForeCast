package org.openweather.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.service.WeatherService;

@Path("/weatherForecast/v1")
public class WeatherForecastResource {

    @Inject
    WeatherService weatherService;

    @GET
    @Path("/location")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public OpenWeatherMapResponse getWeatherForeCast(@RestQuery("location") String location) {
        return weatherService.getCurWeather(location);
    }
}

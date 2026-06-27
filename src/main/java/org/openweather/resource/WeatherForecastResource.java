package org.openweather.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;
import org.openweather.domain.CityInfo;
import org.openweather.domain.ForecastResponse;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.service.WeatherService;

import java.util.List;

@Path("/weatherForecast/v1")
@Produces(MediaType.APPLICATION_JSON)
public class WeatherForecastResource {

    @Inject
    WeatherService weatherService;

    /**
     * @deprecated kept for backward compatibility; new clients should use {@link #current} with a
     * fixed city id (PRD §F1).
     */
    @Deprecated
    @GET
    @Path("/location")
    @Consumes(MediaType.APPLICATION_JSON)
    public OpenWeatherMapResponse getWeatherForeCast(@RestQuery("location") String location) {
        return weatherService.getCurWeather(location);
    }

    /**
     * The 3 fixed cities for the tab/segmented control (PRD §F1).
     */
    @GET
    @Path("/cities")
    public List<CityInfo> cities() {
        return CityInfo.all();
    }

    /**
     * Current weather for a fixed city (PRD §F2/F4). {@code units} defaults to metric (°C).
     */
    @GET
    @Path("/current")
    public OpenWeatherMapResponse current(@RestQuery("cityId") int cityId,
                                          @RestQuery("units") @DefaultValue("metric") String units) {
        return weatherService.getCurrentWeatherById(cityId, units);
    }

    /**
     * 5-day / 3-hour forecast for a fixed city (PRD §F3/F4). {@code units} defaults to metric (°C).
     */
    @GET
    @Path("/forecast")
    public ForecastResponse forecast(@RestQuery("cityId") int cityId,
                                     @RestQuery("units") @DefaultValue("metric") String units) {
        return weatherService.getForecastById(cityId, units);
    }
}

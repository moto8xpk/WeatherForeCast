package org.openweather.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.openweather.domain.WeatherSummary;
import org.openweather.service.SummarizeTodayUseCase;

@Path("/ai")
@Produces(MediaType.APPLICATION_JSON)
public class AiResource {

    private final SummarizeTodayUseCase summarizeToday;

    @Inject
    public AiResource(SummarizeTodayUseCase summarizeToday) {
        this.summarizeToday = summarizeToday;
    }

    /**
     * Ví dụ:
     * GET /ai/summary?city=Ho%20Chi%20Minh&lang=vi
     */
    @GET
    @Path("/summary")
    public WeatherSummary summary(@QueryParam("city") @DefaultValue("") String city,
                                  @QueryParam("lang") @DefaultValue("vi") String lang) {
        String c = city.isBlank() ? null : city;
        return summarizeToday.handle(c, lang);
    }
}


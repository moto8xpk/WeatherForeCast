package org.openweather.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.openweather.builder.WeatherContextBuilder;
import org.openweather.domain.WeatherSummary;
import org.openweather.infra.AiSummarizerPort;


@ApplicationScoped
public class SummarizeTodayUseCase {

    @Inject WeatherService weatherService;
    @Inject AiSummarizerPort ai;

    public WeatherSummary handle(String city, String lang) {
        var cur = weatherService.getCurWeather(city);
        var ctx = WeatherContextBuilder.fromCurrent(cur, (city == null || city.isBlank()) ? "Default" : city);
        return ai.summarize(ctx, (city == null || city.isBlank()) ? "Default" : city, (lang == null || lang.isBlank()) ? "vi" : lang);
    }
}


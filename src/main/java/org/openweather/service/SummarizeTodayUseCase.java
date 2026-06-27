package org.openweather.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.openweather.builder.WeatherContextBuilder;
import org.openweather.domain.WeatherSummary;
import org.openweather.infra.AiSummarizerPort;


@ApplicationScoped
public class SummarizeTodayUseCase {

    private final WeatherService weatherService;
    private final AiSummarizerPort ai;

    @Inject
    public SummarizeTodayUseCase(WeatherService weatherService, AiSummarizerPort ai) {
        this.weatherService = weatherService;
        this.ai = ai;
    }

    public WeatherSummary handle(String city, String lang) {
        String resolvedCity = (city == null || city.isBlank()) ? "Default" : city;
        String resolvedLang = (lang == null || lang.isBlank()) ? "vi" : lang;

        var cur = weatherService.getCurWeather(city);
        var ctx = WeatherContextBuilder.fromCurrent(cur, resolvedCity);
        return ai.summarize(ctx, resolvedCity, resolvedLang);
    }
}


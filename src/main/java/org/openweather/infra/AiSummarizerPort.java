package org.openweather.infra;

import org.openweather.domain.WeatherContext;
import org.openweather.domain.WeatherSummary;

public interface AiSummarizerPort {
    WeatherSummary summarize(WeatherContext ctx, String city, String lang);
}

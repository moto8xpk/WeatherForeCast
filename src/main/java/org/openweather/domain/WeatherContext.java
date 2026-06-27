package org.openweather.domain;

import java.time.Instant;
import java.util.List;

public record WeatherContext(
        String source,
        String city,
        double lat,
        double lon,
        Instant timestamp,
        double temp,
        double feelsLike,
        double humidity,
        double uvIndex,
        double windSpeed,
        double rain1h,
        String description,
        List<ForecastHour> hourly
) {
    public record ForecastHour(Instant dt, double temp, double rain, double wind, String desc) {}
}

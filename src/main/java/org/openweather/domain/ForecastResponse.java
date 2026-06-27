package org.openweather.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response of OpenWeather {@code GET /data/2.5/forecast} (PRD §F3). Free tier returns ~40 entries
 * spaced 3 hours apart across the next 5 days — NOT a true hourly forecast.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForecastResponse {

    public String cod;
    public Integer message;
    public Integer cnt;
    public List<ForecastItem> list;
    public ForecastCity city;
}

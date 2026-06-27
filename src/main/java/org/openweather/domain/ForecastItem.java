package org.openweather.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * A single 3-hour forecast slot. {@code dt} is a UTC epoch (seconds); {@code dtTxt} is the
 * human-readable UTC timestamp (e.g. "2026-06-27 09:00:00").
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForecastItem implements Serializable {

    public Long dt;
    public MainDomain main;
    public List<WeatherDomain> weather;
    public WindDomain wind;
    public CloudsDomain clouds;
    public Integer visibility;
    @JsonProperty("dt_txt")
    public String dtTxt;
}

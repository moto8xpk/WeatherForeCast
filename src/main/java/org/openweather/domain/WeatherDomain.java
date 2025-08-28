package org.openweather.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherDomain {
    public Long id;
    public String main;
    public String description;
    public String icon;
}

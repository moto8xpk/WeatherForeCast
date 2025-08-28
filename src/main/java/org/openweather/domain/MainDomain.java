package org.openweather.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MainDomain {
    public Double temp;
    public Double feelsLike;
    public Double tempMin;
    public Double tempMax;
    public Integer pressure;
    public Integer humidity;
    public Integer seaLevel;
    public Integer grndLevel;
}

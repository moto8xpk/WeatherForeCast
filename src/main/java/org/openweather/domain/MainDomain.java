package org.openweather.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainDomain {
    public Double temp;
    @JsonProperty("feels_like")
    public Double feelsLike;
    @JsonProperty("temp_min")
    public Double tempMin;
    @JsonProperty("temp_max")
    public Double tempMax;
    public Integer pressure;
    public Integer humidity;
    @JsonProperty("sea_level")
    public Integer seaLevel;
    @JsonProperty("grnd_level")
    public Integer grndLevel;
}

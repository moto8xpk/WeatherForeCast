package org.openweather.domain;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class OpenWeatherMapResponse implements Serializable {

    public CoordDomain coord;
    public List<WeatherDomain> weather;
    public String base;
    public MainDomain main;
    public Integer visibility;
    public WindDomain wind;
    public CloudsDomain clouds;
    public Long dt;
    public SysDomain sys;
    public Integer timezone;
    public Integer id;
    public String name;
    public Integer cod;
}


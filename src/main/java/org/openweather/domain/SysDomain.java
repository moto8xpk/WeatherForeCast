package org.openweather.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SysDomain {
    public Integer type;
    public Integer id;
    public String country;
    public Long sunrise;
    public Long sunset;
}

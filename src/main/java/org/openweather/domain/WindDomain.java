package org.openweather.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WindDomain {
    public Double speed;
    public Integer deg;
}

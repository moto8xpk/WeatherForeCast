package org.openweather.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CoordDomain {
    public Double lon;
    public Double lat;
}

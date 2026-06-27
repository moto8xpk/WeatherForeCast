package org.openweather.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * City block of the forecast response. {@code timezone} is the shift in seconds from UTC, used by the
 * client to render the 3-hour slots in local time (PRD §8).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForecastCity {

    public Integer id;
    public String name;
    public CoordDomain coord;
    public String country;
    public Integer timezone;
    public Long sunrise;
    public Long sunset;
}

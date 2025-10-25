package org.openweather.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "open_weather_map_response", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenWeatherMapResponseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coord_lon")
    private Double lon;

    @Column(name = "coord_lat")
    private Double lat;

    // station get weather record
    private String base;

    // temperature
    @Column(name = "main_temp")
    private Double mainTemp;

    // temperature feel like
    @Column(name = "main_feels_like")
    private Double mainFeelsLike;

    // temperature min
    @Column(name = "main_temp_min")
    private Double mainTempMin;

    // temperature max
    @Column(name = "main_temp_max")
    private Double mainTempMax;

    @Column(name = "main_pressure")
    private Long mainPressure;

    @Column(name = "main_humidity")
    private Long mainHumidity;

    @Column(name = "main_sea_level")
    private Long mainSeaLevel;

    @Column(name = "main_grnd_level")
    private Long mainGrndLevel;

    @Column(nullable = false)
    private Long visibility;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "wind_deg")
    private Long windDeg;

    @Column(name = "clouds_all")
    private Long cloudsAll;

    @Column(nullable = false)
    private Long dt;

    @Column(name = "sys_type")
    private Long sysType;

    @Column(name = "sys_id")
    private Long sysId;

    @Column(name = "sys_country")
    private String sysCountry;

    @Column(name = "sys_sunrise")
    private Long sysSunrise;

    @Column(name = "sys_sunset")
    private Long sysSunset;

    @Column(nullable = false)
    private Long timezone;

    @Column(name = "city_id")
    private Long cityId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long cod;

    @OneToMany(mappedBy = "openWeatherMapResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeatherDomainEntity> weather;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

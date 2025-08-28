package org.openweather.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "weather_domain", schema = "public")
@Data
public class WeatherDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weather_id")
    private Long weatherId;

    @Column
    private String main;

    @Column
    private String description;

    @Column
    private String icon;

    @ManyToOne
    @JoinColumn(name = "open_weather_map_response_id")
    private OpenWeatherMapResponseEntity openWeatherMapResponse;

    public Long getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(Long weatherId) {
        this.weatherId = weatherId;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

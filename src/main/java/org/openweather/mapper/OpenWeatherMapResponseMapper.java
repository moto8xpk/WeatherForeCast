package org.openweather.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.model.OpenWeatherMapResponseEntity;

@Mapper
public interface OpenWeatherMapResponseMapper {

    OpenWeatherMapResponseMapper INSTANCE = Mappers.getMapper(OpenWeatherMapResponseMapper.class);

    @Mappings({
            @Mapping(target = "lon", source = "response.coord.lon"),
            @Mapping(target = "lat", source = "response.coord.lat"),
            @Mapping(target = "mainTemp", source = "main.temp"),
            @Mapping(target = "mainFeelsLike", source = "main.feelsLike"),
            @Mapping(target = "mainTempMin", source = "main.tempMin"),
            @Mapping(target = "mainTempMax", source = "main.tempMax"),
            @Mapping(target = "mainPressure", source = "main.pressure"),
            @Mapping(target = "mainHumidity", source = "main.humidity"),
            @Mapping(target = "mainSeaLevel", source = "main.seaLevel"),
            @Mapping(target = "mainGrndLevel", source = "main.grndLevel"),
            @Mapping(target = "windSpeed", source = "wind.speed"),
            @Mapping(target = "windDeg", source = "wind.deg"),
            @Mapping(target = "cloudsAll", source = "clouds.all"),
            @Mapping(target = "sysType", source = "sys.type"),
            @Mapping(target = "sysId", source = "sys.id"),
            @Mapping(target = "sysCountry", source = "sys.country"),
            @Mapping(target = "sysSunrise", source = "sys.sunrise"),
            @Mapping(target = "sysSunset", source = "sys.sunset"),
            @Mapping(target = "timezone", source = "timezone"),
            @Mapping(target = "cityId", source = "id"),
            @Mapping(target = "weather", ignore = true),
            @Mapping(target = "createdAt", ignore = true)
    })
    OpenWeatherMapResponseEntity toEntity(OpenWeatherMapResponse response);
}

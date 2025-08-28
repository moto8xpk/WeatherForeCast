package org.openweather.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.openweather.domain.WeatherDomain;
import org.openweather.model.WeatherDomainEntity;

@Mapper
public interface WeatherDomainMapper {

    WeatherDomainMapper INSTANCE = Mappers.getMapper(WeatherDomainMapper.class);

    @Mappings({
//            @Mapping(target = "weatherId", source = "id"),
//            @Mapping(target = "main", source = "main"),
//            @Mapping(target = "icon", source = "icon"),
//            @Mapping(target = "description", source = "description")
    })
    WeatherDomainEntity toEntity(WeatherDomain weatherDomain);
}

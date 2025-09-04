package org.openweather.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.mapper.OpenWeatherMapResponseMapper;
import org.openweather.mapper.WeatherDomainMapper;
import org.openweather.model.OpenWeatherMapResponseEntity;
import org.openweather.model.WeatherDomainEntity;
import org.openweather.repository.OpenWeatherMapResponseEntityRepository;
import org.openweather.repository.WeatherDomainEntityRepository;

import java.util.List;

@ApplicationScoped
public class WeatherDataService {

    @Inject
    OpenWeatherMapResponseEntityRepository openWeatherMapResponseEntityRepository;

    @Inject
    WeatherDomainEntityRepository weatherDomainEntityRepository;

    @Transactional
    public void saveWeatherData(OpenWeatherMapResponse data) {
        OpenWeatherMapResponseEntity entity = openWeatherMapResponseEntityRepository.getEntityManager().merge(OpenWeatherMapResponseMapper.INSTANCE.toEntity(data));
        data.weather.forEach(weather -> {
            WeatherDomainEntity weatherDomain = WeatherDomainMapper.INSTANCE.toEntity(weather);
            weatherDomain.setOpenWeatherMapResponse(entity);
            weatherDomainEntityRepository.getEntityManager().merge(weatherDomain);
        });
    }
}


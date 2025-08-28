package org.openweather.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.openweather.model.WeatherDomainEntity;
import org.openweather.repository.WeatherDomainEntityRepository;

@ApplicationScoped
public class WeatherDomainService {

    @Inject
    private WeatherDomainEntityRepository weatherDomainEntityRepository;

    public WeatherDomainEntity save(WeatherDomainEntity weatherDomainEntity) {
        return weatherDomainEntityRepository.getEntityManager().merge(weatherDomainEntity);
    }
}

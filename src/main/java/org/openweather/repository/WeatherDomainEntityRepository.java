package org.openweather.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.openweather.model.WeatherDomainEntity;

@ApplicationScoped
public class WeatherDomainEntityRepository implements PanacheRepository<WeatherDomainEntity> {
}

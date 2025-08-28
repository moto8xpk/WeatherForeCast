package org.openweather.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.openweather.model.OpenWeatherMapResponseEntity;

@ApplicationScoped
public class OpenWeatherMapResponseEntityRepository implements PanacheRepository<OpenWeatherMapResponseEntity> {


}

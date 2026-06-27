package org.openweather.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Panache repositories carry no custom logic; this just covers their default construction. */
class RepositoryTest {

    @Test
    void repositoriesCanBeInstantiated() {
        assertNotNull(new OpenWeatherMapResponseEntityRepository());
        assertNotNull(new WeatherDomainEntityRepository());
    }
}

package org.openweather.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.openweather.model.WeatherDomainEntity;
import org.openweather.repository.WeatherDomainEntityRepository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class WeatherDomainServiceTest {

    @Test
    void save_mergesEntityViaEntityManager() {
        WeatherDomainService service = new WeatherDomainService();
        WeatherDomainEntityRepository repo = mock(WeatherDomainEntityRepository.class);
        EntityManager em = mock(EntityManager.class);
        service.weatherDomainEntityRepository = repo;

        WeatherDomainEntity entity = new WeatherDomainEntity();
        WeatherDomainEntity merged = new WeatherDomainEntity();
        when(repo.getEntityManager()).thenReturn(em);
        when(em.merge(entity)).thenReturn(merged);

        assertSame(merged, service.save(entity));
        verify(em).merge(entity);
    }
}

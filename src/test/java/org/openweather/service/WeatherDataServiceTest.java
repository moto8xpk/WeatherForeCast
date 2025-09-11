package org.openweather.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.domain.WeatherDomain;
import org.openweather.model.OpenWeatherMapResponseEntity;
import org.openweather.model.WeatherDomainEntity;
import org.openweather.repository.OpenWeatherMapResponseEntityRepository;
import org.openweather.repository.WeatherDomainEntityRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
class WeatherDataServiceTest {

    @Inject
    WeatherDataService service;

    @InjectMock
    OpenWeatherMapResponseEntityRepository openWeatherMapResponseEntityRepository;

    @InjectMock
    WeatherDomainEntityRepository weatherDomainEntityRepository;

    @Test
    void saveWeatherData_shouldMergeParentAndChildren_andSetBackRef() {
        // Arrange
        EntityManager parentEm = mock(EntityManager.class);
        EntityManager childEm  = mock(EntityManager.class);

        when(openWeatherMapResponseEntityRepository.getEntityManager()).thenReturn(parentEm);
        when(weatherDomainEntityRepository.getEntityManager()).thenReturn(childEm);

        OpenWeatherMapResponseEntity mergedEntity = new OpenWeatherMapResponseEntity();
        when(parentEm.merge(any(OpenWeatherMapResponseEntity.class))).thenReturn(mergedEntity);

        OpenWeatherMapResponse dto = new OpenWeatherMapResponse();
        dto.weather = new ArrayList<>();
        var weather_1 = WeatherDomain.builder().build();
        var weather_2 = WeatherDomain.builder().build();
        dto.weather.add(weather_1);
        dto.weather.add(weather_2);

        when(childEm.merge(any(WeatherDomainEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, WeatherDomainEntity.class));

        // Act
        service.saveWeatherData(dto);

        // Assert
        verify(parentEm, times(1)).merge(any(OpenWeatherMapResponseEntity.class));

        ArgumentCaptor<WeatherDomainEntity> childCaptor = ArgumentCaptor.forClass(WeatherDomainEntity.class);
        verify(childEm, times(2)).merge(childCaptor.capture());
        List<WeatherDomainEntity> mergedChildren = childCaptor.getAllValues();

        for (WeatherDomainEntity child : mergedChildren) {
            assertEquals(mergedEntity, child.getOpenWeatherMapResponse(),
                    "Back-reference (openWeatherMapResponse) phải trỏ về entity parent đã merge");
        }
    }

    @Test
    void saveWeatherData_withEmptyWeather_shouldMergeOnlyParent() {
        // Arrange
        EntityManager parentEm = mock(EntityManager.class);
        EntityManager childEm  = mock(EntityManager.class);

        when(openWeatherMapResponseEntityRepository.getEntityManager()).thenReturn(parentEm);
        when(weatherDomainEntityRepository.getEntityManager()).thenReturn(childEm);

        when(parentEm.merge(any(OpenWeatherMapResponseEntity.class)))
                .thenReturn(new OpenWeatherMapResponseEntity());

        OpenWeatherMapResponse dto = new OpenWeatherMapResponse();
        dto.weather = List.of();

        // Act
        service.saveWeatherData(dto);

        // Assert
        verify(parentEm, times(1)).merge(any(OpenWeatherMapResponseEntity.class));
        verify(childEm, never()).merge(any(WeatherDomainEntity.class));
    }
}

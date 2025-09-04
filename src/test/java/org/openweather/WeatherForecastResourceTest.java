package org.openweather;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;

import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.service.WeatherService;

@QuarkusTest
public class WeatherForecastResourceTest {

    @InjectMock
    WeatherService weatherService;

    @Test
    void getWeatherForecast_shouldReturn200_andCallServiceWithLocation() {
        // Arrange
        String location = "Ho Chi Minh";
        OpenWeatherMapResponse stub = new OpenWeatherMapResponse();
        Mockito.when(weatherService.getCurWeather(eq(location))).thenReturn(stub);

        // Act + Assert
        given()
                .queryParam("location", location)
                .when()
                .get("/weatherForecast/v1/location")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(notNullValue());

        // Verify service
        Mockito.verify(weatherService).getCurWeather(eq(location));
        Mockito.verifyNoMoreInteractions(weatherService);
    }

    @Test
    void getWeatherForecast_whenServiceThrows_shouldPropagateAs5xx() {
        String location = "Hanoi";
        Mockito.when(weatherService.getCurWeather(eq(location)))
                .thenThrow(new RuntimeException("Service error"));

        given()
                .queryParam("location", location)
                .when()
                .get("/weatherForecast/v1/location")
                .then()
                .statusCode(500);
    }
}

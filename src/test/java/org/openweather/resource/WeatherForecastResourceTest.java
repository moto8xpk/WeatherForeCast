package org.openweather.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;

import org.openweather.domain.City;
import org.openweather.domain.ForecastResponse;
import org.openweather.domain.InvalidUnitsException;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.domain.UnknownCityException;
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

    @Test
    void cities_returnsThreeFixedCities() {
        given()
                .when()
                .get("/weatherForecast/v1/cities")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cityId", hasSize(3));
    }

    @Test
    void current_defaultsUnitsToMetric_andCallsService() {
        int cityId = City.HCM.getCityId();
        Mockito.when(weatherService.getCurrentWeatherById(eq(cityId), eq("metric")))
                .thenReturn(new OpenWeatherMapResponse());

        given()
                .queryParam("cityId", cityId)
                .when()
                .get("/weatherForecast/v1/current")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(notNullValue());

        Mockito.verify(weatherService).getCurrentWeatherById(eq(cityId), eq("metric"));
    }

    @Test
    void forecast_passesUnitsThrough() {
        int cityId = City.HA_NOI.getCityId();
        Mockito.when(weatherService.getForecastById(eq(cityId), eq("imperial")))
                .thenReturn(new ForecastResponse());

        given()
                .queryParam("cityId", cityId)
                .queryParam("units", "imperial")
                .when()
                .get("/weatherForecast/v1/forecast")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        Mockito.verify(weatherService).getForecastById(eq(cityId), eq("imperial"));
    }

    @Test
    void current_unknownCity_returns404() {
        Mockito.when(weatherService.getCurrentWeatherById(eq(999), eq("metric")))
                .thenThrow(new UnknownCityException(999));

        given()
                .queryParam("cityId", 999)
                .when()
                .get("/weatherForecast/v1/current")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON);
    }

    @Test
    void current_invalidUnits_returns400() {
        int cityId = City.DA_NANG.getCityId();
        Mockito.when(weatherService.getCurrentWeatherById(eq(cityId), eq("kelvin")))
                .thenThrow(new InvalidUnitsException("kelvin"));

        given()
                .queryParam("cityId", cityId)
                .queryParam("units", "kelvin")
                .when()
                .get("/weatherForecast/v1/current")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON);
    }
}

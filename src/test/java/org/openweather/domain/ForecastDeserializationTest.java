package org.openweather.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Verifies the forecast DTOs bind the OpenWeather /forecast payload, including snake_case fields. */
class ForecastDeserializationTest {

    private static final String SAMPLE = """
            {
              "cod": "200",
              "message": 0,
              "cnt": 1,
              "list": [
                {
                  "dt": 1719468000,
                  "main": { "temp": 30.5, "feels_like": 36.2, "temp_min": 30.5, "temp_max": 31.0, "humidity": 74 },
                  "weather": [ { "id": 500, "main": "Rain", "description": "light rain", "icon": "10d" } ],
                  "wind": { "speed": 3.4, "deg": 200 },
                  "clouds": { "all": 40 },
                  "visibility": 10000,
                  "dt_txt": "2026-06-27 09:00:00"
                }
              ],
              "city": { "id": 1581130, "name": "Hanoi", "country": "VN", "timezone": 25200 }
            }
            """;

    @Test
    void deserializesForecastPayload() throws Exception {
        ForecastResponse resp = new ObjectMapper().readValue(SAMPLE, ForecastResponse.class);

        assertEquals("200", resp.cod);
        assertEquals(1, resp.cnt);
        assertEquals(1, resp.list.size());

        ForecastItem item = resp.list.get(0);
        assertEquals(1719468000L, item.dt);
        assertEquals("2026-06-27 09:00:00", item.dtTxt);
        assertEquals(30.5, item.main.temp);
        assertEquals(36.2, item.main.feelsLike);
        assertEquals(31.0, item.main.tempMax);
        assertEquals(74, item.main.humidity);
        assertEquals("10d", item.weather.get(0).icon);
        assertEquals(3.4, item.wind.speed);

        assertEquals(1581130, resp.city.id);
        assertEquals(25200, resp.city.timezone);
    }
}

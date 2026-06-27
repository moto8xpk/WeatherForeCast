package org.openweather.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openweather.domain.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeatherContextBuilderTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void fromCurrent_withFullData_mapsEveryField() {
        OpenWeatherMapResponse r = new OpenWeatherMapResponse();
        CoordDomain coord = new CoordDomain();
        coord.setLon(106.0);
        coord.setLat(10.0);
        r.coord = coord;
        r.main = MainDomain.builder().temp(30.0).feelsLike(33.0).humidity(70).build();
        r.wind = WindDomain.builder().speed(4.5).build();
        r.weather = List.of(WeatherDomain.builder().description("clear sky").build());
        r.dt = 1_700_000_000L;

        WeatherContext ctx = WeatherContextBuilder.fromCurrent(r, "Ho Chi Minh");

        assertEquals("openweathermap-current", ctx.source());
        assertEquals("Ho Chi Minh", ctx.city());
        assertEquals(106.0, ctx.lon());
        assertEquals(10.0, ctx.lat());
        assertEquals(30.0, ctx.temp());
        assertEquals(33.0, ctx.feelsLike());
        assertEquals(70.0, ctx.humidity());
        assertEquals(4.5, ctx.windSpeed());
        assertEquals("clear sky", ctx.description());
        assertEquals(Instant.ofEpochSecond(1_700_000_000L), ctx.timestamp());
        assertTrue(Double.isNaN(ctx.uvIndex()));
        assertTrue(ctx.hourly().isEmpty());
    }

    @Test
    void fromCurrent_withAllNulls_usesDefaultsAndNaN() {
        OpenWeatherMapResponse r = new OpenWeatherMapResponse(); // every field null

        WeatherContext ctx = WeatherContextBuilder.fromCurrent(r, null);

        assertEquals("N/A", ctx.city());
        assertTrue(Double.isNaN(ctx.lat()));
        assertTrue(Double.isNaN(ctx.lon()));
        assertTrue(Double.isNaN(ctx.temp()));
        assertTrue(Double.isNaN(ctx.feelsLike()));
        assertTrue(Double.isNaN(ctx.humidity()));
        assertEquals(0.0, ctx.windSpeed());
        assertEquals("", ctx.description());
        assertNotNull(ctx.timestamp());
    }

    @Test
    void fromCurrent_withBlankCity_andNullFirstWeather_isHandled() {
        OpenWeatherMapResponse r = new OpenWeatherMapResponse();
        List<WeatherDomain> weather = new ArrayList<>();
        weather.add(null);
        r.weather = weather;
        CoordDomain coord = new CoordDomain(); // lat/lon null
        r.coord = coord;
        r.main = MainDomain.builder().build(); // temp/feelsLike/humidity null
        r.wind = WindDomain.builder().build(); // speed null

        WeatherContext ctx = WeatherContextBuilder.fromCurrent(r, "  ");

        assertEquals("N/A", ctx.city());
        assertEquals("", ctx.description());
        assertEquals(0.0, ctx.windSpeed());
        assertTrue(Double.isNaN(ctx.temp()));
    }

    @Test
    void fromCurrent_withNullDescription_isCoercedToEmpty() {
        OpenWeatherMapResponse r = new OpenWeatherMapResponse();
        // weather element present but its description is null -> safe(null) -> ""
        r.weather = List.of(WeatherDomain.builder().build());

        WeatherContext ctx = WeatherContextBuilder.fromCurrent(r, "City");

        assertEquals("", ctx.description());
    }

    @Test
    void fromCurrent_withEmptyWeatherList_givesEmptyDescription() {
        OpenWeatherMapResponse r = new OpenWeatherMapResponse();
        r.weather = List.of(); // non-null but empty -> !isEmpty() branch is false

        WeatherContext ctx = WeatherContextBuilder.fromCurrent(r, "City");

        assertEquals("", ctx.description());
    }

    @Test
    void todayFromOneCall_withWeatherEmptyArray_usesEmptyDescription() throws Exception {
        ZoneId zone = ZoneId.of("UTC");
        long now = Instant.now().getEpochSecond();

        // weather present, is array, but size 0 -> last sub-condition false
        String json = """
            {
              "current": { "dt": %d, "weather": [] },
              "hourly": [ { "dt": %d, "weather": [] } ]
            }
            """.formatted(now, now);

        // blank (non-null) city -> "N/A" branch of the city ternary
        var ctx = WeatherContextBuilder.todayFromOneCall(mapper.readTree(json), "  ", 0, 0, zone);
        assertEquals("N/A", ctx.city());
        assertEquals("", ctx.description());
        assertEquals("", ctx.hourly().get(0).desc());
    }

    @Test
    void todayFromOneCall_withWeatherNotArray_usesEmptyDescription() throws Exception {
        ZoneId zone = ZoneId.of("UTC");
        long now = Instant.now().getEpochSecond();

        // weather present but NOT an array -> isArray() branch is false
        String json = """
            {
              "current": { "dt": %d, "weather": {} },
              "hourly": [ { "dt": %d, "weather": "x" } ]
            }
            """.formatted(now, now);

        var ctx = WeatherContextBuilder.todayFromOneCall(mapper.readTree(json), "C", 0, 0, zone);
        assertEquals("", ctx.description());
        assertEquals("", ctx.hourly().get(0).desc());
    }

    @Test
    void todayFromOneCall_keepsOnlyTodayHours() throws Exception {
        ZoneId zone = ZoneId.of("UTC");
        long now = Instant.now().getEpochSecond();
        long yesterday = now - 60 * 60 * 24;

        String json = """
            {
              "current": {
                "dt": %d,
                "temp": 28.0,
                "feels_like": 30.0,
                "humidity": 65,
                "uvi": 7.0,
                "wind_speed": 3.0,
                "rain": { "1h": 1.2 },
                "weather": [ { "description": "light rain" } ]
              },
              "hourly": [
                { "dt": %d, "temp": 27.0, "rain": { "1h": 0.5 }, "wind_speed": 2.0, "weather": [ { "description": "cloudy" } ] },
                { "dt": %d, "temp": 20.0 }
              ]
            }
            """.formatted(now, now, yesterday);

        var node = mapper.readTree(json);
        WeatherContext ctx = WeatherContextBuilder.todayFromOneCall(node, "Hanoi", 21.0, 105.8, zone);

        assertEquals("openweather-onecall", ctx.source());
        assertEquals("Hanoi", ctx.city());
        assertEquals(28.0, ctx.temp());
        assertEquals(7.0, ctx.uvIndex());
        assertEquals(1.2, ctx.rain1h());
        assertEquals("light rain", ctx.description());
        assertEquals(1, ctx.hourly().size());
        assertEquals("cloudy", ctx.hourly().get(0).desc());
    }

    @Test
    void todayFromOneCall_withMissingOptionalFields_usesDefaults() throws Exception {
        ZoneId zone = ZoneId.of("UTC");
        long now = Instant.now().getEpochSecond();

        String json = """
            {
              "current": { "dt": %d },
              "hourly": [ { "dt": %d } ]
            }
            """.formatted(now, now);

        var node = mapper.readTree(json);
        WeatherContext ctx = WeatherContextBuilder.todayFromOneCall(node, null, 0, 0, zone);

        assertEquals("N/A", ctx.city());
        assertEquals("", ctx.description());
        assertEquals(0.0, ctx.windSpeed());
        assertEquals(0.0, ctx.rain1h());
        assertEquals(1, ctx.hourly().size());
        assertEquals("", ctx.hourly().get(0).desc());
    }
}

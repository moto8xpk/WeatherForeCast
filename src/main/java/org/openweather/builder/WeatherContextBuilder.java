package org.openweather.builder;

import com.fasterxml.jackson.databind.JsonNode;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.domain.WeatherContext;
import org.openweather.domain.WeatherDomain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Chuyển dữ liệu OpenWeather → WeatherContext gọn để đưa vào AI/use-case.
 * - fromCurrent(...)  : dùng response CURRENT WEATHER (API mà bạn đang gọi)
 * - fromOneCall(... ) : dùng JSON One Call 3.0 (nếu sau này chuyển)
 */
public final class WeatherContextBuilder {

    private static final String WEATHER = "weather";
    private static final String DEFAULT_CITY = "N/A";

    private WeatherContextBuilder() {}

    /** Build từ CURRENT WEATHER (service của bạn đang dùng). */
    public static WeatherContext fromCurrent(OpenWeatherMapResponse r, String city) {
        var coord = r.getCoord();
        double lat = coord != null ? orNaN(coord.getLat()) : Double.NaN;
        double lon = coord != null ? orNaN(coord.getLon()) : Double.NaN;

        var main = r.getMain();
        double temp = main != null ? orNaN(main.getTemp()) : Double.NaN;
        double feelsLike = main != null ? orNaN(main.getFeelsLike()) : Double.NaN;
        double humidity = main != null ? orNaN(main.getHumidity()) : Double.NaN;

        var wind = r.getWind();
        double windSpeed = wind != null ? orZero(wind.getSpeed()) : 0.0;

        long epoch = r.getDt() != null ? r.getDt() : System.currentTimeMillis() / 1000;

        return new WeatherContext(
                "openweathermap-current",
                resolveCity(city),
                lat,
                lon,
                Instant.ofEpochSecond(epoch),
                temp,
                feelsLike,
                humidity,
                Double.NaN,         // current API không có UV
                windSpeed,
                Double.NaN,
                firstDescription(r.getWeather()),
                List.of()           // current không có hourly
        );
    }

    /** Build từ ONE CALL 3.0 (nếu bạn dùng raw JSON từ client One Call). */
    public static WeatherContext todayFromOneCall(JsonNode oneCall, String city, double lat, double lon, ZoneId zone) {
        var today = LocalDate.now(zone);

        // current
        JsonNode cur = oneCall.path("current");
        double temp      = cur.path("temp").asDouble(Double.NaN);
        double feelsLike = cur.path("feels_like").asDouble(Double.NaN);
        double humidity  = cur.path("humidity").asDouble(Double.NaN);
        double uvIndex   = cur.path("uvi").asDouble(Double.NaN);
        double windSpeed = cur.path("wind_speed").asDouble(0.0);
        double rain1h    = cur.path("rain").path("1h").asDouble(0.0);
        String desc      = descriptionOf(cur);

        long epoch       = cur.path("dt").asLong(System.currentTimeMillis() / 1000);
        Instant ts       = Instant.ofEpochSecond(epoch);

        // hourly chỉ lấy các giờ thuộc "hôm nay"
        List<WeatherContext.ForecastHour> hours = new ArrayList<>();
        oneCall.path("hourly").forEach(h -> {
            Instant dt = Instant.ofEpochSecond(h.path("dt").asLong());
            if (LocalDate.ofInstant(dt, zone).equals(today)) {
                double hTemp = h.path("temp").asDouble(Double.NaN);
                double hRain = h.path("rain").path("1h").asDouble(0.0);
                double hWind = h.path("wind_speed").asDouble(0.0);
                hours.add(new WeatherContext.ForecastHour(dt, hTemp, hRain, hWind, descriptionOf(h)));
            }
        });

        return new WeatherContext(
                "openweather-onecall",
                resolveCity(city),
                lat,
                lon,
                ts,
                temp,
                feelsLike,
                humidity,
                uvIndex,
                windSpeed,
                rain1h,
                desc,
                hours
        );
    }

    private static double orNaN(Number v) {
        return v != null ? v.doubleValue() : Double.NaN;
    }

    private static double orZero(Number v) {
        return v != null ? v.doubleValue() : 0.0;
    }

    private static String resolveCity(String city) {
        return city != null && !city.isBlank() ? city : DEFAULT_CITY;
    }

    /** Description from the first element of a CURRENT-WEATHER weather list. */
    private static String firstDescription(List<WeatherDomain> weather) {
        if (weather == null || weather.isEmpty() || weather.get(0) == null) {
            return "";
        }
        return safe(weather.get(0).getDescription());
    }

    /** Description from a One Call node's "weather" array. */
    private static String descriptionOf(JsonNode node) {
        JsonNode weather = node.path(WEATHER);
        if (weather.isArray() && !weather.isEmpty()) {
            return safe(weather.get(0).path("description").asText(""));
        }
        return "";
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}

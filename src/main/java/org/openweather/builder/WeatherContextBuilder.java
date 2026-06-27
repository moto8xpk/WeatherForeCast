package org.openweather.builder;

import com.fasterxml.jackson.databind.JsonNode;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.domain.WeatherContext;

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

    private WeatherContextBuilder() {}

    /** Build từ CURRENT WEATHER (service của bạn đang dùng). */
    public static WeatherContext fromCurrent(OpenWeatherMapResponse r, String city) {
        double lat = (r.getCoord() != null && r.getCoord().getLat() != null) ? r.getCoord().getLat() : Double.NaN;
        double lon = (r.getCoord() != null && r.getCoord().getLon() != null) ? r.getCoord().getLon() : Double.NaN;

        double temp      = r.getMain() != null && r.getMain().getTemp() != null ? r.getMain().getTemp() : Double.NaN;
        double feelsLike = r.getMain() != null && r.getMain().getFeelsLike() != null ? r.getMain().getFeelsLike() : Double.NaN;
        double humidity  = r.getMain() != null && r.getMain().getHumidity() != null ? r.getMain().getHumidity() : Double.NaN;

        double windSpeed = r.getWind() != null && r.getWind().getSpeed() != null ? r.getWind().getSpeed() : 0.0;

        String desc = "";
        if (r.getWeather() != null && !r.getWeather().isEmpty() && r.getWeather().get(0) != null) {
            desc = safe(r.getWeather().get(0).getDescription());
        }

        long epoch = r.getDt() != null ? r.getDt() : System.currentTimeMillis() / 1000;
        Instant ts = Instant.ofEpochSecond(epoch);

        return new WeatherContext(
                "openweathermap-current",
                city != null && !city.isBlank() ? city : "N/A",
                lat,
                lon,
                ts,
                temp,
                feelsLike,
                humidity,
                Double.NaN,         // current API không có UV
                windSpeed,
                Double.NaN,
                desc,
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
        String desc      = cur.has("weather") && cur.path("weather").isArray() && cur.path("weather").size() > 0
                ? safe(cur.path("weather").get(0).path("description").asText(""))
                : "";

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
                String hDesc = (h.has("weather") && h.path("weather").isArray() && h.path("weather").size() > 0)
                        ? safe(h.path("weather").get(0).path("description").asText(""))
                        : "";
                hours.add(new WeatherContext.ForecastHour(dt, hTemp, hRain, hWind, hDesc));
            }
        });

        return new WeatherContext(
                "openweather-onecall",
                city != null && !city.isBlank() ? city : "N/A",
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

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}

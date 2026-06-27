package org.openweather.domain;

import java.util.Arrays;

/**
 * The 3 fixed cities supported by the MVP (PRD §F1). OpenWeather city IDs are the single source of
 * truth — name lookups are avoided to prevent mis-identification.
 */
public enum City {

    HCM(1566083, "TP. Hồ Chí Minh"),
    DA_NANG(1583992, "Đà Nẵng"),
    HA_NOI(1581130, "Hà Nội");

    private final int cityId;
    private final String displayName;

    City(int cityId, String displayName) {
        this.cityId = cityId;
        this.displayName = displayName;
    }

    public int getCityId() {
        return cityId;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * @throws UnknownCityException if the id is not one of the 3 fixed cities (mapped to HTTP 404).
     */
    public static City fromId(int cityId) {
        return Arrays.stream(values())
                .filter(c -> c.cityId == cityId)
                .findFirst()
                .orElseThrow(() -> new UnknownCityException(cityId));
    }
}

package org.openweather.domain;

import java.util.Arrays;
import java.util.List;

/**
 * Lightweight view of a fixed city for the tab/segmented control (PRD §F1).
 */
public record CityInfo(int cityId, String name) {

    public static List<CityInfo> all() {
        return Arrays.stream(City.values())
                .map(c -> new CityInfo(c.getCityId(), c.getDisplayName()))
                .toList();
    }
}

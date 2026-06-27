package org.openweather.domain;

import java.util.List;

public record WeatherSummary(
        String headline,
        String brief,
        List<String> tips,
        String riskLevel) {
}

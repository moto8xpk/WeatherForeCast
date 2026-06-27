package org.openweather.domain;

/**
 * Thrown when a request targets a city id outside the 3 fixed cities. Mapped to HTTP 404 by
 * {@link org.openweather.resource.ApiExceptionMapper}.
 */
public class UnknownCityException extends RuntimeException {

    public UnknownCityException(int cityId) {
        super("Unknown city id: " + cityId + ". Supported cities: 1566083 (HCM), 1583992 (Da Nang), 1581130 (Ha Noi).");
    }
}

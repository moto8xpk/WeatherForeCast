package org.openweather.domain;

/**
 * Thrown when the {@code units} parameter is neither {@code metric} nor {@code imperial}.
 * Mapped to HTTP 400 by {@link org.openweather.resource.ApiExceptionMapper}.
 */
public class InvalidUnitsException extends RuntimeException {

    public InvalidUnitsException(String units) {
        super("Invalid units: '" + units + "'. Allowed values: metric (°C) or imperial (°F).");
    }
}

package org.openweather.domain;

/**
 * Wraps a transient OpenWeather failure (network error / timeout / 5xx) so {@code @Retry} retries it
 * (PRD §F8). 4xx errors are NOT wrapped — they are client errors and must not be retried.
 */
public class RetryableWeatherException extends RuntimeException {

    public RetryableWeatherException(Throwable cause) {
        super(cause);
    }
}

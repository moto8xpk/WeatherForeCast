package org.openweather.infra;

/**
 * Thrown when an AI provider fails to produce a weather summary
 * (serialization, transport, or an empty/invalid response).
 */
public class AiSummarizationException extends RuntimeException {

    public AiSummarizationException(String message) {
        super(message);
    }

    public AiSummarizationException(String message, Throwable cause) {
        super(message, cause);
    }
}

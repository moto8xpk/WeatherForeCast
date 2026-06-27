package org.openweather.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.openweather.domain.InvalidUnitsException;
import org.openweather.domain.UnknownCityException;

import java.util.Map;

/**
 * Translates request-validation failures into clean JSON errors instead of a generic 500, so the
 * client can render the right message (PRD §F5, backend side). Mappers are scoped to the specific
 * exception types — unrelated failures keep propagating as 500.
 */
public final class ApiExceptionMapper {

    private ApiExceptionMapper() {
    }

    static Response error(Response.Status status, String message) {
        return Response.status(status)
                .entity(Map.of("error", message, "status", status.getStatusCode()))
                .build();
    }

    /** Unknown city id → 404. */
    @Provider
    public static class UnknownCityMapper implements ExceptionMapper<UnknownCityException> {
        @Override
        public Response toResponse(UnknownCityException exception) {
            return error(Response.Status.NOT_FOUND, exception.getMessage());
        }
    }

    /** Invalid units → 400. */
    @Provider
    public static class InvalidUnitsMapper implements ExceptionMapper<InvalidUnitsException> {
        @Override
        public Response toResponse(InvalidUnitsException exception) {
            return error(Response.Status.BAD_REQUEST, exception.getMessage());
        }
    }
}

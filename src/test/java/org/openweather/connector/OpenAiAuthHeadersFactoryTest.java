package org.openweather.connector;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenAiAuthHeadersFactoryTest {

    @Test
    void update_addsAuthorizationAndContentTypeHeaders() {
        OpenAiAuthHeadersFactory factory = new OpenAiAuthHeadersFactory();
        factory.apiKey = "secret-key";

        MultivaluedMap<String, String> in = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> out = new MultivaluedHashMap<>();

        MultivaluedMap<String, String> headers = factory.update(in, out);

        assertEquals("Bearer secret-key", headers.getFirst("Authorization"));
        assertEquals("application/json", headers.getFirst("Content-Type"));
    }
}

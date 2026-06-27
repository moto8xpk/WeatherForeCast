package org.openweather.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Covers the hand-written (non-Lombok, non-record-generated) members of the domain types. */
class DomainTest {

    @Test
    void message_factoryMethods_setRole() {
        assertEquals("system", Message.system("a").role());
        assertEquals("a", Message.system("a").content());
        assertEquals("user", Message.user("b").role());
        assertEquals("assistant", Message.assistant("c").role());
    }

    @Test
    void responseFormat_jsonObjectConstant() {
        assertEquals("json_object", ChatCompletionRequest.ResponseFormat.JSON_OBJECT.type());
    }

    @Test
    void cloudsDomain_constructorAndAccessors() {
        CloudsDomain clouds = new CloudsDomain(40);
        assertEquals(40, clouds.getAll());
        clouds.setAll(75);
        assertEquals(75, clouds.getAll());
    }
}

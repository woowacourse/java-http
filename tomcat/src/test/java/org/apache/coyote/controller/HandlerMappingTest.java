package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.coyote.http.HttpMethod;
import org.junit.jupiter.api.Test;


public class HandlerMappingTest {

    static {
        SingletonContainer.registerSingletons();
    }

    @Test
    void findFromUri() {
        final HandlerMapping handlerMapping = SingletonContainer.getObject(HandlerMapping.class);
        assertAll(
                assertThat(handlerMapping.findFromUri("/", HttpMethod.GET))::isNotNull,
                assertThat(handlerMapping.findFromUri("/login", HttpMethod.POST))::isNotNull,
                () -> assertThrows(IllegalArgumentException.class,
                        () -> handlerMapping.findFromUri("/login", HttpMethod.GET)),
                assertThat(handlerMapping.findFromUri("/register", HttpMethod.POST))::isNotNull
        );
    }
}

package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.apache.coyote.http.HttpMethod;
import org.junit.jupiter.api.Test;

class SingletonContainerTest {

    static {
        SingletonContainer.registerSingletons();
    }

    @Test
    void getAllObjects() {
        final Map<Class<?>, Object> allObjects = SingletonContainer.getAllObjects();
        assertThat(allObjects).isNotNull();
    }
}
package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.coyote.http.HttpMethod;
import org.junit.jupiter.api.Test;

class SingletonContainerTest {

    static {
        SingletonContainer.scanPackage("nextstep.jwp.controller");
    }

    @Test
    void findFromUri() {
        assertAll(
                assertThat(SingletonContainer.findFromUri("/", HttpMethod.GET))::isNotNull,
                assertThat(SingletonContainer.findFromUri("/login", HttpMethod.POST))::isNotNull,
                () -> assertThrows(IllegalArgumentException.class,
                        () -> SingletonContainer.findFromUri("/login", HttpMethod.GET)),
                assertThat(SingletonContainer.findFromUri("/register", HttpMethod.POST))::isNotNull
        );
    }
}
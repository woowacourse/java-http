package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.coyote.http.HttpMethod;
import org.junit.jupiter.api.Test;

class ControllerContainerTest {

    static {
        ControllerContainer.scanPackage("nextstep.jwp.controller");
    }

    @Test
    void findFromUri() {
        assertAll(
                assertThat(ControllerContainer.findFromUri("/", HttpMethod.GET))::isNotNull,
                assertThat(ControllerContainer.findFromUri("/login", HttpMethod.POST))::isNotNull,
                () -> assertThrows(IllegalArgumentException.class,
                        () -> ControllerContainer.findFromUri("/login", HttpMethod.GET)),
                assertThat(ControllerContainer.findFromUri("/register", HttpMethod.POST))::isNotNull
        );
    }
}
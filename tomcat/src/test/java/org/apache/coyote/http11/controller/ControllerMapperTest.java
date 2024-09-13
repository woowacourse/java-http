package org.apache.coyote.http11.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerMapperTest {

    @Test
    @DisplayName("컨트롤러를 찾을 수 있다.")
    void map1() {
        Controller controller = ControllerMapper.map("/login");

        assertThat(controller).isExactlyInstanceOf(LoginController.class);
    }

    @Test
    @DisplayName("컨트롤러를 찾을 수 있다.")
    void map2() {
        Controller controller = ControllerMapper.map("/index.html");

        assertThat(controller).isExactlyInstanceOf(StaticResourceController.class);
    }
}

package org.apache.coyote.processor;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.junit.jupiter.api.Test;

class ControllerMapperTest {

    @Test
    void getController() {
        // given
        ControllerMapper controllerMapper = new ControllerMapper();

        // when
        Controller controller = controllerMapper.getController("/");

        // then
        assertThat(controller.getClass()).isEqualTo(HomeController.class);
    }
}

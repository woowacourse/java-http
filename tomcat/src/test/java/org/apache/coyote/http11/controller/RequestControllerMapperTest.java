package org.apache.coyote.http11.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestControllerMapperTest {

    @Test
    @DisplayName("url에 맞는 Controller를 반환한다.")
    void getController_login() {
        String url = "/login";
        Controller controller = RequestControllerMapper.getController(url);
        assertThat(controller.getClass()).isEqualTo(LoginController.class);
    }

    @Test
    @DisplayName("url에 맞는 Controller를 반환한다.")
    void getController_register() {
        String url = "/register";
        Controller controller = RequestControllerMapper.getController(url);
        assertThat(controller.getClass()).isEqualTo(RegisterController.class);
    }
}

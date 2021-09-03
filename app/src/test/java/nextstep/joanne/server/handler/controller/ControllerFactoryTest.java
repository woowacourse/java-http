package nextstep.joanne.server.handler.controller;

import nextstep.joanne.dashboard.controller.LoginController;
import nextstep.joanne.dashboard.controller.RegisterController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ControllerFactoryTest {

    @DisplayName("uri에 따른 Controller를 등록한다.")
    @Test
    void addControllers() {
        // given
        Map<String, Controller> controllers = ControllerFactory.addControllers();

        // when - then
        assertAll(
                () -> assertThat(controllers).containsKeys("/login", "/register"),
                () -> assertThat(controllers.get("/login")).isInstanceOf(LoginController.class),
                () -> assertThat(controllers.get("/register")).isInstanceOf(RegisterController.class)
        );
    }
}
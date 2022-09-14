package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ControllerMappingTest {

    @Test
    void findLoginController() {
        Controller controller = ControllerMapping.findController("/login");

        assertThat(controller.getClass()).isEqualTo(LoginController.class);
    }

    @Test
    void findRegisterController() {
        Controller controller = ControllerMapping.findController("/register");

        assertThat(controller.getClass()).isEqualTo(RegisterController.class);
    }

    @Test
    void findMainController() {
        Controller controller = ControllerMapping.findController("/");

        assertThat(controller.getClass()).isEqualTo(MainController.class);
    }
}

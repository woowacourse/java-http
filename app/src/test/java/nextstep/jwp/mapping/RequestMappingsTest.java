package nextstep.jwp.mapping;

import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.mapper.ControllerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingsTest {

    private final ControllerMapper controllerMapper = ControllerMapper.getInstance();

    @DisplayName("매핑 확인 (/)")
    @Test
    void home() {
        Controller controller = controllerMapper.resolve("/");
        assertThat(controller).isInstanceOf(HelloController.class);
    }

    @DisplayName("매핑 확인 (/login)")
    @Test
    void login() {
        Controller controller = controllerMapper.resolve("/login");
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @DisplayName("매핑 확인 (/register)")
    @Test
    void register() {
        Controller controller = controllerMapper.resolve("/register");
        assertThat(controller).isInstanceOf(RegisterController.class);
    }
}

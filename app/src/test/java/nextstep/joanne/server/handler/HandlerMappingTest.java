package nextstep.joanne.server.handler;

import nextstep.joanne.dashboard.controller.LoginController;
import nextstep.joanne.dashboard.controller.RegisterController;
import nextstep.joanne.server.handler.controller.Controller;
import nextstep.joanne.server.handler.controller.ControllerFactory;
import nextstep.joanne.server.handler.controller.ResourceController;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.request.RequestLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HandlerMappingTest {

    private HandlerMapping handlerMapping;

    @BeforeEach
    void setUp() {
        handlerMapping = new HandlerMapping(ControllerFactory.addControllers());
    }

    @DisplayName("HandlerMapping에서 url로 Controller를 찾는다. - index.html")
    @Test
    void getWhenIndex() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new RequestLine(null, "/index", null),
                null,
                null
        );

        // when
        Controller controller = handlerMapping.get(httpRequest);

        // then
        assertThat(controller).isInstanceOf(ResourceController.class);
    }

    @DisplayName("HandlerMapping에서 url로 Controller를 찾는다. - login.html")
    @Test
    void getWhenLogin() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new RequestLine(null, "/login", null),
                null,
                null
        );

        // when
        Controller controller = handlerMapping.get(httpRequest);

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @DisplayName("HandlerMapping에서 url로 Controller를 찾는다. - register.html")
    @Test
    void getWhenRegister() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new RequestLine(null, "/register", null),
                null,
                null
        );

        // when
        Controller controller = handlerMapping.get(httpRequest);

        // then
        assertThat(controller).isInstanceOf(RegisterController.class);
    }
}
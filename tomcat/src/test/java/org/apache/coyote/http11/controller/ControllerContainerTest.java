package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.Test;

class ControllerContainerTest {

    @Test
    void find() {
        HomeController homeController = new HomeController();
        LoginController loginController = new LoginController();
        RegisterController registerController = new RegisterController();

        ControllerContainer controllerContainer = new ControllerContainer(
                List.of(homeController, loginController, registerController));

        RequestLine requestLine = RequestLine.of("GET /login http/1.1");
        Headers headers = Headers.of(List.of("Host: localhost:8080"));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, "");

        assertThat(controllerContainer.find(httpRequest)).isEqualTo(loginController);
    }
}

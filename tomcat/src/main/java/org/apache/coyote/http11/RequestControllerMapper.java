package org.apache.coyote.http11;

import java.util.List;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.apache.coyote.http11.controller.WelcomePageController;
import org.apache.coyote.http11.request_response.request.HttpRequest;

public class RequestControllerMapper {

    private final List<Controller> controllers = List.of(
        new LoginController(),
        new RegisterController(),
        new WelcomePageController(),
        new StaticResourceController()
    );

    public Controller mapController(HttpRequest request) {
        return controllers.stream()
            .filter(controller -> controller.supports(request))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("invalid request %s".formatted(request.getUriPath())));
    }
}

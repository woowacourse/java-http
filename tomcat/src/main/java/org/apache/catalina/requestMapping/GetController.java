package org.apache.catalina.requestMapping;

import java.util.List;
import java.util.Optional;
import org.apache.catalina.controller.DefaultController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.request.HttpRequest;

public class GetController {

    private final List<Controller> controllers;

    public GetController() {
        this.controllers = initControllers();
    }

    public Optional<Controller> findController(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(controller -> controller.canHandle(httpRequest))
                .findFirst();
    }

    private List<Controller> initControllers() {
        return List.of(
                new DefaultController(),
                new StaticResourceController(),
                new LoginController(),
                new RegisterController()
        );
    }
}
